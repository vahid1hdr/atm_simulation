package com.egs.eval.client.service;

import com.egs.eval.client.external.model.AuthRequest;
import com.egs.eval.client.external.model.AuthTypesResponse;
import com.egs.eval.client.external.model.TokenResponse;

public interface AuthProxy {

    AuthTypesResponse getAuthTypes();
    TokenResponse getFreshToken(AuthRequest authRequest);
}
