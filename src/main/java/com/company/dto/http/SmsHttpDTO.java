package com.company.dto.http;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SmsHttpDTO {
    private String key;
    private String phone;
    private String message;

}
