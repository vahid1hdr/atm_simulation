package com.egs.eval.atm.dal.repository;

import com.egs.eval.atm.dal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

}
