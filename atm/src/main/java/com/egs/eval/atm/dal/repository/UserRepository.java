package com.egs.eval.atm.dal.repository;

import com.egs.eval.atm.dal.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.Set;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByCardSet(Set<String> cardNumber);
}
