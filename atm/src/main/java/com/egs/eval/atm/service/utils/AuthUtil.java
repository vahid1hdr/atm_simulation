package com.egs.eval.atm.service.utils;

public class AuthUtil {
    public static String getBearerToken(String authorizationHeader) {
        return authorizationHeader.replaceAll("Bearer ", "");
    }
}
