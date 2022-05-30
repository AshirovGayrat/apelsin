package com.company.service;

import com.company.dtoRequest.VideoTagRequestDTO;
import com.company.entity.VideoTagEntity;
import com.company.exp.ItemNotFoundException;
import com.company.repository.VideoTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VideoTagService {
    @Autowired
    private VideoTagRepository videoTagRepository;
    @Autowired
    private TagService tagService;
    @Autowired
    private VideoService videoService;

    public Boolean addTag(VideoTagRequestDTO dto){
        videoService.get(dto.getVideoId());
        tagService.getById(dto.getTagId());

        VideoTagEntity tagEntity=new VideoTagEntity();
        tagEntity.setTagId(dto.getTagId());
        tagEntity.setVideoId(dto.getVideoId());

        videoTagRepository.save(tagEntity);
        return true;
    }

    public Boolean delete(Integer id, VideoTagRequestDTO dto){
        Optional<VideoTagEntity> optional=videoTagRepository.findById(id);
        if (optional.isEmpty()){
            throw new ItemNotFoundException("Video Tag Not found");
        }
        videoTagRepository.delete(optional.get());
        return true;
    }

}
