package com.company.dto;

import com.company.enums.ProfileRole;
import com.company.enums.ProfileStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
public class ProfileResponceDto extends ProfileRequestDTO{
    private String id;
    private ProfileStatus status;
    private ProfileRole role;
    private Integer photoId;
    private LocalDateTime createdDate;

    private AttachSimpleDTO attachDto;
}
