package com.egs.eval.client.external.model;

import lombok.Data;

@Data
public class AuthRequest {
    private String cardNumber;
    private String authType;
    private String value;
}
