package com.company.entity;

import com.company.enums.ProfileStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "profile_table")
public class ProfileEntity extends BaseEntity{
    private String surname;
    private String name;
    private String phone;

    @Enumerated(EnumType.STRING)
    private ProfileStatus status;

    @Column(name = "attach_id")
    private String attachId;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attach_id", insertable = false, updatable = false)
    private AttachEntity attach;

    private Boolean visible=true;
}
