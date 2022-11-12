package com.egs.eval.client.service;

import com.egs.eval.client.external.model.BalanceResponse;
import com.egs.eval.client.external.model.PredefinedValueResponse;
import com.egs.eval.client.external.model.TransactionRequest;
import com.egs.eval.client.external.model.TransactionResponse;

import java.util.List;

public interface TransactionProxy {
    TransactionResponse withdraw(String token, TransactionRequest transactionRequest);
    TransactionResponse deposit(String token, TransactionRequest transactionRequest);
    TransactionResponse rollback(String token, String transactionId);
    List<PredefinedValueResponse> getPredefinedValues(String token);
    BalanceResponse getBalance(String token);
}
