package com.company.controller;

import com.company.dto.request.ProfileChangeStatusRequestDTO;
import com.company.dto.request.ProfileRequestDTO;
import com.company.enums.ProfileRole;
import com.company.service.ProfileService;
import com.company.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@Api(tags = "Profile")
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/profile")
public class ProfileController {
    private final ProfileService profileService;

    @ApiOperation(value = "Create ", notes = "Method: Create Profile")
    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody @Valid ProfileRequestDTO requestDTO, HttpServletRequest request) {
        log.info("Create: {}", requestDTO);
        JwtUtil.getIdFromHeader(request);
        return ResponseEntity.ok(profileService.create(requestDTO));
    }

    @ApiOperation(value = "Get by id", notes = "Method: by Profile id")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") String id) {
        log.info("Get by id: {}", id);
        return ResponseEntity.ok(profileService.getById(id));
    }

    @ApiOperation(value = "Get all", notes = "Method: Profile get all")
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        log.info("Get all: {}");
        return ResponseEntity.ok(profileService.getAll());
    }

    @ApiOperation(value = "Delete by id", notes = "Method: Delete by Profile id")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") String id, HttpServletRequest request) {
        log.info("Delete by id: {}", id);
        JwtUtil.getIdFromHeader(request);
        return ResponseEntity.ok(profileService.delete(id));
    }

    @ApiOperation(value = "Change Status", notes = "Method: by Profile id")
    @PutMapping("/{id}")
    public ResponseEntity<?> changeStatus(@PathVariable("id") String id,
                                          @RequestBody ProfileChangeStatusRequestDTO requestDTO,
                                          HttpServletRequest request) {
        log.info("Change status by id: {},{}", requestDTO, id);
        JwtUtil.getIdFromHeader(request, ProfileRole.ADMIN);
        return ResponseEntity.ok(profileService.changeStatus(id, requestDTO));
    }
}
