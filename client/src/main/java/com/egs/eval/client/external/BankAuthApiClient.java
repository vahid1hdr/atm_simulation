package com.egs.eval.client.external;

import com.egs.eval.client.external.model.AuthRequest;
import com.egs.eval.client.external.model.AuthTypesResponse;
import com.egs.eval.client.external.model.TokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "bank-auth-client", url = "${bank.auth.base.url}", configuration = DefaultFeignErrorDecoder.class)
public interface BankAuthApiClient {

    @GetMapping(path = "${bank.auth.getTypes.url}")
    AuthTypesResponse getAuthMechanisms();

    @PostMapping
    TokenResponse getToken(@RequestBody AuthRequest authRequest);

}