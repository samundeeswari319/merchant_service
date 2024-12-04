package com.merchant.service.repository;

import com.merchant.service.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    @Query("{ 'mid': ?0, 'app_id': ?1 }")
    Page<User> findByMidAndAppId(String mid, String app_id, Pageable pageable);

    @Query("{ 'mid': ?0 }")
    Page<User> findByMid(String mid, Pageable pageable);

    @Query("{ 'user_id': ?0 }")
    User findByUserId(String user_id);

    @Query("{ 'user_details.mobile_number': ?0,'app_id': ?1 }")
    User findByMobileNumberAndAppId(String mobile_number,String app_id);

    @Query("{ 'mobile_number': ?0 }")
    User findByMobileNumber(String mobile_number);
}
