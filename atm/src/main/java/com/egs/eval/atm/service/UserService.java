package com.egs.eval.atm.service;

import com.egs.eval.atm.dal.entity.User;
import com.egs.eval.atm.service.model.UserQueryModel;

import java.util.Optional;

public interface UserService {

    Optional<User> getUserByCardNumber(UserQueryModel queryModel);

    int addTodayFailedLoginAttempts(String cardNo);

    void resetTodayFailedLoginAttempts(String cardNo);

    void validateTodayFailedLoginAttempts(User user, String cardNumber);
}
