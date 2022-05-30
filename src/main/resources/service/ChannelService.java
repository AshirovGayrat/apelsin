package com.company.service;

import com.company.dto.ChannelResponceDTO;
import com.company.dto.ChannelShortInfoDTO;
import com.company.dtoRequest.ChannelUpdateStatusDTO;
import com.company.dtoRequest.dto.ChannelRequestDto;
import com.company.entity.ChannelEntity;
import com.company.entity.ProfileEntity;
import com.company.enums.ChannelStatus;
import com.company.exp.AppForbiddenException;
import com.company.exp.ItemAlreadyExistsException;
import com.company.exp.ItemNotFoundException;
import com.company.repository.ChannelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ChannelService {
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private AttachService attachService;
    @Autowired
    private ProfileService profileService;

    public Boolean create(ChannelRequestDto dto, Integer pid) {
        String name = channelRepository.getByName(dto.getName());
        if (dto.getChannelPhotoId() != null) {
            attachService.get(dto.getChannelPhotoId());
        }
        if (dto.getBannerPhotoId() != null) {
            attachService.get(dto.getBannerPhotoId());
        }
        if (name != null) {
            throw new ItemAlreadyExistsException("Channel name is Already exists!");
        }
        String key = channelRepository.getByKey(dto.getKey());
        if (key != null) {
            throw new ItemAlreadyExistsException("Channel Already exists!");
        }

        channelRepository.save(toChannelEntity(pid, dto));
        return true;
    }

    public PageImpl<ChannelResponceDTO> getAllWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<ChannelEntity> channelEntityPage = channelRepository.findAll(pageable);
        List<ChannelEntity> entityList = channelEntityPage.getContent();
        long totalElements = channelEntityPage.getTotalElements();
        List<ChannelResponceDTO> dtoList = entityList.stream().map(this::toDto).toList();
        return new PageImpl<ChannelResponceDTO>(dtoList, pageable, totalElements);
    }

    public Boolean update(Integer pid, Integer id, ChannelRequestDto dto) {
        ProfileEntity profileEntity = profileService.get(pid);

        String name = channelRepository.getByName(dto.getName());
        if (name != null) {
            throw new ItemAlreadyExistsException("Channel name is Already exists!");
        }

        ChannelEntity entity = channelRepository.findById(id).
                orElseThrow(() -> new ItemNotFoundException("Chanel Not found"));

        if (!entity.getId().equals(pid)) {
            throw new AppForbiddenException("Not Access");
        }

        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setKey(dto.getKey());
        entity.setProfile(profileEntity);
        entity.setChannelPhotoId(dto.getChannelPhotoId());
        entity.setBannerPhotoId(dto.getBannerPhotoId());
        channelRepository.save(entity);
        return true;
    }

    public Boolean updateStatus(Integer id, ChannelUpdateStatusDTO dto) {
        Optional<ChannelEntity> optional = channelRepository.findById(id);
        if (optional.isEmpty()) {
            throw new ItemNotFoundException("Chanel Not found!");
        }
        int n = channelRepository.updateStatus(ChannelStatus.valueOf(dto.getStatus()), id);
        return n > 1;
    }

//    public AttachEntity updateAttach(MultipartFile file, Integer pId) {
//        ChannelEntity channelEntity=channelRepository.findByProfileId(pId).
//                orElseThrow(()->new ItemNotFoundException("Channel id not found"));
//
//        AttachEntity attachEntity=new AttachEntity();
//
//        if (channelEntity.getBannerPhoto() != null) {
//            attachEntity=attachService.updateAttach(file,channelEntity.getBannerPhoto().getId());
//        } else if (channelEntity.getBannerPhoto() == null) {
//            attachEntity=attachService.reUploadAttach(file);
//        }
//        channelEntity.setBannerPhoto(attachEntity);
//
//        return attachEntity;
//    }

    public Boolean updateChannelImage(String attachId, Integer channelId, Integer profileId) {
        attachService.get(attachId);

        ChannelEntity entity = getById(channelId);

        if (!entity.getProfileId().equals(profileId)) {
            log.warn("Not access {}", profileId);
            throw new AppForbiddenException("Not access!");
        }

        if (Optional.ofNullable(entity.getChannelPhotoId()).isPresent()) {
            if (entity.getChannelPhotoId().equals(attachId)) {
                return true;
            }
            String oldAttach = entity.getChannelPhotoId();
            channelRepository.updatePhoto(attachId, channelId);
            attachService.delete(oldAttach);
            return true;
        }
        channelRepository.updatePhoto(attachId, channelId);
        return true;
    }

    public Boolean updateBannerImage(String attachId, Integer channelId, Integer profileId) {
        attachService.get(attachId);

        ChannelEntity entity = getById(channelId);

        if (!entity.getProfileId().equals(profileId)) {
            log.warn("Not access {}", profileId);
            throw new AppForbiddenException("Not access!");
        }

        if (Optional.ofNullable(entity.getChannelPhotoId()).isPresent()) {
            if (entity.getBannerPhotoId().equals(attachId)) {
                return true;
            }
            String oldAttach = entity.getBannerPhotoId();
            channelRepository.updateBanner(attachId, channelId);
            attachService.delete(oldAttach);
            return true;
        }
        channelRepository.updateBanner(attachId, channelId);
        return true;
    }

//    public Boolean updateBannerImage(Integer chId, AttachDto dto){
//        ChannelEntity channelEntity=channelRepository.findById(chId).
//                orElseThrow(()->new ItemNotFoundException("Channel id not found"));
//        channelEntity.setBannerPhoto(updateAttach(file, pId));
//        return true;
//    }

    public Boolean delete(Integer id) {
        ChannelEntity entity = channelRepository.findById(id).
                orElseThrow(() -> new ItemNotFoundException("Chanel not found"));
        channelRepository.delete(entity);
        return true;
    }

    public ChannelEntity getById(Integer id) {
        return channelRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Channel id not found"));
    }

    public ChannelResponceDTO getDtoById(Integer id) {
        return toDto(channelRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Channel id not found")));
    }

    public ChannelShortInfoDTO getChannelShortInfoDTOById(Integer id) {
        return toSimpleDto(channelRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Channel id not found")));
    }

    public ChannelShortInfoDTO toSimpleDto(ChannelEntity entity) {
        ChannelShortInfoDTO dto = new ChannelShortInfoDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        if (entity.getChannelPhotoId() != null) {
            dto.setPhotoUrl(attachService.toOpenURL(entity.getChannelPhotoId()));
        }
        dto.setKey(entity.getKey());
        return dto;
    }

    public ChannelResponceDTO toDto(ChannelEntity entity) {
        ChannelResponceDTO dto = new ChannelResponceDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        if (entity.getChannelPhotoId() != null) {
            dto.getChannelPhotoDto().setUrl(attachService.toOpenURL(entity.getChannelPhotoId()));
            dto.getChannelPhotoDto().setAttachId(entity.getChannelPhotoId());
        }
        if (entity.getBannerPhotoId() != null) {
            dto.getBannerPhotoDto().setUrl(attachService.toOpenURL(entity.getBannerPhotoId()));
            dto.getBannerPhotoDto().setAttachId(entity.getBannerPhotoId());
        }
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setKey(entity.getKey());
        return dto;
    }

    public ChannelEntity toChannelEntity(Integer pid, ChannelRequestDto dto) {
        ChannelEntity entity = new ChannelEntity();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setKey(dto.getKey());
        entity.setProfileId(pid);
        entity.setChannelPhotoId(dto.getChannelPhotoId());
        entity.setBannerPhotoId(dto.getBannerPhotoId());
        entity.setStatus(ChannelStatus.ACTIVE);
        return entity;
    }
}
