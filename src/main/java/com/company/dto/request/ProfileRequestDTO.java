package com.company.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ProfileRequestDTO {
    @NotNull
    private String name;
    @NotNull
    private String surname;
    @NotBlank(message = "mazgi")
    @Size(min = 12,max = 12)
    private String phone;

    private String attachId;

}
