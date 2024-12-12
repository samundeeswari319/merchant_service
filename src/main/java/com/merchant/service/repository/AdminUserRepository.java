package com.merchant.service.repository;


import com.merchant.service.model.AdminData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminUserRepository extends MongoRepository<AdminData,String> {
}
