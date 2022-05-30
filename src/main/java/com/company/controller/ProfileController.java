package com.company.controller;

import com.company.dto.ChangePswdDTO;
import com.company.dto.ProfileRequestDTO;
import com.company.service.ProfileService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/profile")
public class ProfileController {
    @Autowired
    private ProfileService profileService;

    @ApiOperation(value = "create", notes = "Method for create profile", nickname = "Mazgi")
    @PostMapping("/adm")
    public ResponseEntity<?> createProfile(@RequestBody ProfileRequestDTO dto) {
        return ResponseEntity.ok(profileService.createProfile(dto));
    }

    @ApiOperation(value = "update", notes = "Method for update profile", nickname = "Mazgi")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProfile( @PathVariable("id")String id,
                                            @RequestBody @Valid ProfileRequestDTO dto) {
        log.info("update profile: {}", "id: " + id + " " + dto);
        return ResponseEntity.ok(profileService.updateProfile(id, dto));
    }

    @ApiOperation(value = "update", notes = "Method for update profile Image", nickname = "Mazgi")
    @PutMapping("/{file}")
    public ResponseEntity<?> updateImage(@RequestParam MultipartFile file,
                                         @RequestParam("pid") String pid) {
        log.info("update profile: {}", "pid: " + pid);
        try {
            return ResponseEntity.ok(profileService.updateImage(file, pid));
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Attach not found");
        }
    }

    @DeleteMapping("/{pid}")
    public ResponseEntity<?> deleteImage(@PathVariable("pid")String pid) {
        return ResponseEntity.ok(profileService.deleteImage(pid));
    }


    // ADMIN
    @DeleteMapping("/adm/{id}")
    public ResponseEntity<?> deleteProfile(@PathVariable("id") String id) {
        return ResponseEntity.ok(profileService.delete(id));
    }

    @GetMapping("")
    public ResponseEntity<?> getPaginationList(@RequestParam(value = "page", defaultValue = "0") int page,
                                               @RequestParam(value = "size", defaultValue = "3") int size) {
        return ResponseEntity.ok(profileService.paginationList(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") String id) {
        return ResponseEntity.ok(profileService.getById(id));
    }

}
