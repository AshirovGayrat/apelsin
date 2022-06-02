package com.company.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CardRequestDTO {
    @NotNull
    private String name;
    @NotNull
    @Size(min = 16, max = 16)
    private String number;
    @NotNull
    private String expDate;
}
