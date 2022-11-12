package com.egs.eval.atm.service.model;

import lombok.Data;

@Data
public class TransactionResult {
    private String transactionId;
    private Long balance;
    private Long value;
}
