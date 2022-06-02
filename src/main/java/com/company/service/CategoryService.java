package com.company.service;

import com.company.dto.request.CategoryRequestDTO;
import com.company.dto.request.MerchandRequestDTO;
import com.company.dto.response.CategoryResponseDTO;
import com.company.dto.response.MerchandResponceDTO;
import com.company.entity.CategoryEntity;
import com.company.entity.MerchandEntity;
import com.company.exception.ItemNotFoundException;
import com.company.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryResponseDTO create(CategoryRequestDTO requestDTO) {
        Optional<CategoryEntity> optional = categoryRepository.findByNameUzAndVisible(requestDTO.getNameUz(), true);
        if (optional.isPresent()) {
            log.info("Category name already exists");
            throw new ItemNotFoundException("Category name already exists");
        }
        CategoryEntity entity = new CategoryEntity();
        entity.setNameEn(requestDTO.getNameEn());
        entity.setNameUz(requestDTO.getNameUz());
        entity.setNameRu(requestDTO.getNameRu());
        entity.setKey(requestDTO.getKey());

        categoryRepository.save(entity);
        return toDTO(entity);
    }

    public CategoryResponseDTO update(String id, CategoryRequestDTO dto) {
        if (dto.getNameUz() != null) {
            Optional<CategoryEntity> optional = categoryRepository.findByNameUzAndVisible(dto.getNameUz(),true);
            if (optional.isPresent()) {
                log.warn("Category name already exists");
                throw new ItemNotFoundException("Category name already exists");
            }
        }
        CategoryEntity entity = get(id);
        entity.setNameEn(dto.getNameEn());
        entity.setNameUz(dto.getNameUz());
        entity.setNameRu(dto.getNameRu());
        entity.setKey(dto.getKey());

        return toDTO(entity);
    }

    public CategoryResponseDTO getById(String id) {
        CategoryEntity entity = categoryRepository.findByIdAndVisible(id, true).orElseThrow(() -> {
            log.info("Category id not found");
            throw new ItemNotFoundException("Category id not found");
        });
        return toDTO(entity);
    }

    public CategoryEntity get(String id) {
        return categoryRepository.findByIdAndVisible(id, true).orElseThrow(() -> {
            log.info("Category id not found");
            throw new ItemNotFoundException("Category id not found");
        });
    }

    public PageImpl<CategoryResponseDTO> categoryList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        Page<CategoryEntity> pages = categoryRepository.findAllByVisible(true, pageable);
        List<CategoryEntity> entityList = pages.getContent();

        List<CategoryResponseDTO> dtoList = new LinkedList<>();
        entityList.forEach(entity -> {
            dtoList.add(toDTO(entity));
        });
        return new PageImpl<CategoryResponseDTO>(dtoList, pageable, pages.getTotalElements());
    }

    public Boolean delete(String id) {
        getById(id);//check
        int n = categoryRepository.deleteStatus(false, id);
        return n > 0;
    }

    private CategoryResponseDTO toDTO(CategoryEntity entity) {
        CategoryResponseDTO responseDTO = new CategoryResponseDTO();
        responseDTO.setId(entity.getId());
        responseDTO.setNameUz(entity.getNameUz());
        responseDTO.setNameRu(entity.getNameRu());
        responseDTO.setNameEn(entity.getNameEn());
        responseDTO.setKey(entity.getKey());
        responseDTO.setCreatedDate(entity.getCreatedDate());
        return responseDTO;
    }
}
