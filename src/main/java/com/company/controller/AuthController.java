package com.company.controller;

import com.company.dto.RegistrationDto;
import com.company.dto.SmsDTO;
import com.company.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/auth")
@Api(tags = "For Authorization")
public class AuthController {
    @Autowired
    private AuthService authService;

    @ApiOperation(value = "registration", notes = "Method for registration", nickname = "Mazgi")
    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody @Valid RegistrationDto dto){
        log.info("registration: {}", dto);
        authService.registration(dto);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "activisation", notes = "Method for activisation", nickname = "Mazgi")
    @PostMapping("/activisation")
    public ResponseEntity<?> activisation(@RequestBody @Valid SmsDTO dto){
        log.info("registration: {}", dto);
        return ResponseEntity.ok(authService.activisation(dto));
    }
}
