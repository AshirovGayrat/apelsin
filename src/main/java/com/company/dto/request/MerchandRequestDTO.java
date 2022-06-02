package com.company.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class MerchandRequestDTO {
    @NotNull
    @Size(min = 3)
    private String name;
    @NotNull
    @Size(min = 16, max = 16)
    private String cardNumber;
    @NotNull
    private Integer persent;
    private String attachId;
}
