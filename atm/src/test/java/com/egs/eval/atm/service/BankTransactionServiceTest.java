package com.egs.eval.atm.service;

import com.egs.eval.atm.dal.entity.Transaction;
import com.egs.eval.atm.dal.repository.TransactionRepository;
import com.egs.eval.atm.service.exception.ConflictException;
import com.egs.eval.atm.service.exception.NotFoundException;
import com.egs.eval.atm.service.exception.PreconditionFailedException;
import com.egs.eval.atm.service.mapper.TransactionMapper;
import com.egs.eval.atm.service.model.TransactionResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankTransactionServiceTest {

    @InjectMocks
    private BankTransactionService bankTransactionService;
    @Mock
    private TransactionRepository repository;
    @Mock
    private TransactionMapper mapper;
    private final PodamFactory podamFactory = new PodamFactoryImpl();

    @Test
    void Given_Nothing_When_TryToGetPredefinedValues_Then_10000_20000_30000_50000_100000_WouldBeReturned() {
        List<Integer> predefinedValues = bankTransactionService.getPredefinedValues();
        assertThat(predefinedValues, hasSize(5));
        assertThat(predefinedValues, contains(10000, 20000, 30000, 50000, 100000));
    }

    @Test
    void Given_BalanceIsGreaterThanValue_When_TryToWithdraw_Then_SubtractionObBalanceAndValueShouldBeAsNewBalance() {
        String userId = podamFactory.manufacturePojo(String.class);
        int value = podamFactory.manufacturePojo(Integer.class);
        long balance = value + 50;
        Transaction transaction = podamFactory.manufacturePojo(Transaction.class);
        TransactionResult transactionResult = new TransactionResult();
        transactionResult.setTransactionId(transaction.getTransactionId());
        transactionResult.setBalance(balance - value);
        transactionResult.setValue((long) value);
        when(repository.sumValues(userId)).thenReturn(balance);
        when(mapper.getTransaction(any(), any(), any())).thenReturn(transaction);
        when(mapper.getTransactionResult(transaction, balance - value)).thenReturn(transactionResult);
        TransactionResult result = bankTransactionService.withdraw(value, userId);
        assertEquals(transactionResult.getTransactionId(), result.getTransactionId());
        assertEquals(balance - value, result.getBalance());
        assertEquals(value, result.getValue());
        verify(repository, times(1)).save(transaction);
    }

    @Test
    void Given_BalanceIsEqualToValue_When_TryToWithdraw_Then_ZeroShouldBeAsNewBalance() {
        String userId = podamFactory.manufacturePojo(String.class);
        int value = podamFactory.manufacturePojo(Integer.class);
        long balance = value;
        Transaction transaction = podamFactory.manufacturePojo(Transaction.class);
        TransactionResult transactionResult = new TransactionResult();
        transactionResult.setTransactionId(transaction.getTransactionId());
        transactionResult.setBalance(balance - value);
        transactionResult.setValue((long) value);
        when(repository.sumValues(userId)).thenReturn(balance);
        when(mapper.getTransaction(any(), any(), any())).thenReturn(transaction);
        when(mapper.getTransactionResult(transaction, balance - value)).thenReturn(transactionResult);
        TransactionResult result = bankTransactionService.withdraw(value, userId);
        assertEquals(transactionResult.getTransactionId(), result.getTransactionId());
        assertEquals(0, result.getBalance());
        assertEquals(value, result.getValue());
        verify(repository, times(1)).save(transaction);
    }

    @Test
    void Given_BalanceIsLessThanValue_When_TryToWithdraw_Then_PreconditionFailedExceptionWouldBeThrown() {
        String userId = podamFactory.manufacturePojo(String.class);
        int value = podamFactory.manufacturePojo(Integer.class);
        long balance = value - 1;
        when(repository.sumValues(userId)).thenReturn(balance);
        assertThrows(PreconditionFailedException.class, () -> bankTransactionService.withdraw(value, userId));
        verify(repository, times(0)).save(any());
    }

    @Test
    void Given_Nothing_When_TryToDeposit_Then_SumOfValueAndBalanceShouldBeAsNewBalance() {
        String userId = podamFactory.manufacturePojo(String.class);
        int value = podamFactory.manufacturePojo(Integer.class);
        long balance = podamFactory.manufacturePojo(Integer.class);
        Transaction transaction = podamFactory.manufacturePojo(Transaction.class);
        TransactionResult transactionResult = new TransactionResult();
        transactionResult.setTransactionId(transaction.getTransactionId());
        transactionResult.setBalance(balance + value);
        transactionResult.setValue((long) value);
        when(repository.sumValues(userId)).thenReturn(balance);
        when(mapper.getTransaction(any(), any(), any())).thenReturn(transaction);
        when(mapper.getTransactionResult(transaction, balance + value)).thenReturn(transactionResult);
        TransactionResult result = bankTransactionService.deposit(value, userId);
        assertEquals(transactionResult.getTransactionId(), result.getTransactionId());
        assertEquals(balance + value, result.getBalance());
        assertEquals(value, result.getValue());
        verify(repository, times(1)).save(transaction);
    }

    @Test
    void Given_TransactionExistsAndNotYetRollBack_When_TryToRollBack_Then_OppositeOfTransactionValuePlusBalanceShouldBeAsNewBalance() {
        long balance = podamFactory.manufacturePojo(Integer.class);
        Transaction transaction = podamFactory.manufacturePojo(Transaction.class);
        Transaction rollBackTransaction = podamFactory.manufacturePojo(Transaction.class);
        rollBackTransaction.setRolledBackFor(transaction.getTransactionId());
        rollBackTransaction.setValue(transaction.getValue() * -1);
        TransactionResult transactionResult = new TransactionResult();
        transactionResult.setTransactionId(rollBackTransaction.getTransactionId());
        transactionResult.setBalance(balance + rollBackTransaction.getValue());
        transactionResult.setValue(rollBackTransaction.getValue());
        when(repository.findByTransactionId(transaction.getTransactionId())).thenReturn(Optional.of(transaction));
        when(repository.save(rollBackTransaction)).thenReturn(rollBackTransaction);
        when(repository.sumValues(transaction.getUserId())).thenReturn(balance);
        when(mapper.getRollbackTransactionFromTransaction(any(), any())).thenReturn(rollBackTransaction);
        when(mapper.getTransactionResult(any(), anyLong())).thenReturn(transactionResult);
        TransactionResult result = bankTransactionService.rollback(transaction.getTransactionId());
        assertEquals(rollBackTransaction.getTransactionId(), result.getTransactionId());
        assertEquals(balance + rollBackTransaction.getValue(), result.getBalance());
        assertEquals(transaction.getValue() * -1, result.getValue());
        verify(repository, times(1)).save(rollBackTransaction);
    }

    @Test
    void Given_TransactionDoesNotExistsForRollBack_When_TryToRollBack_Then_NotFoundExceptionWouldBeThrown() {
        String transactionId = podamFactory.manufacturePojo(String.class);
        when(repository.findByTransactionId(transactionId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bankTransactionService.rollback(transactionId));
        verify(repository, times(1)).findByTransactionId(transactionId);
    }

    @Test
    void Given_TransactionHasBeenRollBackBefore_When_TryToRollBack_Then_ConflictExceptionWouldBeThrown() {
        Transaction transaction = podamFactory.manufacturePojo(Transaction.class);
        when(repository.findByTransactionId(transaction.getTransactionId())).thenReturn(Optional.of(transaction));
        when(repository.findByRolledBackFor(transaction.getTransactionId())).thenReturn(Optional.of(new Transaction()));
        assertThrows(ConflictException.class, () -> bankTransactionService.rollback(transaction.getTransactionId()));
        verify(repository, times(1)).findByTransactionId(transaction.getTransactionId());
        verify(repository, times(1)).findByRolledBackFor(transaction.getTransactionId());
    }

}
