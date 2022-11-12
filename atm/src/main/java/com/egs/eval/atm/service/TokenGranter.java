package com.egs.eval.atm.service;

import java.time.LocalDateTime;

public interface TokenGranter {
    String generateToken(String value);
    LocalDateTime getExpirationDateFromToken(String token);
    String getUserIdFromToken(String token);
}
