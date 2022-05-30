package com.company.service;

import com.company.dto.VideoDto;
import com.company.dto.VideoShortInfoDTO;
import com.company.dtoRequest.VideoRequestDTO;
import com.company.entity.VideoEntity;
import com.company.entity.VideoTagEntity;
import com.company.enums.ProfileRole;
import com.company.enums.VideoStatus;
import com.company.enums.VideoType;
import com.company.exp.AppBadRequestException;
import com.company.exp.ItemAlreadyExistsException;
import com.company.exp.ItemNotFoundException;
import com.company.repository.VideoRepository;
import com.company.repository.VideoTagRepository;
import com.company.util.PageableUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VideoService {
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private AttachService attachService;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private VideoTagRepository videoTagRepository;

    public Boolean create(VideoRequestDTO dto, Integer chId) {
//        attachService.get(dto.getAttachId());//check attach id

        VideoEntity entity = new VideoEntity();
        entity.setKey(dto.getKey());
        entity.setAttachId(dto.getAttachId());
        entity.setVideoName(dto.getVideoName());
        entity.setDescription(dto.getDescription());
        entity.setPlaylistId(dto.getPlaylistId());
        entity.setChannelId(chId);
        entity.setCategory_id(dto.getCategoryId());
        entity.setStatus(VideoStatus.PUBLIC);
        entity.setType(VideoType.valueOf(dto.getType()));

        if (dto.getPreviewPhotoId() != null) {
            entity.setPreviewId(dto.getPreviewPhotoId());
        }

        try {
            videoRepository.save(entity);
        } catch (ItemAlreadyExistsException e) {
            throw new ItemNotFoundException("Unique");
        }
        return true;
    }

    public PageImpl<VideoDto> getAllWithPagination(int page, int size) {
        Page<VideoEntity> videoEntityPage = videoRepository.findAll(PageableUtil.getPageable(page, size, "createdDate"));

        List<VideoEntity> playlistEntityList = videoEntityPage.getContent();
        long totalContent = videoEntityPage.getTotalElements();
        List<VideoDto> dtoList = playlistEntityList.stream().map(this::toDto).toList();
        return new PageImpl<VideoDto>(dtoList, PageableUtil.getPageable(page, size, "createdDate"), totalContent);
    }

    ////
    public PageImpl<VideoDto> getChannelVideos(Integer channelId, int page, int size) {
        return getPageableList(videoRepository.findAllByChannelId(channelId, PageableUtil.getPageable(page,size,"createdDate")),
                PageableUtil.getPageable(page, size, "createdDate"));
    }
    public PageImpl<VideoDto> getPageableList(Page<VideoEntity> videoEntityPage, Pageable pageable) {
        List<VideoEntity> playlistEntityList = videoEntityPage.getContent();
        long totalContent = videoEntityPage.getTotalElements();
        List<VideoDto> dtoList = playlistEntityList.stream().map(this::toDto).toList();
        return new PageImpl<VideoDto>(dtoList, pageable, totalContent);
    }
    ////

    public String update(Integer id, VideoRequestDTO dto) {
        Optional<VideoEntity> optional = videoRepository.findById(id);
        if (optional.isEmpty()) {
            throw new AppBadRequestException("Id Not Found");
        }

        VideoEntity entity = optional.get();
        entity.setVideoName(dto.getVideoName());
        entity.setDescription(dto.getDescription());
        if (dto.getPreviewPhotoId() != null) {
            entity.setPreviewId(dto.getPreviewPhotoId());
        }

        try {
            videoRepository.save(entity);
        } catch (ItemAlreadyExistsException e) {
            throw new ItemNotFoundException("Unique");
        }
        return "Success";
    }

    public void viewCount(Integer vId) {
        VideoEntity entity = videoRepository.findById(vId).orElseThrow(() -> new ItemNotFoundException("Not found!"));
        int vCount = entity.getViewCount() + 1;
        videoRepository.updateViewCount(vCount, vId);
    }

    public void shareCount(Integer vId) {
        VideoEntity entity = videoRepository.findById(vId).orElseThrow(() -> new ItemNotFoundException("Not found!"));
        int shCount = entity.getSharedCount() + 1;
        videoRepository.updateShareCount(shCount, vId);
    }

    public String delete(Integer id) {
        Optional<VideoEntity> optional = videoRepository.findById(id);
        if (optional.isEmpty()) {
            throw new AppBadRequestException("Id Not Found");
        }

        videoRepository.delete(optional.get());
        return "Success";
    }

    public VideoEntity get(Integer id) {
        return videoRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Not found!"));
    }

    public VideoDto getDto(Integer id) {
        return toDto(videoRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Not found!")));
    }

    public VideoDto getVideoByKey(String key, Integer pId) {
        VideoEntity entity=videoRepository.findByKey(key).orElseThrow(() -> new ItemNotFoundException("Not found!"));
        if (!entity.getStatus().equals(VideoStatus.PRIVATE) && !entity.getStatus().equals(VideoStatus.BLOCK)){
            return toDto(entity);
        }

        if (profileService.get(pId).getRole().equals(ProfileRole.ADMIN)){
            return toDto(entity);
        }

        if (channelService.getById(entity.getChannelId()).getProfileId().equals(pId)){
            return toDto(entity);
        }
        return new VideoDto();
    }

    public PageImpl<VideoShortInfoDTO> searchByTagId(Integer tagId, int page, int size){
        Pageable pageable=PageableUtil.getPageable(page, size, "createdDate");
        Page<VideoTagEntity> videoTagPage=videoTagRepository.findAllByTagId(tagId, pageable);
        List<VideoTagEntity> videoTagList=videoTagPage.getContent();
        List<VideoShortInfoDTO> dtoList=new ArrayList<>();
        videoTagList.forEach(entity -> {
            dtoList.add(toSimpleDto(get(entity.getVideoId())));
        });
        return new PageImpl<VideoShortInfoDTO>(dtoList, pageable, videoTagPage.getTotalElements());
    }

    public VideoShortInfoDTO toSimpleDto(VideoEntity entity){
        VideoShortInfoDTO dto=new VideoShortInfoDTO();
        dto.setId(entity.getId());
        dto.setKey(entity.getKey());
        dto.setName(entity.getVideoName());
        return dto;
    }

    public VideoDto toDto(VideoEntity entity) {
        VideoDto dto = new VideoDto();
        dto.setVideoName(entity.getVideoName());
        dto.setDescription(entity.getDescription());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setId(entity.getId());
        dto.setViewCount(entity.getViewCount());
        dto.setChannelDto(channelService.getChannelShortInfoDTOById(entity.getChannelId()));
        dto.setPlaylistId(entity.getPlaylistId());
        return dto;
    }

}
