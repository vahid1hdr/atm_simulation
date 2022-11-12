package com.egs.eval.client.api.controller;

import com.egs.eval.client.external.model.BalanceResponse;
import com.egs.eval.client.external.model.PredefinedValueResponse;
import com.egs.eval.client.external.model.TransactionRequest;
import com.egs.eval.client.external.model.TransactionResponse;
import com.egs.eval.client.service.TransactionProxy;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/public/v1/transaction")
@Api(tags = "transaction apis")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionProxy proxy;
    private final HttpServletRequest request;
    private static final String AUTHORIZATION_HEADER = "Authorization";

    @PostMapping("/withdraw")
    public TransactionResponse doWithdraw(@RequestBody TransactionRequest transactionRequest) {
        return proxy.withdraw(request.getHeader(AUTHORIZATION_HEADER), transactionRequest);
    }

    @PostMapping("/deposit")
    public TransactionResponse doDeposit(@RequestBody TransactionRequest transactionRequest) {
        return proxy.deposit(request.getHeader(AUTHORIZATION_HEADER), transactionRequest);
    }

    @PostMapping("/rollback/{transactionId}")
    public TransactionResponse doRollback(@PathVariable("transactionId") String transactionId) {
        return proxy.rollback(request.getHeader(AUTHORIZATION_HEADER), transactionId);
    }

    @GetMapping("/withdraw/predefines")
    public List<PredefinedValueResponse> getPredefinedValues() {
        return proxy.getPredefinedValues(request.getHeader(AUTHORIZATION_HEADER));
    }

    @SneakyThrows
    @GetMapping("/balance")
    public BalanceResponse getBalance() {
        return proxy.getBalance(request.getHeader(AUTHORIZATION_HEADER));
    }
}
