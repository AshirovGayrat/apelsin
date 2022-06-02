package com.company.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class AuthDTO {
    @NotNull
    @Size(min = 12,max = 12)
    private String phone;
    @NotNull
    @Size(min = 4)
    private String password;
}
