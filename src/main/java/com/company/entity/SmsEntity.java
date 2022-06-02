package com.company.entity;

import com.company.enums.SmsStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "sms_table")
public class SmsEntity extends BaseEntity {
    private String phone;
    private String content;
    private SmsStatus status;
}
