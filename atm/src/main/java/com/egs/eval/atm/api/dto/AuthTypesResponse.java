package com.egs.eval.atm.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AuthTypesResponse {
    private List<String> strategies;
}
