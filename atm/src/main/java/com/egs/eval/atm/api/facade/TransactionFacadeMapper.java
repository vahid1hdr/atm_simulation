package com.egs.eval.atm.api.facade;


import com.egs.eval.atm.api.dto.TransactionResponse;
import com.egs.eval.atm.service.model.TransactionResult;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface TransactionFacadeMapper {

    TransactionResponse getTransactionResponseFromResultAndValue(TransactionResult transactionResult, Integer value);

    TransactionResponse getTransactionResponseFromResult(TransactionResult transactionResult);
}
