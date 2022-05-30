package com.company.service;

import com.company.dto.CategoryRequestDTO;
import com.company.dto.CategoryResponceDTO;
import com.company.entity.CategoryEntity;
import com.company.exp.AppBadRequestException;
import com.company.exp.CategoryAlreadyExistsException;
import com.company.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public CategoryRequestDTO create(CategoryRequestDTO dto) {
        if (getByName(dto.getNameUz()).isPresent()) {
            throw new CategoryAlreadyExistsException("Category already exists");
        }

        CategoryEntity entity = new CategoryEntity();
        entity.setNameUz(dto.getNameUz());
        entity.setNameRu(dto.getNameRu());
        entity.setNameEn(dto.getNameEn());
        entity.setKey(dto.getKey());

        try {
            categoryRepository.save(entity);
        }catch (DataIntegrityViolationException e){
            log.warn("Unique {}", dto);
            throw new AppBadRequestException("Unique Items!");
        }
        return dto;
    }

    public PageImpl<CategoryResponceDTO> getAllWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<CategoryEntity> categoryEntityPage = categoryRepository.findAll(pageable);

        List<CategoryEntity> categoryEntityList = categoryEntityPage.getContent();
        long totalContent = categoryEntityPage.getTotalElements();
        List<CategoryResponceDTO> dtoList = categoryEntityList.stream().map(this::toDto).toList();
        return new PageImpl<CategoryResponceDTO>(dtoList, pageable, totalContent);
    }

    public String update(String id, CategoryRequestDTO dto) {
        Optional<CategoryEntity> optional = getById(id);
        if (optional.isEmpty()) {
            throw new AppBadRequestException("Id Not Found");
        }

        if (getByName(dto.getNameUz()).isPresent()) {
            throw new CategoryAlreadyExistsException("Category alredy exists");
        }

        CategoryEntity category = optional.get();
        category.setNameUz(dto.getNameUz());
        category.setNameRu(dto.getNameRu());
        category.setNameEn(dto.getNameEn());

        try {
            categoryRepository.save(category);
        }catch (DataIntegrityViolationException e){
            log.warn("Unique {}", dto);
            throw new AppBadRequestException("Unique Items!");
        }
        return "Success";
    }

    public String delete(String id) {
        Optional<CategoryEntity> optional = getById(id);
        if (optional.isEmpty()) {
            throw new AppBadRequestException("Id Not Found");
        }
        CategoryEntity entity = optional.get();
        categoryRepository.delete(entity);
        return "Success";
    }

    public Optional<CategoryEntity> getById(String id){
        return categoryRepository.findById(id);
    }

    public Optional<CategoryEntity> getByName(String name){
        return categoryRepository.findByNameUz(name);
    }

    public CategoryResponceDTO toDto(CategoryEntity entity) {
        CategoryResponceDTO dto = new CategoryResponceDTO();
        dto.setNameEn(entity.getNameEn());
        dto.setNameRu(entity.getNameRu());
        dto.setNameUz(entity.getNameUz());
        dto.setId(entity.getId());
        dto.setCreatedDate(entity.getCreatedDate());
        return dto;
    }

}
