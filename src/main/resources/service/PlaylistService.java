package com.company.service;

import com.company.dto.AttachSimpleDTO;
import com.company.dtoRequest.PlaylistRequestDTO;
import com.company.entity.ChannelEntity;
import com.company.entity.PlaylistEntity;
import com.company.exp.AppBadRequestException;
import com.company.exp.AppForbiddenException;
import com.company.exp.ItemNotFoundException;
import com.company.mapper.PlayListInfoJpqlAdminMapper;
import com.company.mapper.PlaylistShortInfoMapper;
import com.company.mapper.VideoShortInfoMapper;
import com.company.repository.PlaylistRepository;
import com.company.repository.PlaylistVideoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PlaylistService {
    @Autowired
    private PlaylistRepository playlistRepository;
    @Autowired
    private PlaylistVideoRepository playlistVideoRepository;
    @Autowired
    private AttachService attachService;
    @Autowired
    private ChannelService channelService;

    public Boolean create(Integer chId, PlaylistRequestDTO dto, Integer pid) {
        ChannelEntity channelEntity = channelService.getById(chId);
        if (!channelEntity.getProfileId().equals(pid)) {
            log.warn("Not access {}", pid);
            throw new AppForbiddenException("not access!");
        }
        if (dto.getAttachId() != null) {
            attachService.get(dto.getAttachId());//check attach Id
        }

        PlaylistEntity entity = new PlaylistEntity();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescripyion());
        entity.setOrderNum(dto.getOrderNum());
        entity.setPlaylistPhotoId(dto.getAttachId());
        entity.setChannelId(chId);
        playlistRepository.save(entity);
        return true;
    }

    public PageImpl<PlaylistDto> list(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        List<PlaylistDto> dtoList = new ArrayList<>();

        Page<PlayListInfoJpqlAdminMapper> entityPage = playlistRepository.getPlaylistInfoJpql(pageable);

        List<PlayListInfoJpqlAdminMapper> entityList = entityPage.getContent();
        List<PlaylistDto> playListDTO = new LinkedList<>();
        entityList.forEach(entity -> {
            PlaylistDto dto = new PlaylistDto();
            dto.setId(entity.getPl_id());
            dto.setName(entity.getPl_name());
            dto.setDescripyion(entity.getPl_description());

            ChannelResponceDTO channelDTO = new ChannelResponceDTO();
            channelDTO.setId(entity.getCh_id());
            channelDTO.setName(entity.getCh_name());
            if (entity.getCh_photo_id() != null) {
                AttachSimpleDTO attachDTO = new AttachSimpleDTO();
                attachDTO.setUrl(attachService.toOpenURL(entity.getCh_photo_id()));
                channelDTO.setChannelPhotoDto(attachDTO);
            }

            dto.setChannelDto(channelDTO);

            ProfileDto profileDTO = new ProfileDto();
            profileDTO.setId(entity.getPr_id());
            profileDTO.setName(entity.getPr_name());
            profileDTO.setSurname(entity.getPr_surname());

            if (Optional.ofNullable(entity.getPr_photo_id()).isPresent()) {
                AttachSimpleDTO attachDTO = new AttachSimpleDTO();
                attachDTO.setUrl(attachService.toOpenURL(entity.getPr_photo_id()));
                profileDTO.setAttachDto(attachDTO);
            }
            channelDTO.setProfile(profileDTO);

            playListDTO.add(dto);
        });
        return new PageImpl<>(playListDTO, pageable, entityPage.getTotalElements());
    }

    public PageImpl<PlaylistShortInfoDTO> channelPlaylists(int page, int size, String channelKey) {
        Pageable pageable = PageRequest.of(page, size);

        Page<PlaylistShortInfoMapper> entityPage = playlistRepository.getChannelPlaylists(channelKey, pageable);

        List<PlaylistShortInfoMapper> entityList = entityPage.getContent();
        List<PlaylistShortInfoDTO> playListDTO = new LinkedList<>();
        entityList.forEach(entity -> {
            PlaylistShortInfoDTO dto = new PlaylistShortInfoDTO();
            dto.setId(entity.p_id());
            dto.setName(entity.p_name());
            dto.setCreatedDate(entity.p_created_date());

            ChannelShortInfoDTO channelDTO = new ChannelShortInfoDTO();
            channelDTO.setId(entity.ch_id());
            channelDTO.setName(entity.ch_name());
            dto.setChannel(channelDTO);

            List<VideoShortInfoMapper> videoList = playlistVideoRepository.getVideoList(entity.p_id(), pageable).getContent();
            VideoShortInfoDTO videoDto = new VideoShortInfoDTO();
            videoDto.setId(videoList.get(0).v_id());
            videoDto.setName(videoList.get(0).v_name());
            videoDto.setKey(videoList.get(0).v_key());
            dto.getVideoList().add(videoDto);

            VideoShortInfoDTO videoDto2 = new VideoShortInfoDTO();
            videoDto2.setId(videoList.get(1).v_id());
            videoDto2.setName(videoList.get(1).v_name());
            videoDto2.setKey(videoList.get(1).v_key());
            dto.getVideoList().add(videoDto2);

            playListDTO.add(dto);
        });
        return new PageImpl<PlaylistShortInfoDTO>(playListDTO, pageable, entityPage.getTotalElements());
    }

    public PageImpl<PlaylistDto> getAllWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<PlaylistEntity> playlistEntityPage = playlistRepository.findAll(pageable);

        List<PlaylistEntity> playlistEntityList = playlistEntityPage.getContent();
        long totalContent = playlistEntityPage.getTotalElements();
        List<PlaylistDto> dtoList = playlistEntityList.stream().map(this::toDto).toList();
        return new PageImpl<PlaylistDto>(dtoList, pageable, totalContent);
    }

    public String update(Integer id, PlaylistRequestDTO dto) {
        Optional<PlaylistEntity> optional = playlistRepository.findById(id);
        if (optional.isEmpty()) {
            throw new AppBadRequestException("Id Not Found");
        }

        PlaylistEntity playlist = optional.get();
        playlist.setName(dto.getName());
        playlist.setDescription(dto.getDescripyion());
        playlist.setOrderNum(dto.getOrderNum());
        playlist.setUpdateDate(LocalDateTime.now());
        playlist.setPlaylistPhotoId(dto.getAttachId());

        playlistRepository.save(playlist);
        return "Success";
    }

    public String delete(Integer id) {
        Optional<PlaylistEntity> optional = playlistRepository.findById(id);
        if (optional.isEmpty()) {
            throw new AppBadRequestException("Id Not Found");
        }

        playlistRepository.delete(optional.get());
        return "Success";
    }

    public PlaylistEntity get(Integer id) {
        return playlistRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Not found!"));
    }

    public PlaylistDto toDto(PlaylistEntity entity) {
        PlaylistDto dto = new PlaylistDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescripyion(entity.getDescription());
        dto.setOrderNum(entity.getOrderNum());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setChannelDto(channelService.getDtoById(entity.getChannelId()));
        dto.getPlaylistPhotoDto().setUrl(attachService.toOpenURL(entity.getPlaylistPhotoId()));
        dto.getPlaylistPhotoDto().setAttachId(entity.getPlaylistPhotoId());
        return dto;
    }

}
