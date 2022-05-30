package com.company.entity;

import com.company.enums.CardStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "card_table")
public class CardEntity extends BaseEntity{
    private String name;
    private String number;
    @Enumerated(EnumType.STRING)
    private CardStatus status;

    @Column(name = "profile_id")
    private String profileId;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", insertable = false, updatable = false)
    private ProfileEntity profile;
}
