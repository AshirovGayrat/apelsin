package com.company.service;

import com.company.dto.http.SmsHttpDTO;
import com.company.dto.http.SmsHttpResponseDTO;
import com.company.entity.SmsEntity;
import com.company.enums.SmsStatus;
import com.company.repository.SmsRepository;
import com.company.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class SmsService {
    @Autowired
    private SmsRepository smsRepository;

    public void sendSms(String phone) {
        SmsEntity sms = new SmsEntity();
        String code = RandomUtil.getRandomSmsCode();
        sms.setContent(code);
        sms.setPhone(phone);
        sms.setStatus(SmsStatus.NOT_USED);
        String message = "Mazgi (DML). Pytonni o'qishni boshlang. Kode: " + code;

        smsRepository.save(sms);
        this.sendSms(phone, message);
    }

    private void sendSms(String phone, String message) {
        RestTemplate restTemplate = new RestTemplate();

        SmsHttpDTO dto = new SmsHttpDTO();
        dto.setKey("131231231231231231231231231");
        dto.setMessage(message);
        dto.setPhone(phone);

       /* HttpEntity<SmsHttpDTO> requestBody = new HttpEntity<SmsHttpDTO>(dto);
        SmsHttpResponseDTO response = restTemplate.postForObject("https://api.smsfly.uz/", requestBody, SmsHttpResponseDTO.class);
        log.info("Sms sent: request {}, response {}", dto, response);*/

        log.info("Sms sent: request {}", dto);
    }
}
