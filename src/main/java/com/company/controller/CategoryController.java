package com.company.controller;

import com.company.dto.request.CategoryRequestDTO;
import com.company.dto.request.MerchandRequestDTO;
import com.company.dto.request.ProfileChangeStatusRequestDTO;
import com.company.dto.request.ProfileRequestDTO;
import com.company.dto.response.CategoryResponseDTO;
import com.company.dto.response.MerchandResponceDTO;
import com.company.enums.ProfileRole;
import com.company.service.CategoryService;
import com.company.service.ProfileService;
import com.company.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@Api(tags = "Category")
@RestController
@RequestMapping("/v1/category")
public class CategoryController {
    private final CategoryService categoryService;

    @ApiOperation(value = "Create ", notes = "Method: Create Category")
    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody @Valid CategoryRequestDTO requestDTO,
                                    HttpServletRequest request) {
        log.info("Create: {}", requestDTO);
        JwtUtil.getIdFromHeader(request,ProfileRole.ADMIN);
        return ResponseEntity.ok(categoryService.create(requestDTO));
    }

    @ApiOperation(value = "update ", notes = "Method update category")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id")String id,
                                    @RequestBody @Valid CategoryRequestDTO dto,
                                    HttpServletRequest request){
        log.info("update: {}", dto);
        JwtUtil.getIdFromHeader(request, ProfileRole.ADMIN);
        return ResponseEntity.ok(categoryService.update(id, dto));
    }

    @ApiOperation(value = "Get by id", notes = "Method: by Category id")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") String id) {
        log.info("Get by id: {}", id);
        return ResponseEntity.ok(categoryService.getById(id));
    }

    @ApiOperation(value = "Category List ", notes = "Method used for get Category List")
    @GetMapping("/pagination")
    public ResponseEntity<PageImpl<CategoryResponseDTO>> getAll(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                @RequestParam(value = "size", defaultValue = "10") int size,
                                                                HttpServletRequest request) {
        JwtUtil.getIdFromHeader(request);
        return ResponseEntity.ok(categoryService.categoryList(page, size));
    }

    @ApiOperation(value = "Delete by id", notes = "Method: Delete by Category id")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") String id,HttpServletRequest request) {
        log.info("Delete by id: {}", id);
        JwtUtil.getIdFromHeader(request);
        return ResponseEntity.ok(categoryService.delete(id));
    }


}
