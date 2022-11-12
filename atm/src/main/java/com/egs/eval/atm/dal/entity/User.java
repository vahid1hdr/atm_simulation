package com.egs.eval.atm.dal.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Document("user")
public class User extends BaseEntity {
    private String pin;
    private String fingerprint;
    private Set<String> cardSet;
    private Session activeSession;
    private Integer todayFailedLoginAttempts;

    @Data
    private static class Session {
        private String token;
        private LocalDateTime createdDate;
    }
}
