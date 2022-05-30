package com.company.service;

import com.company.dto.TagResponceDTO;
import com.company.dtoRequest.TagRequestDTO;
import com.company.entity.TagEntity;
import com.company.exp.AppBadRequestException;
import com.company.exp.ItemAlreadyExistsException;
import com.company.exp.ItemNotFoundException;
import com.company.repository.TagRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TagService {
    @Autowired
    private TagRepository tagRepository;

    public TagResponceDTO create(TagRequestDTO dto) {

        if (!dto.getName().startsWith("#")){
            dto.setName("#"+dto.getName());
        }

        Optional<TagEntity> optional = tagRepository.findByName(dto.getName());

        if (optional.isPresent()){
            throw new ItemAlreadyExistsException("Tag already exists!");
        }

        TagEntity entity = new TagEntity();
        entity.setName(dto.getName());

        try {
            tagRepository.save(entity);
        }catch (DataIntegrityViolationException e){
            log.warn("Unique {}", dto);
            throw new AppBadRequestException("Unique Items!");
        }
        return toDTO(entity);
    }

    public TagResponceDTO update(Integer id, TagRequestDTO dto) {

        if (!dto.getName().startsWith("#")){
            dto.setName("#"+dto.getName());
        }

        TagEntity entity = tagRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Not Found!"));


        entity.setName(dto.getName());
        entity.setUpdateDate(LocalDateTime.now());

        try {
            tagRepository.save(entity);
        }catch (DataIntegrityViolationException e){
            log.warn("Unique {}", dto);
            throw new AppBadRequestException("Unique Items!");
        }
        return toDTO(entity);
    }

    public Boolean delete(Integer id) {
        TagEntity entity = tagRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Not Found!"));

        tagRepository.delete(entity);
        return true;
    }

    public TagEntity getById(Integer id){
        Optional<TagEntity> optional=tagRepository.findById(id);
        if (optional.isEmpty()){
            throw new ItemNotFoundException("Tag not found!");
        }
        return optional.get();
    }

    private TagResponceDTO toDTO(TagEntity entity) {
        TagResponceDTO dto = new TagResponceDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setUpdatedDate(entity.getUpdateDate());
        return dto;
    }

    public List<TagResponceDTO> getTagList() {
        List<TagEntity> entityList = tagRepository.findAll();
        List<TagResponceDTO> dtoList = new LinkedList<>();
        for (TagEntity entity : entityList) {
            dtoList.add(toDTO(entity));
        }
        return dtoList;
    }


}
