package com.egs.eval.atm.dal.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "session_tbl")
public class Session extends BaseEntity {

    @JoinColumn(name = "user_Id")
    @OneToOne
    private User user;

    @Column(name = "token")
    private String token;
}