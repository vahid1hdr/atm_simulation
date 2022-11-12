package com.egs.eval.atm.service;

import com.egs.eval.atm.service.model.TransactionResult;

import java.util.List;

public interface TransactionService {
    List<Integer> getPredefinedValues();

    TransactionResult withdraw(Integer value, String userId);

    TransactionResult deposit(Integer value, String userId);

    TransactionResult rollback(String transactionId);

    long getUserBalance(String userId);
}
