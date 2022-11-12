package com.egs.eval.atm.dal.repository;

import com.egs.eval.atm.dal.entity.Transaction;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
    @Aggregation(pipeline = {
            "{$match:{'userId' : ?0}}",
            "{$group :{'_id': null, 'total' : { $sum: '$value' } }}"
    })
    Long sumValues(String userId);

    Optional<Transaction> findByTransactionId(String transactionId);

    Optional<Transaction> findByRolledBackFor(String rolledBackFor);
}
