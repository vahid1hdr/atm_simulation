package com.egs.eval.atm.service;

import com.egs.eval.atm.dal.entity.User;
import com.egs.eval.atm.dal.repository.UserRepository;
import com.egs.eval.atm.service.exception.NotAuthenticatedException;
import com.egs.eval.atm.service.exception.NumberOfAttemptsExceededException;
import com.egs.eval.atm.service.model.UserQueryModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BankUserService implements UserService {

    private final UserRepository userRepository;
    private final CardService cardService;
    private final PasswordEncoder passwordEncoder;
    private static final int MAX_ALLOWED_FAILED_ATTEMPTS = 3;

    @Override
    public User getUserByCardNumber(UserQueryModel queryModel) {
        User user = findUser(queryModel.getCard());
        validateTodayFailedLoginAttempts(user);
        credentialMatches(queryModel, user);
        return user;
    }

    private User findUser(String cardNumber) {
        return cardService.getUserByCardNumber(cardNumber);
    }

    @Override
    public void validateTodayFailedLoginAttempts(User user) {
        if (Objects.nonNull(user.getTodayFailedLoginAttempts())) {
            long dayNumber = getDayNumber();
            if (dayNumber == user.getDayOfFailLogin()) {
                if (user.getTodayFailedLoginAttempts() >= MAX_ALLOWED_FAILED_ATTEMPTS) {
                    throw new NumberOfAttemptsExceededException("You are locked.");
                }
            } else {
                resetUserTodayAttempts(user);
            }
        }
    }

    private void increaseUserTodayAttempts(User user) {
        long dayNumber = getDayNumber();

        user.setTodayFailedLoginAttempts(increaseAttempts(user.getTodayFailedLoginAttempts()));
        user.setDayOfFailLogin(dayNumber);
        userRepository.save(user);
    }

    private long getDayNumber() {
        return new Date().getTime() / (24L * 3600000);
    }

    private void resetUserTodayAttempts(User user) {
        if (Objects.nonNull(user.getTodayFailedLoginAttempts())) {
            user.setTodayFailedLoginAttempts(0);
            user.setDayOfFailLogin(getDayNumber());
            userRepository.save(user);
        }
    }

    private Integer increaseAttempts(Integer todayFailedLoginAttempts) {
        return Objects.isNull(todayFailedLoginAttempts) ? 1 : todayFailedLoginAttempts + 1;
    }

    private void credentialMatches(UserQueryModel queryModel, User user) {
        boolean matched = (Objects.nonNull(queryModel.getPin()) && passwordEncoder.matches(queryModel.getPin(), user.getPin())) ||
                (Objects.nonNull(queryModel.getFingerprint()) && passwordEncoder.matches(queryModel.getFingerprint(), user.getFingerprint()));
        if (!matched) {
            increaseUserTodayAttempts(user);
            throw new NotAuthenticatedException("Invalid Authentication.");
        }
        resetUserTodayAttempts(user);
    }
}
