package com.company.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class CategoryRequestDTO {
    @NotNull
    @Size(min = 3)
    private String nameUz;
    @NotNull
    @Size(min = 3)
    private String nameRu;
    @NotNull
    @Size(min = 3)
    private String nameEn;
    @NotNull
    private String key;
}
