package com.egs.eval.client.external;

import com.egs.eval.client.external.model.BalanceResponse;
import com.egs.eval.client.external.model.PredefinedValueResponse;
import com.egs.eval.client.external.model.TransactionRequest;
import com.egs.eval.client.external.model.TransactionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "bank-transaction-client", url = "${bank.transaction.base.url}", configuration = DefaultFeignErrorDecoder.class)
public interface BankTransactionApiClient {

    @PostMapping("${bank.transaction.withdraw.url}")
    TransactionResponse doWithdraw(@RequestBody TransactionRequest transactionRequest,
                                   @RequestHeader("Authorization") String authHeader);

    @PostMapping("${bank.transaction.deposit.url}")
    TransactionResponse doDeposit(@RequestBody TransactionRequest transactionRequest,
                                  @RequestHeader("Authorization") String authHeader);

    @PostMapping("${bank.transaction.rollback.url}")
    TransactionResponse doRollback(@PathVariable("transactionId") String transactionId,
                                   @RequestHeader("Authorization") String authHeader);

    @GetMapping("${bank.transaction.withdraw.predefines.url}")
    List<PredefinedValueResponse> getPredefinedValues(@RequestHeader("Authorization") String authHeader);

    @GetMapping("${bank.transaction.balance.url}")
    BalanceResponse getBalance(@RequestHeader("Authorization") String authHeader);
}
