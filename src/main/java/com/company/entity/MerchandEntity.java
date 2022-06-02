package com.company.entity;

import com.company.enums.MerchandStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "merchand")
public class MerchandEntity extends BaseEntity{
    private String name;
    @Column(name = "card_number")
    private String cardNumber;
    private Integer persentage;
    private MerchandStatus status;

    @Column(name = "attach_id")
    private String attachId;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attach_id", insertable = false, updatable = false)
    private AttachEntity attach;
}
