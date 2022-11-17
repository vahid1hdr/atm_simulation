package com.egs.eval.atm.dal.repository;

import com.egs.eval.atm.dal.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    @Query(value = "SELECT sum(value) FROM Transaction where userId=:userId")
    Long sumValues(String userId);

    Optional<Transaction> findByTransactionId(String transactionId);

    Optional<Transaction> findByRolledBackFor(String rolledBackFor);
}
