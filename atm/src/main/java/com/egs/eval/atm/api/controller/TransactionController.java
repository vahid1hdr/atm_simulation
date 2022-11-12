package com.egs.eval.atm.api.controller;

import com.egs.eval.atm.api.dto.BalanceResponse;
import com.egs.eval.atm.api.dto.PredefinedValueResponse;
import com.egs.eval.atm.api.dto.TransactionRequest;
import com.egs.eval.atm.api.dto.TransactionResponse;
import com.egs.eval.atm.api.facade.TransactionFacade;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transaction")
@Api(tags = "transaction apis")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionFacade facade;
    private final HttpServletRequest request;
    private static final String AUTHORIZATION_HEADER = "Authorization";

    @PostMapping("/withdraw")
    public TransactionResponse doWithdraw(@RequestBody TransactionRequest transactionRequest) {
        return facade.withdraw(transactionRequest, request.getHeader(AUTHORIZATION_HEADER));
    }

    @PostMapping("/deposit")
    public TransactionResponse doDeposit(@RequestBody TransactionRequest transactionRequest) {
        return facade.deposit(transactionRequest, request.getHeader(AUTHORIZATION_HEADER));
    }

    @PostMapping("/rollback/{transactionId}")
    public TransactionResponse doRollback(@PathVariable("transactionId") String transactionId) {
        return facade.rollback(transactionId);
    }

    @GetMapping("/withdraw/predefines")
    public List<PredefinedValueResponse> getPredefinedValues() {
        return facade.getPredefinedValueList();
    }

    @GetMapping("/balance")
    public BalanceResponse getBalance() throws InterruptedException {
        Thread.sleep(16000L);
        return facade.getBalance(request.getHeader(AUTHORIZATION_HEADER));
    }
}
