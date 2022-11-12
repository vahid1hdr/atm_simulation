package com.egs.eval.atm.api.dto;

import lombok.Data;

@Data
public class TransactionResponse {
    private Integer value;
    private Long balance;
    private String transactionId;
}
