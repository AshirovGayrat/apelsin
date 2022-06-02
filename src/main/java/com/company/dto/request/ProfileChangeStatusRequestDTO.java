package com.company.dto.request;

import com.company.enums.ProfileStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileChangeStatusRequestDTO {
    ProfileStatus status;
}
