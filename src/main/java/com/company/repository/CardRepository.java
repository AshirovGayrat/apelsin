package com.company.repository;

import com.company.entity.CardEntity;
import com.company.enums.CardStatus;
import com.company.enums.ProfileStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<CardEntity, String> {
    Optional<CardEntity> findByNumber(String number);

    Optional<CardEntity> findByPhoneAndVisible(String phone, Boolean visible);

    @Transactional
    @Modifying
    @Query("update CardEntity set status = :status where id = :cid")
    int chengStatus(@Param("status") CardStatus status, @Param("cid") String id);

    Optional<CardEntity> findByIdAndStatus(String id, CardStatus active);

    List<CardEntity> findAllByProfileId(String profileId);

    @Transactional
    @Modifying
    @Query("update CardEntity set phone = :phone where id = :cid")
    int assignPhone(@Param("phone") String phone, @Param("cid") String cid);

    @Query("select c.balance from CardEntity as c where c.number=:number")
    public Optional<Long> getBalance(@Param("number") String number);

    @Transactional
    @Modifying
    @Query("update CardEntity set balance = balance-:amount where id = :cid")
    int paymentMinus(@Param("amount") Long amount, @Param("cid") String cid);

    @Transactional
    @Modifying
    @Query("update CardEntity set balance = balance+:amount where id = :cid")
    int paymentPlus(@Param("amount") Long amount, @Param("cid") String cid);

    @Transactional
    @Modifying
    @Query("update CardEntity set status = :status where phone = :phone")
    int activation(@Param("status") CardStatus status, @Param("phone") String phone);
}