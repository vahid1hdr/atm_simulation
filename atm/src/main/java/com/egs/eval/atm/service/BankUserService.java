package com.egs.eval.atm.service;

import com.egs.eval.atm.dal.entity.Card;
import com.egs.eval.atm.dal.entity.User;
import com.egs.eval.atm.dal.repository.CardRepository;
import com.egs.eval.atm.dal.repository.UserRepository;
import com.egs.eval.atm.service.exception.NotAuthenticatedException;
import com.egs.eval.atm.service.exception.NumberOfAttemptsExceededException;
import com.egs.eval.atm.service.model.UserQueryModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BankUserService implements UserService {

    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final PasswordEncoder passwordEncoder;
    //    @Value("${max.allowed.failed.attempts}")
    private int maxAllowedFailedAttempts=3;

    @Override
    public Optional<User> getUserByCardNumber(UserQueryModel queryModel) {
        Optional<User> userOptional = findUser(queryModel);
        userOptional.ifPresent(user -> validateTodayFailedLoginAttempts(user,queryModel.getCard()));
        userOptional = userOptional.filter(user -> credentialMatches(queryModel, user));
        userOptional.ifPresent(this::resetUserTodayAttempts);
        return userOptional;

    }

    private Optional<User> findUser(UserQueryModel queryModel) {
        Optional<User> userOptional = cardRepository.findByCardNumber(queryModel.getCard()).map(Card::getUser);
        return userOptional;
    }

    @Override
    public int addTodayFailedLoginAttempts(String cardNo) {
        return cardRepository.findByCardNumber(cardNo)
                .map(card -> increaseUserTodayAttempts(card.getUser()))
                .orElseThrow(() -> new NotAuthenticatedException("cardNumber is not valid. cardNo: " + cardNo));
    }

    @Override
    public void resetTodayFailedLoginAttempts(String cardNo) {
         cardRepository.findByCardNumber(cardNo)
                .ifPresent(card -> resetUserTodayAttempts(card.getUser()));
    }

    @Override
    public void validateTodayFailedLoginAttempts(User user, String cardNumber) {
        long dayNumber = getDayNumber();
        if (user.getTodayFailedLoginAttempts() >0) {
            if (user.getDayOfFailLogin()!=null && dayNumber == user.getDayOfFailLogin()) {
                if (user.getTodayFailedLoginAttempts() > maxAllowedFailedAttempts) {
                    throw new NumberOfAttemptsExceededException("You are locked.");
                }
            }
            else{
                resetUserTodayAttempts(user);
            }
        }
    }

    private Integer increaseUserTodayAttempts(User user) {
        long dayNumber = getDayNumber();

        user.setTodayFailedLoginAttempts(increaseAttempts(user.getTodayFailedLoginAttempts()));
        user.setDayOfFailLogin(dayNumber);
        userRepository.save(user);
        return user.getTodayFailedLoginAttempts();
    }

    private long getDayNumber() {
        return new Date().getTime() / (24L * 3600000);
    }

    private void resetUserTodayAttempts(User user) {
        if (user.getTodayFailedLoginAttempts()==null || user.getTodayFailedLoginAttempts()>0) {
            user.setTodayFailedLoginAttempts(0);
            user.setDayOfFailLogin(getDayNumber());
            userRepository.save(user);
        }
    }

    private Integer increaseAttempts(Integer todayFailedLoginAttempts) {
        return Objects.isNull(todayFailedLoginAttempts) ? 1 : todayFailedLoginAttempts + 1;
    }

    private boolean credentialMatches(UserQueryModel queryModel, User user) {
        return (Objects.nonNull(queryModel.getPin()) && passwordEncoder.matches(queryModel.getPin(), user.getPin())) ||
                (Objects.nonNull(queryModel.getFingerprint()) && passwordEncoder.matches(queryModel.getFingerprint(), user.getFingerprint()));
    }
}
