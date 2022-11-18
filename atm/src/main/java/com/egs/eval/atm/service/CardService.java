package com.egs.eval.atm.service;

import com.egs.eval.atm.dal.entity.User;

public interface CardService {
    User getUserByCardNumber(String cardNumber);
}
