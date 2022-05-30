package com.company.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class SmsDTO {
    @NotNull
    @Size(min = 13, max = 13)
    private String phone;
    @NotNull
    @Size(min = 6, max = 6)
    private int sms;
}
