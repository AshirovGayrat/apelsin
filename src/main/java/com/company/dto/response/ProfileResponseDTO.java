package com.company.dto.response;

import com.company.dto.AttachSimpleDTO;
import com.company.dto.request.ProfileRequestDTO;
import com.company.enums.ProfileStatus;
import com.company.enums.StatusEnum;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class ProfileResponseDTO extends ProfileRequestDTO {
    private String id;
    private LocalDateTime createdDate;
    private ProfileStatus status;
    private String jwt;
    private AttachSimpleDTO attach;
}
