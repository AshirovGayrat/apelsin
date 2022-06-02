package com.company.controller;

import com.company.dto.RegistrationDto;
import com.company.dto.SmsDTO;
import com.company.dto.request.AuthDTO;
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
@RequestMapping("/v1/auth")
@Api(tags = "For Authorization")
public class AuthController {
    @Autowired
    private AuthService authService;

    @ApiOperation(value = "registration", notes = "Method for registration", nickname = "Mazgi")
    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody @Valid RegistrationDto dto) {
        log.info("registration: {}", dto);
        authService.registration(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    private ResponseEntity<?> login(@RequestBody AuthDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @ApiOperation(value = "activation", notes = "Method for activation", nickname = "Mazgi")
    @PostMapping("/activation")
    public ResponseEntity<?> activisation(@RequestBody @Valid SmsDTO dto) {
        log.info("registration: {}", dto);
        return ResponseEntity.ok(authService.activation(dto));
    }

    @PostMapping("/resend")
    private ResponseEntity<?> resendSms(@RequestBody AuthDTO dto) {
        authService.resendSmsCode(dto.getPhone());
        return ResponseEntity.ok().build();
    }

}
