package com.company.dto.response;

import com.company.dto.request.CategoryRequestDTO;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class CategoryResponseDTO   extends CategoryRequestDTO {
    private  String id;
    private  LocalDateTime createdDate;
    private  Boolean visible;
}
