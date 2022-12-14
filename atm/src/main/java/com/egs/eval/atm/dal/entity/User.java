package com.egs.eval.atm.dal.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "user_tbl")
public class User extends BaseEntity {

    @Column(name = "pin")
    private String pin;

    @Column(name = "fingerprint")
    private String fingerprint;

    @Column(name = "today_failed_login_attempts")
    private Integer todayFailedLoginAttempts;

    @Column(name = "day_of_fail_login")
    private Long dayOfFailLogin;

}
