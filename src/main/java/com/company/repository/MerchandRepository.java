package com.company.repository;

import com.company.entity.MerchandEntity;
import com.company.enums.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Optional;

public interface MerchandRepository extends JpaRepository<MerchandEntity, String> {
    Optional<MerchandEntity> findByNameAndVisible(String name, Boolean visible);

    Optional<MerchandEntity> findByIdAndVisible(String id, Boolean visible);

    Page<MerchandEntity> findAllByVisible(Boolean visible, Pageable pageable);

    @Transactional
    @Modifying
    @Query("update MerchandEntity set visible = :visible where id = :id")
    int updateVisible(@Param("visible") Boolean visible, @Param("id") String id);
}