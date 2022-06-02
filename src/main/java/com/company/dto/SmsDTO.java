package com.company.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class SmsDTO {
    @NotNull
    @Size(min = 12, max = 12)
    private String phone;
    @NotNull
    @Size(min = 5, max = 5)
    private String sms;
}
