package com.company.dto.http;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class SmsHttpResponseDTO {
    private Boolean success;
    private String reason;
    private Integer resultCode;
}
