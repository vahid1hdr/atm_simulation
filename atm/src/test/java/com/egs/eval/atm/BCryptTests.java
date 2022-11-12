package com.egs.eval.atm;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BCryptTests {
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Test
    void when_encodingAPassword_then_resultsShouldBeEqualUsingMatchesMethod(){
        String password = "qwertyuiopl";
        String encodedPassword = passwordEncoder.encode(password);
        assertTrue(passwordEncoder.matches(password, encodedPassword));
    }
}
