package com.company.service;

import com.company.dto.ProfileDto;
import com.company.dtoRequest.ChangePswdDTO;
import com.company.dtoRequest.UpdateProfileDTO;
import com.company.entity.AttachEntity;
import com.company.entity.ProfileEntity;
import com.company.enums.ProfileStatus;
import com.company.exp.EmailAlreadyExistsException;
import com.company.exp.ItemNotFoundException;
import com.company.repository.ProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    public Boolean changePswd(ChangePswdDTO dto){
        ProfileEntity entity=profileRepository.findByPassword(dto.getPassword()).
                orElseThrow(()-> new ItemNotFoundException("Profile not found"));

        entity.setPassword(dto.getPassword());
        return true;
    }

    //Create profile
    public ProfileDto createProfile(ProfileDto dto) {

        Optional<ProfileEntity> optional = profileRepository.findByEmail(dto.getEmail());
        if (optional.isPresent()) {
            log.warn("email alredy exists : {}", dto );
            throw new EmailAlreadyExistsException("Email Already Exits");
        }

        ProfileEntity entity = toEntity(dto);
        profileRepository.save(entity);
        dto.setId(entity.getId());
        return dto;
    }

    //Get User pagination list
    public List<ProfileDto> paginationList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        List<ProfileDto> list = new ArrayList<>();
        profileRepository.findAll(pageable).forEach(entity -> {
            list.add(toDTO(entity));
        });
        if (list.isEmpty()) {
            log.warn(" not found : {}");
            throw new ItemNotFoundException("Not Found!");
        }
        return list;
    }

    // Get by id
    public ProfileDto getById(Integer id) {
        ProfileEntity entity = profileRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Not Found!"));
        return toDTO(entity);
    }

    // Update profile
    public ProfileDto updateProfile(Integer id, UpdateProfileDTO dto) {

        Optional<ProfileEntity> optional = profileRepository.findById(id);
        if (optional.isEmpty()) {
            log.warn("Profile not found : {}", dto );
            throw new ItemNotFoundException("Profile not found!");
        }

        ProfileEntity entity = optional.get();

        entity.setName(dto.getName());
        entity.setSurname(dto.getSurname());
        profileRepository.save(entity);

        return toDTO(entity);
    }

    //delete profile
    public Boolean delete(Integer id) {
        ProfileEntity entity = profileRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Not Found!"));

        if (entity==null) {
            log.warn("id not found : {}", id );
            throw new ItemNotFoundException("Not Found!");
        }

        int n = profileRepository.updateStatus(ProfileStatus.DELETED, id);
        return n > 0;
    }

    // Update profile photo
    public Boolean updateImage(MultipartFile file, Integer pId) {
        ProfileEntity profileEntity = get(pId);

        AttachEntity attachEntity=new AttachEntity();

        if (profileEntity.getAttach() != null) {
            attachEntity=attachService.updateAttach(file,profileEntity.getAttach().getId());
        } else if (profileEntity.getAttach() == null) {
            attachEntity=attachService.reUploadAttach(file);
        }
        profileEntity.setAttach(attachEntity);

        return true;
    }

    public Boolean deleteImage(Integer pId) {
        ProfileEntity profileEntity = get(pId);

        if (attachService.delete(profileEntity.getAttach().getId())){
            profileEntity.setAttach(null);
            return true;
        }
        return false;
    }

    private ProfileDto toDTO(ProfileEntity entity) {
        ProfileDto dto = new ProfileDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setSurname(entity.getSurname());
        dto.setEmail(entity.getEmail());
        dto.setPassword(entity.getPassword());
        dto.setUpdateDate(entity.getUpdateDate());
        dto.setCreateDate(entity.getCreatedDate());
        if (entity.getAttach()!=null){
            dto.getAttachDto().setAttachId(entity.getAttachId());
            dto.getAttachDto().setUrl(attachService.toOpenURL(entity.getAttachId()));
        }
        return dto;
    }

    public ProfileEntity get(Integer id) {
        return profileRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Not Found!"));
    }

    public ProfileEntity toEntity(ProfileDto dto){
        ProfileEntity entity = new ProfileEntity();
        entity.setName(dto.getName());
        entity.setSurname(dto.getSurname());
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword());
        entity.setRole(dto.getRole());
        entity.setStatus(ProfileStatus.NOT_CONFIRMED);
        return entity;
    }
}
