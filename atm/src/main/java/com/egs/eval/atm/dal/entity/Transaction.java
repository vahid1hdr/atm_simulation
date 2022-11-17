package com.egs.eval.atm.dal.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "transaction_tbl", indexes = {
        @Index(name = "idx_transactionId", columnList = "transaction_id", unique = true),
        @Index(name = "idx_rolledBackFor", columnList = "rolled_back_for", unique = true)
})
public class Transaction extends BaseEntity {

    @Column(name = "user_id")
    private String userId;

    @Column(name = "value")
    private Long value;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "rolled_back_for")
    private String rolledBackFor;
}
