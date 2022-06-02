package com.company.controller;

import com.company.dto.request.MerchandRequestDTO;
import com.company.dto.response.MerchandResponceDTO;
import com.company.enums.ProfileRole;
import com.company.service.MerchandService;
import com.company.util.JwtUtil;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/v1/merchand")
@RequiredArgsConstructor
public class MerchandController {
    private final MerchandService merchandService;

    @ApiOperation(value = "Create ", notes = "Method Create merchand")
    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody @Valid MerchandRequestDTO dto,
                                    HttpServletRequest request){
        log.info("Create: {}", dto);
        JwtUtil.getIdFromHeader(request, ProfileRole.ADMIN);
        return ResponseEntity.ok(merchandService.create(dto));
    }

    @ApiOperation(value = "update ", notes = "Method update merchand")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id")String id,
                                    @RequestBody @Valid MerchandRequestDTO dto,
                                    HttpServletRequest request){
        log.info("Create: {}", dto);
        JwtUtil.getIdFromHeader(request, ProfileRole.ADMIN);
        return ResponseEntity.ok(merchandService.update(id, dto));
    }

    @ApiOperation(value = "delete ", notes = "Method delete merchand")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id")String id,HttpServletRequest request){
        JwtUtil.getIdFromHeader(request,ProfileRole.ADMIN);
        return ResponseEntity.ok(merchandService.delete(id));
    }

    @ApiOperation(value = "merchand List ", notes = "Method used for get merchandList")
    @GetMapping("/pagination")
    public ResponseEntity<PageImpl<MerchandResponceDTO>> merchandList(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                     @RequestParam(value = "size", defaultValue = "10") int size,
                                                                     HttpServletRequest request) {
        JwtUtil.getIdFromHeader(request);
        return ResponseEntity.ok(merchandService.merchandList(page, size));
    }
}
