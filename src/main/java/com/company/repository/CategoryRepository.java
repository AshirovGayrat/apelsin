package com.company.repository;

import com.company.entity.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, String> {
    Optional<CategoryEntity> findByIdAndVisible(String id, boolean b);

    Optional<CategoryEntity> findByNameUzAndVisible(String nameUz, boolean b);

    Page<CategoryEntity> findAllByVisible(Boolean b, Pageable pageable);

    @Transactional
    @Modifying
    @Query("update CategoryEntity set visible = :visible where id = :id")
    int deleteStatus(@Param("visible") boolean b, @Param("id") String id);
}