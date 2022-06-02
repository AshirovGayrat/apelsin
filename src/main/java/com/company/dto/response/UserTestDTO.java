package com.company.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserTestDTO {
    private Integer id;
    private String name;
    private String email;
    private String gender;
    private String status;
}
