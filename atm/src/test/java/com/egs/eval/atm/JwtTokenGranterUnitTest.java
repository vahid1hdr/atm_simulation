package com.egs.eval.atm;

import com.egs.eval.atm.service.JwtTokenGranter;
import com.egs.eval.atm.service.model.JwtConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class JwtTokenGranterUnitTest {
    private JwtTokenGranter tokenGranter;

    @BeforeEach
    void setup() {
        JwtConfig jwtConfig = new JwtConfig();
        jwtConfig.setSecret("$EcR@Key4JWT$EcR@Key4JWT$EcR@Key4JWT$EcR@Key4JWT");
        jwtConfig.setValiditySeconds(600);
        tokenGranter = new JwtTokenGranter(jwtConfig);
    }
    @Test
    void when_callGenerateToken_then_tokenShouldBeGenerated(){
        String token = tokenGranter.generateToken("userId");
        assertNotNull(token);
    }

    @Test
    void when_callGetUserIdFromToken_then_theUserIdShouldBeReturned(){
        String userId = "userId";
        String token = tokenGranter.generateToken("userId");
        String userIdFromToken = tokenGranter.getUserIdFromToken(token);
        assertEquals(userId, userIdFromToken);
    }

    @Test
    void when_callGetExpirationDateFromToken_then_10MinLaterShouldBeReturned(){
        String token = tokenGranter.generateToken("userId");
        LocalDateTime expirationDate = tokenGranter.getExpirationDateFromToken(token);
        assertEquals(expirationDate.truncatedTo(ChronoUnit.SECONDS), LocalDateTime.now().plusMinutes(10).truncatedTo(ChronoUnit.SECONDS));
    }
}
