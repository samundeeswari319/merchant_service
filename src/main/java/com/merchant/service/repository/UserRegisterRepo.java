package com.merchant.service.repository;

import com.merchant.service.model.UserRegisterData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRegisterRepo extends MongoRepository<UserRegisterData, String> {
    @Query("{ 'mid': ?0 }")
    UserRegisterData findByMid(String mid);
}
