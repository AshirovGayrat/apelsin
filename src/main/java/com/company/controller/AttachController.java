package com.company.controller;

import com.company.dto.AttachDto;
import com.company.service.AttachService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/v1/attach")
public class AttachController {
    @Autowired
    private AttachService attachService;

    @ApiOperation(value = "create", notes = "Method for upload attach", nickname = "Mazgi")
    @PostMapping("")
    public ResponseEntity<AttachDto> create(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(attachService.upload(file));
    }

    @ApiOperation(value = "open_general", notes = "Method for open attach", nickname = "Mazgi")
    @GetMapping(value = "/open_general/{id}", produces = MediaType.ALL_VALUE)
    public byte[] open_general(@PathVariable("id") String id) {
        return attachService.open_general(id);
    }

    @GetMapping("/list")
    public ResponseEntity<?> list(@RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "size", defaultValue = "3") int size) {
        return ResponseEntity.ok(attachService.paginationList(page, size));
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<?> delete(@PathVariable("key") String key) {
        return ResponseEntity.ok(attachService.delete(key));
    }
}