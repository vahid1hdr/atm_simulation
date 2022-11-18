package com.egs.eval.atm.service;

import com.egs.eval.atm.dal.entity.User;
import com.egs.eval.atm.service.model.UserQueryModel;

public interface UserService {

    User getUserByQueryModel(UserQueryModel queryModel);
}
