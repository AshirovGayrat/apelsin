package com.company.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BaseDTO {
    private String id;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
