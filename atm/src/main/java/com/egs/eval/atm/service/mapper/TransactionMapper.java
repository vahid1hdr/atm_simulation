package com.egs.eval.atm.service.mapper;

import com.egs.eval.atm.dal.entity.Transaction;
import com.egs.eval.atm.service.model.TransactionResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface TransactionMapper {

    Transaction getTransaction(String userId, Integer value, String transactionId);

    TransactionResult getTransactionResult(Transaction transaction, long balance);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "rolledBackFor", source = "transaction.transactionId")
    @Mapping(target = "value", expression = "java(transaction.getValue() * -1)")
    @Mapping(target = "userId", source = "transaction.userId")
    @Mapping(target = "transactionId", source = "transactionId")
    Transaction getRollbackTransactionFromTransaction(Transaction transaction, String transactionId);
}
