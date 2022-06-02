package com.company.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Setter
@Getter
@ToString
public class RegistrationDto {
    @NotNull
    @Size(min = 3, max = 25, message = "name requaried")
    private String name;
    @NotNull
    @Size(min = 3, max = 25, message = "surname requaried")
    private String surname;
    @NotNull
    @NotBlank(message = "phone required")
    @Size(min = 12, max = 12, message = "phone requaired")
    private String phone;

    @NotNull
    @Size(min = 3, max = 25, message = "password requaried")
    private String password;
}
