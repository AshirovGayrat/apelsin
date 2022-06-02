package com.company.controller;

import com.company.config.JwtFilter;
import com.company.dto.SmsDTO;
import com.company.dto.request.AuthDTO;
import com.company.dto.request.CardAssignRequestDTO;
import com.company.dto.request.CardRequestDTO;
import com.company.enums.CardStatus;
import com.company.service.CardService;
import com.company.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@Api(tags = "Card")
@RestController
@RequestMapping("/v1/card")
public class CardController {
    @Autowired
    private CardService cardService;

    @ApiOperation(value = "Create ", notes = "Method Create Card")
    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody @Valid CardRequestDTO requestDTO, HttpServletRequest request) {
        log.info("Create: {}", requestDTO);
        String profileId = JwtUtil.getIdFromHeader(request);
        return ResponseEntity.ok(cardService.create(requestDTO,profileId));
    }


    @ApiOperation(value = "Get by id", notes = "Method get By id")
//    @PreAuthorize("hasRole('BANK')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") String id) {
        log.info("Get by id: {}", id);
        return ResponseEntity.ok(cardService.getById(id));
    }

    @ApiOperation(value = "Card List", notes = "Method get Card List By Profile id")
    @GetMapping("")
    public ResponseEntity<?> getCardListByProfileId(HttpServletRequest request) {
        String profileId=JwtUtil.getIdFromHeader(request);
        log.info("Get by id: {}", profileId);
        return ResponseEntity.ok(cardService.getCardListByProfileId(profileId));
    }

    @ApiOperation(value = "Get by Card number", notes = "Method get By Card number")
    @GetMapping("/getByCardNumber/{id}")
    public ResponseEntity<?> getByCardNumber(@PathVariable("id") String id) {
        log.info("Get by card number: {}", id);
        return ResponseEntity.ok(cardService.getByCardNumber(id));
    }

    @ApiOperation(value = "Get by Card number Balance", notes = "Method get By Card number Balance")
    @GetMapping("/getBalance/{number}")
    public ResponseEntity<?> getBalance(@PathVariable("number") String number) {
        log.info("Get balance by card number: {}", number);
        return ResponseEntity.ok(cardService.getBalance(number));
    }


    @ApiOperation(value = "Cheng Status", notes = "Method Cheng Status by id Active")
//    @PreAuthorize("hasRole('BANK')")
    @PutMapping("/cheng-status/{id}")
    public ResponseEntity<?> chengStatus(@PathVariable("id") String id,
                                         @RequestParam("status")CardStatus status) {
        log.info("Chang status : {}", id);
        return ResponseEntity.ok(cardService.changeStatus(status, id));
    }

    @ApiOperation(value = "Cheng Status", notes = "Method Cheng Status by id Active")
//    @PreAuthorize("hasRole('BANK')")
    @PutMapping("/assignPhone/{id}")
    public ResponseEntity<?> assignPhone(@PathVariable("id") String id,
                                         @RequestBody @Valid CardAssignRequestDTO requestDTO) {
        log.info("Assign phone: {},{}", requestDTO, id);
        return ResponseEntity.ok(cardService.assignPhone(requestDTO.getPhone(), id));
    }

    @ApiOperation(value = "activation", notes = "Method for activation", nickname = "Mazgi")
    @PostMapping("/activation")
    public ResponseEntity<?> activisation(@RequestBody @Valid SmsDTO dto) {
        log.info("registration: {}", dto);
        return ResponseEntity.ok(cardService.activation(dto));
    }

    @PostMapping("/resend")
    private ResponseEntity<?> resendSms(@RequestBody AuthDTO dto) {
        cardService.resendSmsCode(dto.getPhone());
        return ResponseEntity.ok().build();
    }
}
