package com.egs.eval.atm.service;

import com.egs.eval.atm.service.model.UserQueryModel;

import java.util.Optional;

public interface UserService {

    Optional<String> getUserId(UserQueryModel queryModel);

    int addTodayFailedLoginAttempts(String cardNo);
}
