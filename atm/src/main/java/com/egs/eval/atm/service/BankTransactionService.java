package com.egs.eval.atm.service;

import com.egs.eval.atm.dal.entity.Transaction;
import com.egs.eval.atm.dal.repository.TransactionRepository;
import com.egs.eval.atm.service.exception.ConflictException;
import com.egs.eval.atm.service.exception.NotFoundException;
import com.egs.eval.atm.service.exception.PreconditionFailedException;
import com.egs.eval.atm.service.mapper.TransactionMapper;
import com.egs.eval.atm.service.model.TransactionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BankTransactionService implements TransactionService {

    private final TransactionRepository repository;
    private final TransactionMapper mapper;

    @Override
    public List<Integer> getPredefinedValues() {
        return List.of(10000, 20000, 30000, 50000, 100000);
    }

    @Override
    public TransactionResult withdraw(Integer value, String userId) {
        long balance = getBalanceByCheckingWithdrawCondition(userId, value);
        Transaction transaction = mapper.getTransaction(userId, value * -1, UUID.randomUUID().toString());
        repository.save(transaction);
        return mapper.getTransactionResult(transaction, balance - value);
    }

    @Override
    public TransactionResult deposit(Integer value, String userId) {
        long balance = getUserBalanceByNullConsideration(userId);
        Transaction transaction = mapper.getTransaction(userId, value, UUID.randomUUID().toString());
        repository.save(transaction);
        return mapper.getTransactionResult(transaction, balance + value);
    }

    @Override
    public TransactionResult rollback(String transactionId) {
        return repository.findByTransactionId(transactionId)
                .map(this::doRollbackTransaction)
                .orElseThrow(() -> {
                    throw new NotFoundException("transactionId not found.");
                });
    }

    @Override
    public long getUserBalance(String userId) {
        return getUserBalanceByNullConsideration(userId);
    }

    private long getBalanceByCheckingWithdrawCondition(String userId, Integer value) {
        long balance = getUserBalanceByNullConsideration(userId);
        if (balance < value)
            throw new PreconditionFailedException("your balance is not enough. balance: " + balance);
        return balance;
    }

    private long getUserBalanceByNullConsideration(String userId) {
        Long result = repository.sumValues(userId);
        return Objects.isNull(result) ? 0 : result;
    }

    private TransactionResult doRollbackTransaction(Transaction transaction) {
        checkForRolledBackBefore(transaction.getTransactionId());
        long balance = getUserBalanceByNullConsideration(transaction.getUserId());
        Transaction rollbackTransaction = mapper.getRollbackTransactionFromTransaction(
                transaction, UUID.randomUUID().toString());
        rollbackTransaction = repository.save(rollbackTransaction);
        return mapper.getTransactionResult(rollbackTransaction, balance + rollbackTransaction.getValue());
    }

    private void checkForRolledBackBefore(String transactionId) {
        repository.findByRolledBackFor(transactionId).ifPresent(transaction -> {
            throw new ConflictException("this transaction rolledBack once before!");
        });
    }
}
