package com.company.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryRequestDTO {
    private String nameUz;
    private String nameEn;
    private String nameRu;
    private String key;
}
