package com.company;

import com.company.dto.response.UserTestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
class ApelsinApplicationTests {

    @Test
    void contextLoads() {
    }
    @Test
    void post1() {
        RestTemplate restTemplate=new RestTemplate();
        UserTestDTO dto=new UserTestDTO();
        dto.setName("Ali");
        dto.setEmail("aliyev@gmail.com");
        dto.setStatus("active");
        dto.setGender("male");

        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth("");

        HttpEntity<UserTestDTO> request=new HttpEntity<UserTestDTO>(dto, headers);
    }

}
