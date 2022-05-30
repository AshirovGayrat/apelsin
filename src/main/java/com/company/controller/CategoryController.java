package com.company.controller;

import com.company.dto.CategoryRequestDTO;
import com.company.service.CategoryService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @ApiOperation(value = "create", notes = "Method for create category", nickname = "Mazgi")
    @PostMapping("/adm")
    public ResponseEntity<?> create(@RequestBody CategoryRequestDTO dto) {
        log.info("Create category: {}", dto);
        return ResponseEntity.ok(categoryService.create(dto));
    }

    @GetMapping("/adm/pagination")
    public ResponseEntity<?> getAll(@RequestParam(value = "page", defaultValue = "0")int page,
                                    @RequestParam(value = "size", defaultValue = "10")int size){
        return ResponseEntity.ok(categoryService.getAllWithPagination(page, size));
    }

    @ApiOperation(value = "update", notes = "Method for update category", nickname = "Mazgi")
    @PutMapping("/adm/{id}")
    public ResponseEntity<?> update(@PathVariable("id") String id, @RequestBody CategoryRequestDTO dto) {
        log.info("update category: {}", dto);
        return ResponseEntity.ok(categoryService.update(id, dto));
    }

    @DeleteMapping("/adm/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
        return ResponseEntity.ok(categoryService.delete(id));
    }

}
