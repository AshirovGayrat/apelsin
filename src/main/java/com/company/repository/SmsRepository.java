package com.company.repository;

import com.company.entity.SmsEntity;
import com.company.enums.SmsStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface SmsRepository extends JpaRepository<SmsEntity, String> {
    Optional<SmsEntity> findByPhoneAndContent(String phone, String sms);

    Optional<SmsEntity> findTopByPhoneAndStatusOrderByCreatedDateDesc(String phone, SmsStatus status);


    @Modifying
    @Transactional
    @Query("Update SmsEntity set status = :status where id =:id")
    void updateSmsStatus(@Param("status") SmsStatus status, @Param("id") String id);
}