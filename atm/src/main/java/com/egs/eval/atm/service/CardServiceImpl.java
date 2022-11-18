package com.egs.eval.atm.service;

import com.egs.eval.atm.dal.entity.Card;
import com.egs.eval.atm.dal.entity.User;
import com.egs.eval.atm.dal.repository.CardRepository;
import com.egs.eval.atm.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
    private final CardRepository repository;

    @Override
    public User getUserByCardNumber(String cardNumber) {
        return repository.findByCardNumber(cardNumber)
                .map(Card::getUser)
                .orElseThrow(() -> new NotFoundException("Invalid card number."));
    }
}
