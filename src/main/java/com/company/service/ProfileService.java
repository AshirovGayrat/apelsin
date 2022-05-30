package com.company.service;

import com.company.dto.AttachSimpleDTO;
import com.company.dto.ChangePswdDTO;
import com.company.dto.ProfileRequestDTO;
import com.company.dto.ProfileResponceDto;
import com.company.entity.AttachEntity;
import com.company.entity.ProfileEntity;
import com.company.enums.ProfileRole;
import com.company.enums.ProfileStatus;
import com.company.exp.AppBadRequestException;
import com.company.exp.ItemAlreadyExistsException;
import com.company.exp.ItemNotFoundException;
import com.company.repository.ProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ProfileService {
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private AttachService attachService;

    //Create profile
    public ProfileResponceDto createProfile(ProfileRequestDTO dto) {

        Optional<ProfileEntity> optional = profileRepository.findByPhone(dto.getPhone());
        if (optional.isPresent()) {
            log.warn("email alredy exists : {}", dto);
            throw new ItemAlreadyExistsException("Phone Already Exits");
        }

        ProfileEntity entity = toEntity(dto);
        try {
            profileRepository.save(entity);
        }catch (DataIntegrityViolationException e){
            log.warn("Unique {}", dto);
            throw new AppBadRequestException("Unique Items!");
        }
        return toDTO(entity);
    }

    //Get User pagination list
    public List<ProfileResponceDto> paginationList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        List<ProfileResponceDto> list = new ArrayList<>();
        profileRepository.findAll(pageable).forEach(entity -> {
            list.add(toDTO(entity));
        });
        if (list.isEmpty()) {
            log.warn(" not found : {}",list);
            throw new ItemNotFoundException("Not Found!");
        }
        return list;
    }

    // Get by id
    public ProfileResponceDto getById(String id) {
        ProfileEntity entity = profileRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Not Found!"));
        return toDTO(entity);
    }

    // Update profile
    public ProfileResponceDto updateProfile(String id, ProfileRequestDTO dto) {

        Optional<ProfileEntity> optional = profileRepository.findById(id);
        if (optional.isEmpty()) {
            log.warn("Profile not found : {}", dto);
            throw new ItemNotFoundException("Profile not found!");
        }

        ProfileEntity entity = optional.get();

        entity.setName(dto.getName());
        entity.setSurname(dto.getSurname());
        profileRepository.save(entity);

        return toDTO(entity);
    }

    //delete profile
    public Boolean delete(String id) {
        ProfileEntity entity = get(id);
        if (entity.getVisible()) {
            return 0 < profileRepository.updateVisible(false, id);
        }
        return true;
    }

    // Update profile photo
    public Boolean updateImage(MultipartFile file, String pId) {
        ProfileEntity profileEntity = get(pId);

        AttachEntity attachEntity = new AttachEntity();

        if (profileEntity.getAttach() != null) {
            attachEntity = attachService.updateAttach(file, profileEntity.getAttach().getId());
        } else if (profileEntity.getAttach() == null) {
            attachEntity = attachService.reUploadAttach(file);
        }
        profileEntity.setAttach(attachEntity);

        return true;
    }

    public Boolean deleteImage(String pId) {
        ProfileEntity profileEntity = get(pId);

        if (attachService.delete(profileEntity.getAttach().getId())) {
            profileEntity.setAttach(null);
            return true;
        }
        return false;
    }

    private ProfileResponceDto toDTO(ProfileEntity entity) {
        ProfileResponceDto dto = new ProfileResponceDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setSurname(entity.getSurname());
        dto.setPhone(entity.getPhone());
        dto.setCreatedDate(entity.getCreatedDate());
        if (entity.getAttachId() != null) {
            AttachSimpleDTO attachDTO = new AttachSimpleDTO();
            attachDTO.setAttachId(entity.getAttachId());
            attachDTO.setUrl(attachService.toOpenURL(entity.getAttachId()));
            dto.setAttachDto(attachDTO);
        }
        return dto;
    }

    public ProfileEntity get(String id) {
        return profileRepository.findById(id).orElseThrow(() ->
                new ItemNotFoundException("Profile Not Found!"));
    }

    public ProfileEntity toEntity(ProfileRequestDTO dto) {
        ProfileEntity entity = new ProfileEntity();
        entity.setName(dto.getName());
        entity.setSurname(dto.getSurname());
        entity.setPhone(dto.getPhone());
        entity.setRole(ProfileRole.ADMIN);
        entity.setStatus(ProfileStatus.ACTIVE);
        return entity;
    }
}
