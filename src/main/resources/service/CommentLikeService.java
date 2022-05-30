package com.company.service;

import com.company.dtoRequest.dto.LikeRequestDTO;
import com.company.entity.CommentLikeEntity;
import com.company.enums.IsLikeType;
import com.company.exp.ItemNotFoundException;
import com.company.repository.CommentLikeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class CommentLikeService {
    @Autowired
    private CommentLikeRepository commentLikeRepository;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private CommentService commentService;

    public String create(Integer pid, Integer commentId, LikeRequestDTO dto) {
        profileService.get(pid);
        commentService.getById(commentId);

        Optional<CommentLikeEntity> optional=commentLikeRepository.findByProfileIdAndCommentId(pid, commentId);
        if (optional.isPresent()){
            if (dto.getType().equals(optional.get().getType().name())){
                commentLikeRepository.delete(optional.get());
                return "deleted";
            }
            CommentLikeEntity entity=optional.get();
            entity.setProfileId(pid);
            entity.setCommentId(commentId);
            entity.setType(IsLikeType.valueOf(dto.getType()));
            commentLikeRepository.save(entity);
            return "created";
        }

        CommentLikeEntity entity2 = new CommentLikeEntity();
        entity2.setCommentId(commentId);
        entity2.setProfileId(pid);
        entity2.setType(IsLikeType.valueOf(dto.getType()));

        commentLikeRepository.save(entity2);

        return "created";
    }

    public Boolean remove(Integer cId, Integer pId) {
        CommentLikeEntity commentLikeEntity = commentLikeRepository.findByProfileIdAndCommentId(pId, cId).
                orElseThrow(() -> new ItemNotFoundException("VideoLike not found!"));

        commentLikeRepository.delete(commentLikeEntity);

        return true;
    }
}
