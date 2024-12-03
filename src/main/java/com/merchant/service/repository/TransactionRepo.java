package com.merchant.service.repository;

import com.merchant.service.impl.TransactionRepositoryCustom;
import com.merchant.service.model.TransactionModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;


public interface TransactionRepo extends MongoRepository<TransactionModel, String> , TransactionRepositoryCustom {
    //List<TransactionModel> findAllByUserId(long userId);
   /* @Query("{ 'userId': ?0, '$or': [ { 'transactionDate': ?1 }, { 'transactionDate': { '$exists': false } } , { 'transactionDate': null } ] }")
    List<TransactionModel> findByUserIdAndDate(@Param("userId") long userId, @Param("transactionDate") String transactionDate);*/

//    TransactionModel findAllByReferenceNumber(String reference_number);
    @Query("{ 'reference_number': ?0 }")
    TransactionModel findAllByReference_number(String reference_number);
    @Query("{ 'reference_number': ?0 }")
    boolean existsByReference_number(String reference_number);

    //Emp.ID : NS00047
    @Query("{ 'reference_number': ?0 }")
    TransactionModel findByReferenceNumber(String referenceNumber);

    @Query("{ 'reference_number': { $in: ?0 } }")
    List<TransactionModel> findByReferenceNumberIn(List<String> referenceNumbers);


}
