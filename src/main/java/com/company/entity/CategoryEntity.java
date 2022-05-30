package com.company.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Setter
@Getter
@Entity
@Table(name = "category")
public class CategoryEntity extends BaseEntity{

    @Column(name = "name_uz",nullable = false, unique = true)
    private String nameUz;
    @Column(name = "name_ru",nullable = false, unique = true)
    private String nameRu;
    @Column(name = "name_en",nullable = false, unique = true)
    private String nameEn;

    private String key;

    private Boolean visible;
}
