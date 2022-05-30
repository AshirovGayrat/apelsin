package com.company.service;

import com.company.dto.LikeAndDislikeCountDTO;
import com.company.dto.VideoLikeResponceDTO;
import com.company.dtoRequest.dto.LikeRequestDTO;
import com.company.entity.VideoLikeEntity;
import com.company.enums.IsLikeType;
import com.company.exp.ItemNotFoundException;
import com.company.mapper.LikeCountMapper;
import com.company.repository.VideoLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VideoLikeService {
    @Autowired
    private VideoLikeRepository videoLikeRepository;
    @Autowired
    private VideoService videoService;
    @Autowired
    private ProfileService profileService;

    public String create(Integer pid, Integer videoId, LikeRequestDTO dto) {
        profileService.get(pid);
        videoService.get(videoId);

        Optional<VideoLikeEntity> optional=videoLikeRepository.findByVideoIdAndProfileId(videoId, pid);
        if (optional.isPresent()){
            if (dto.getType().equals(optional.get().getType().name())){
                videoLikeRepository.delete(optional.get());
                return "deleted";
            }
            VideoLikeEntity videoLikeEntity=optional.get();
            videoLikeEntity.setProfileId(pid);
            videoLikeEntity.setVideoId(videoId);
            videoLikeEntity.setType(IsLikeType.valueOf(dto.getType()));
            videoLikeRepository.save(videoLikeEntity);
            return "created";
        }

        VideoLikeEntity entity = new VideoLikeEntity();
        entity.setVideoId(videoId);
        entity.setProfileId(pid);
        entity.setType(IsLikeType.valueOf(dto.getType()));

        videoLikeRepository.save(entity);

        return "created";
    }

    public Boolean remove(Integer videoId, Integer pId) {
        VideoLikeEntity videoLikeEntity = videoLikeRepository.findByVideoIdAndProfileId(videoId, pId).
                orElseThrow(() -> new ItemNotFoundException("VideoLike not found!"));

        videoLikeRepository.delete(videoLikeEntity);

        return true;
    }

    public PageImpl<VideoLikeResponceDTO> getUserLicedVideoList(Integer pId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        List<VideoLikeResponceDTO> dtoList = new ArrayList<>();

        Page<VideoLikeEntity> entityPage = videoLikeRepository.findByProfileId(pId, pageable);
        List<VideoLikeEntity> entityList = entityPage.getContent();

        entityList.forEach(entity -> {
            VideoLikeResponceDTO dto = new VideoLikeResponceDTO();
            dto.setId(entity.getId());
            dto.setVideo(videoService.getDto(entity.getVideoId()));
            dtoList.add(dto);
        });

        return new PageImpl<VideoLikeResponceDTO>(dtoList, pageable, entityPage.getTotalElements());
    }

    public LikeAndDislikeCountDTO getLikeAndDislikeCount(Integer videoId){
        LikeCountMapper count=videoLikeRepository.getLikeCountByVideoId(videoId);
        return new LikeAndDislikeCountDTO(count.getLike_count(), count.getDislike_count());
    }

}
