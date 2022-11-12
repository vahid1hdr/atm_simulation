package com.egs.eval.atm.api.dto;

import com.egs.eval.atm.service.model.AuthenticationMechanism;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class AuthRequest {
    @ApiModelProperty(example = "'1234567890123456'", required = true)
    @NotBlank(message = "cardNumber can not be blank")
    private String cardNumber;
    @ApiModelProperty(example = "'FINGERPRINT'", required = true)
    @NotNull(message = "authType can not be null")
    private AuthenticationMechanism authType;
    @ApiModelProperty(example = "'MyFingerPrint'", required = true)
    @NotBlank(message = "value can not be blank")
    private String value;
}
