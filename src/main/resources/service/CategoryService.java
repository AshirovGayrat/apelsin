package com.company.service;

import com.company.dto.CategoryResponceDTO;
import com.company.dtoRequest.dto.CategoryRequestDTO;
import com.company.entity.CategoryEntity;
import com.company.exp.AppBadRequestException;
import com.company.exp.CategoryAlreadyExistsException;
import com.company.exp.CategoryAlredyExistsException;
import com.company.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public CategoryRequestDTO create(CategoryRequestDTO dto) {
        if (getByName(dto.getName()).isPresent()) {
            throw new CategoryAlreadyExistsException("Category already exists");
        }

        CategoryEntity entity = new CategoryEntity();
        entity.setName(dto.getName());
        categoryRepository.save(entity);
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

    public String update(Integer id, CategoryRequestDTO dto) {
        Optional<CategoryEntity> optional = getById(id);
        if (optional.isEmpty()) {
            throw new AppBadRequestException("Id Not Found");
        }

        if (getByName(dto.getName()).isPresent()) {
            throw new CategoryAlredyExistsException("Category alredy exists");
        }

        CategoryEntity category = optional.get();
        category.setName(dto.getName());

        categoryRepository.save(category);
        return "Success";
    }

    public String delete(Integer id) {
        Optional<CategoryEntity> optional = getById(id);
        if (optional.isEmpty()) {
            throw new AppBadRequestException("Id Not Found");
        }
        CategoryEntity entity = optional.get();
        categoryRepository.delete(entity);
        return "Success";
    }

    public Optional<CategoryEntity> getById(Integer id){
        return categoryRepository.findById(id);
    }

    public Optional<CategoryEntity> getByName(String name){
        return categoryRepository.findByName(name);
    }

    public CategoryResponceDTO toDto(CategoryEntity entity) {
        CategoryResponceDTO dto = new CategoryResponceDTO();
        dto.setName(entity.getName());
        dto.setId(entity.getId());
        dto.setCreatedDate(entity.getCreatedDate());
        return dto;
    }

}
