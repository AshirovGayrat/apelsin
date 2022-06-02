package com.company.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttachResponseDTO {
    private String id;
    private String path;

    private String extension;

    private String originalName;

    private Long fileSize;

    private String url;
    private LocalDateTime createdDate;

    public AttachResponseDTO(String url) {
        this.url = url;
    }
}
