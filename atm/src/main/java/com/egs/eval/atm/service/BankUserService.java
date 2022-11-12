package com.egs.eval.atm.service;

import com.egs.eval.atm.dal.entity.BaseEntity;
import com.egs.eval.atm.dal.entity.User;
import com.egs.eval.atm.dal.repository.UserRepository;
import com.egs.eval.atm.service.exception.NotAuthenticatedException;
import com.egs.eval.atm.service.mapper.UserServiceMapper;
import com.egs.eval.atm.service.model.UserQueryModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BankUserService implements UserService {

    private final UserRepository repository;
    private final UserServiceMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Optional<String> getUserId(UserQueryModel queryModel) {
        return repository.findByCardSet(Set.of(queryModel.getCard()))
                .filter(user -> credentialMatches(queryModel, user))
                .map(BaseEntity::getId);
    }

    @Override
    public int addTodayFailedLoginAttempts(String cardNo) {
        User userAsExample = mapper.getUserFromUserQueryModel(UserQueryModel.builder().card(cardNo).build());
        return repository.findOne(Example.of(userAsExample))
                .map(this::increaseUserTodayAttempts)
                .orElseThrow(() -> new NotAuthenticatedException("cardNumber is not valid. cardNo: " + cardNo));

    }

    private Integer increaseUserTodayAttempts(User user) {
        user.setTodayFailedLoginAttempts(increaseAttempts(user.getTodayFailedLoginAttempts()));
        repository.save(user);
        return user.getTodayFailedLoginAttempts();
    }

    private Integer increaseAttempts(Integer todayFailedLoginAttempts) {
        return Objects.isNull(todayFailedLoginAttempts) ? 0 : todayFailedLoginAttempts + 1;
    }

    private boolean credentialMatches(UserQueryModel queryModel, User user) {
        return (Objects.nonNull(queryModel.getPin()) && passwordEncoder.matches(queryModel.getPin(), user.getPin())) ||
                (Objects.nonNull(queryModel.getFingerprint()) && passwordEncoder.matches(queryModel.getFingerprint(), user.getFingerprint()));
    }
}
