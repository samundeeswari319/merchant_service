package com.merchant.service.repository;


import com.merchant.service.model.AdminData;
import com.merchant.service.model.Merchant;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminUserRepository extends MongoRepository<AdminData,String> {
    @Query("{ 'data_id': ?0 }")
    AdminData findByDataId(String dataId);
}
