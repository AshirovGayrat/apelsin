package com.company.service;

import com.company.dto.AttachSimpleDTO;
import com.company.dtoRequest.CommentRequestDTO;
import com.company.entity.CommentEntity;
import com.company.exp.AppBadRequestException;
import com.company.exp.ItemNotFoundException;
import com.company.mapper.LikeCountMapper;
import com.company.repository.CommentLikeRepository;
import com.company.repository.CommentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private CommentLikeRepository commentLikeRepository;
    @Autowired
    private AttachService attachService;

    public CommentResponceDTO create(CommentRequestDTO dto, Integer pid) {

        CommentEntity comment = new CommentEntity();
        if (dto.getCommentId() != null) {
            comment.setCommentId(dto.getCommentId());
        }
        if (dto.getVideoId() != null) {
            comment.setVideoId(dto.getVideoId());
        }
        comment.setContent(dto.getContent());
        comment.setProfileId(pid);
        commentRepository.save(comment);

        return toDto(comment);
    }

    public CommentResponceDTO update(Integer id, Integer pId, CommentRequestDTO dto) {

        CommentEntity comment = getById(id);
        comment.setContent(dto.getContent());

        try {
            commentRepository.save(comment);
        } catch (DataIntegrityViolationException e) {
            log.warn("Unique {}", dto);
            throw new AppBadRequestException("Unique Items!");
        }
        return toDto(comment);
    }

    public PageImpl<CommentResponceDTO> getCommentList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        Page<CommentEntity> pages = commentRepository.findAll(pageable);
        List<CommentEntity> entityList = pages.getContent();

        List<CommentResponceDTO> dtoList = new LinkedList<>();
        entityList.forEach(entity -> {
            dtoList.add(toDto(entity));
        });
        return new PageImpl<CommentResponceDTO>(dtoList, pageable, pages.getTotalElements());
    }

    //id,content,created_date,like_count,dislike_count, video(id,name,preview_attach_id,title,duration)
    public List<CommentInfoDTO> getProfilsCommentList(Integer pId) {
        List<CommentInfoDTO> dtoList = new ArrayList<>();
        commentRepository.getProfilsCommentList(pId).forEach(entity -> {
            CommentInfoDTO dto = new CommentInfoDTO();
            LikeCountMapper likeDislikeCount = commentLikeRepository.getLikeCountByCommentId(entity.getC_id());
            if (likeDislikeCount.getLike_count() != null) {
                dto.setLikeCount(likeDislikeCount.getLike_count());
            }
            if (likeDislikeCount.getDislike_count() != null) {
                dto.setDisLikeCount(likeDislikeCount.getDislike_count());
            }

            dto.setContent(entity.getContent());
            dto.setCreatedDate(entity.getCreated_date());
            dto.setId(entity.getC_id());

            VideoShortInfoDTO videoShortInfoDTO = new VideoShortInfoDTO();
            videoShortInfoDTO.setId(entity.getV_id());
            videoShortInfoDTO.setName(entity.getV_name());
            videoShortInfoDTO.setPreviewAttachId(entity.getV_preview_id());
            videoShortInfoDTO.setDescription(entity.getV_description());

            dto.setVideo(videoShortInfoDTO);

            dtoList.add(dto);

        });
        return dtoList;
    }

    public List<VideoCommentResponceDTO> getCommentListByVideoId(Integer videoId) {
        List<VideoCommentResponceDTO> dtoList = new ArrayList<>();
        commentRepository.getVideoCommentList(videoId).forEach(entity -> {
            VideoCommentResponceDTO dto = new VideoCommentResponceDTO();
            LikeCountMapper likeDislikeCount = commentLikeRepository.getLikeCountByCommentId(entity.getC_id());
            if (likeDislikeCount.getLike_count() != null) {
                dto.setLikeCount(likeDislikeCount.getLike_count());
            }
            if (likeDislikeCount.getDislike_count() != null) {
                dto.setDisLikeCount(likeDislikeCount.getDislike_count());
            }

            dto.setContent(entity.getContent());
            dto.setCreatedDate(entity.getCreated_date());
            dto.setId(entity.getC_id());

            ProfileSimpleDTO profile = new ProfileSimpleDTO();
            profile.setId(entity.getP_id());
            profile.setName(entity.getP_name());
            profile.setSurName(entity.getP_surname());

            AttachSimpleDTO attachSimpleDTO = new AttachSimpleDTO();
            attachSimpleDTO.setAttachId(entity.getP_photo_id());
            attachSimpleDTO.setUrl(attachService.toOpenURL(entity.getP_photo_id()));
            profile.setPhoto(attachSimpleDTO);

            dto.setProfile(profile);

            dtoList.add(dto);

        });
        return dtoList;
    }

    public List<CommentResponceDTO> getCommentsByPid(Integer pId) {
        List<CommentResponceDTO> dtoList = new ArrayList<>();
        commentRepository.findAllByProfileId(pId).forEach(entity -> {
            dtoList.add(toDto(entity));
        });
        return dtoList;
    }

    public CommentEntity getById(Integer id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Comment id not found!"));
    }

    public Boolean delete(Integer id) {
        CommentEntity entity = commentRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Not Found!"));

        commentRepository.delete(entity);
        return true;
    }

    public CommentResponceDTO toDto(CommentEntity entity) {
        CommentResponceDTO dto = new CommentResponceDTO();
        dto.setId(entity.getId());
        dto.setContent(entity.getContent());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setCommentId(entity.getCommentId());
        dto.setVideoId(entity.getVideoId());
        dto.setProfileId(entity.getProfileId());
        return dto;
    }
}
