package com.company.service;

import com.company.dto.ChannelShortInfoDTO;
import com.company.dto.PlaylistVideoInfoDTO;
import com.company.dto.VideoShortInfoDTO;
import com.company.dtoRequest.PlaylistVideoRequestDTO;
import com.company.entity.PlaylistEntity;
import com.company.entity.PlaylistVideoEntity;
import com.company.entity.VideoEntity;
import com.company.exp.AppBadRequestException;
import com.company.mapper.PlaylistVideoInfoMapper;
import com.company.repository.PlaylistVideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PlaylistVideoService {
    @Autowired
    private PlaylistVideoRepository playlistVideoRepository;
    @Autowired
    private VideoService videoService;
    @Autowired
    private PlaylistService playlistService;

    public Boolean create(PlaylistVideoRequestDTO dto){
        VideoEntity videoEntity=videoService.get(dto.getVideoId());
        PlaylistEntity playlistEntity=playlistService.get(dto.getPlaylistId());

        PlaylistVideoEntity entity=new PlaylistVideoEntity();
        entity.setVideoId(videoEntity.getId());
        entity.setPlaylistId(playlistEntity.getId());
        entity.setOrderNum(dto.getOrderNum());
        playlistVideoRepository.save(entity);
        return true;
    }

    //Get Video list by playListId (video status published)
    public PageImpl<PlaylistVideoInfoDTO> getVideoList(Integer playlistId, int page, int size){
        Pageable pageable= PageRequest.of(page, size);

        Page<PlaylistVideoInfoMapper> mapperList=playlistVideoRepository.getPlaylistVideoList(playlistId, pageable);
        List<PlaylistVideoInfoMapper> entityList=mapperList.getContent();
        List<PlaylistVideoInfoDTO> dtoList=new ArrayList<>();

        entityList.forEach(mapper -> {
            PlaylistVideoInfoDTO dto=new PlaylistVideoInfoDTO();
            dto.setPlaylistId(mapper.getP_id());
            dto.setOrderNum(mapper.getPv_order_num());
            dto.setCreatedDate(mapper.getCreated_date());
            VideoShortInfoDTO videoInfoDTO=new VideoShortInfoDTO();
            videoInfoDTO.setId(mapper.getV_id());
            videoInfoDTO.setName(mapper.getV_name());
            videoInfoDTO.setPreviewAttachId(mapper.getV_preview_id());
            videoInfoDTO.setKey(mapper.getV_key());
            dto.setVideo(videoInfoDTO);
            ChannelShortInfoDTO channelDTO=new ChannelShortInfoDTO();
            channelDTO.setId(mapper.getCh_id());
            channelDTO.setName(mapper.getCh_name());
            channelDTO.setKey(mapper.getCh_key());
            dto.setChannel(channelDTO);
            dtoList.add(dto);
        });
        return new PageImpl<PlaylistVideoInfoDTO>(dtoList, pageable, mapperList.getTotalElements());
    }

    public String update(Integer id, PlaylistVideoRequestDTO dto) {
        Optional<PlaylistVideoEntity> optional = playlistVideoRepository.findById(id);
        if (optional.isEmpty()) {
            throw new AppBadRequestException("Id Not Found");
        }

        PlaylistVideoEntity playlist = optional.get();
        playlist.setOrderNum(dto.getOrderNum());
        playlist.setUpdateDate(LocalDateTime.now());
        playlist.setPlaylistId(dto.getPlaylistId());
        playlist.setVideoId(dto.getVideoId());

        playlistVideoRepository.save(playlist);
        return "Success";
    }

    public String delete(Integer id) {
        Optional<PlaylistVideoEntity> optional = playlistVideoRepository.findById(id);
        if (optional.isEmpty()) {
            throw new AppBadRequestException("Id Not Found");
        }

        playlistVideoRepository.delete(optional.get());
        return "Success";
    }

}
