package com.company.entity;

import com.company.enums.CardStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "card_apelsin")
public class CardEntity extends BaseEntity {
    @Column
    private String name;
    @Column
    private String number;
    @Column
    private Long balance = 0l;
    @Column
    private String phone;

    @Column(name = "profile_id")
    private String profileId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", insertable = false, updatable = false)
    private ProfileEntity profile;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column
    @Enumerated(EnumType.STRING)
    private CardStatus status;
}
