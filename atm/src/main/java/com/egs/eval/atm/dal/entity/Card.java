package com.egs.eval.atm.dal.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "card_tbl")
public class Card extends BaseEntity {

    @JoinColumn(name = "user_Id")
    @OneToOne
    private User user;

    @Column(name = "card_number")
    private String cardNumber;

}
