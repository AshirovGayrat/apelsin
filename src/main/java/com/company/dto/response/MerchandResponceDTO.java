package com.company.dto.response;

import com.company.dto.AttachSimpleDTO;
import com.company.dto.request.MerchandRequestDTO;
import com.company.enums.MerchandStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MerchandResponceDTO extends MerchandRequestDTO {
    private String id;
    private LocalDateTime createdDate;
    private MerchandStatus status;
    private AttachSimpleDTO attach;
}
