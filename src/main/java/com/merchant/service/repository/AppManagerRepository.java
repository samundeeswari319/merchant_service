package com.merchant.service.repository;


import com.merchant.service.entity.AppManager;
import com.merchant.service.entity.Authentication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppManagerRepository extends JpaRepository<AppManager, Long> {
    @Query("SELECT u FROM AppManager u WHERE u.mid = :mid AND u.id = :id")
    AppManager findByTokenAndId(@Param("mid") String mid, @Param("id") String id);
}
