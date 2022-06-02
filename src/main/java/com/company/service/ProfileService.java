package com.company.service;

import com.company.dto.AttachSimpleDTO;
import com.company.dto.request.ProfileChangeStatusRequestDTO;
import com.company.dto.request.ProfileRequestDTO;
import com.company.dto.response.ProfileResponseDTO;
import com.company.entity.ProfileEntity;
import com.company.enums.ProfileRole;
import com.company.enums.ProfileStatus;
import com.company.exception.AppBadRequestException;
import com.company.exception.ItemAlreadyExistsException;
import com.company.exception.ItemNotFoundException;
import com.company.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final AttachService attachService;


    public ProfileResponseDTO create(ProfileRequestDTO requestDTO) {
        Optional<ProfileEntity> optional = profileRepository.findByPhone(requestDTO.getPhone());
        if (optional.isPresent()) {
            log.warn("Phone already axists : {}", requestDTO);
            throw new ItemAlreadyExistsException("Phone already exists!");
        }

        ProfileEntity entity = new ProfileEntity();
        entity.setName(requestDTO.getName());
        entity.setSurname(requestDTO.getSurname());
        entity.setPhone(requestDTO.getPhone());
        entity.setStatus(ProfileStatus.ACTIVE);
        entity.setRole(ProfileRole.USER);

        if (requestDTO.getAttachId() != null) {
            entity.setAttachId(requestDTO.getAttachId());
        }

        try {
            profileRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            log.warn("Unique {}", requestDTO);
            throw new AppBadRequestException("Unique Items!");
        }

        return toDTO(entity);
    }

    public ProfileResponseDTO getById(String id) {
        ProfileEntity entity = profileRepository.findByIdAndVisible(id, true).orElseThrow(() -> {
            log.warn("Profile id not found");
            throw new ItemNotFoundException("Profile id not found");
        });
        return toDTO(entity);
    }

    public List<ProfileResponseDTO> getAll() {
        List<ProfileResponseDTO> profileList = new LinkedList<>();
        profileRepository.findAllByVisible(true).forEach(entity -> {
            profileList.add(toDTO(entity));
        });
        return profileList;
    }

    public Boolean delete(String id) {
        int n = profileRepository.deleteStatus(false, id);
        return n > 0;
    }

    public Boolean changeStatus(String id, ProfileChangeStatusRequestDTO requestDTO) {
        int n = profileRepository.changeStatus(requestDTO.getStatus(), id);
        return n > 0;
    }

    private ProfileResponseDTO toDTO(ProfileEntity entity) {
        ProfileResponseDTO responseDTO = new ProfileResponseDTO();
        responseDTO.setId(entity.getId());
        responseDTO.setStatus(entity.getStatus());
        responseDTO.setName(entity.getName());
        responseDTO.setSurname(entity.getSurname());
        responseDTO.setPhone(entity.getPhone());
        responseDTO.setCreatedDate(entity.getCreatedDate());
        if (entity.getAttachId() != null) {
            AttachSimpleDTO attach=new AttachSimpleDTO();
            attach.setId(entity.getAttachId());
            attach.setToOpenUrl(attachService.toOpenUrl(entity.getAttachId()));
            responseDTO.setAttach(attach);
        }
        return responseDTO;
    }

    public ProfileEntity getByPhone(String phone) {
        return profileRepository.findByPhone(phone)
                .orElseThrow(() -> new ItemNotFoundException("Not Found!"));
    }
}
