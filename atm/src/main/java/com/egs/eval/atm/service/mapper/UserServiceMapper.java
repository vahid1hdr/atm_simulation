package com.egs.eval.atm.service.mapper;

import com.egs.eval.atm.dal.entity.User;
import com.egs.eval.atm.service.model.UserQueryModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

import java.util.Set;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, imports = Set.class)
public interface UserServiceMapper {

    @Mapping(target = "cardSet", expression = "java((queryModel.getCard() == null) ? null : Set.of(queryModel.getCard()))")
    User getUserFromUserQueryModel(UserQueryModel queryModel);
}
