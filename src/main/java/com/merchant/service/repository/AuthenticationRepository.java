package com.merchant.service.repository;



import com.merchant.service.entity.Authentication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthenticationRepository extends JpaRepository<Authentication, Long> {
    @Query("SELECT u FROM Authentication u WHERE u.merchant_id = :merchant_id")
    Authentication findByMerchantId(@Param("merchant_id") String merchant_id);

    @Query("SELECT u FROM Authentication u WHERE u.token = :token")
    Authentication findByToken(@Param("token") String token);
}
