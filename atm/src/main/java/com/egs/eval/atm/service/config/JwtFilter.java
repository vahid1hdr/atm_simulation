package com.egs.eval.atm.service.config;


import com.egs.eval.atm.api.dto.ErrorResponse;
import com.egs.eval.atm.service.TokenGranter;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Order
public class JwtFilter extends OncePerRequestFilter {

    private TokenGranter tokenGranter;

    public JwtFilter(TokenGranter tokenGranter) {
        this.tokenGranter = tokenGranter;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = httpServletRequest.getHeader("Authorization");
        getRejectionMessage(authHeader).ifPresentOrElse(
                message -> returnProperUnAuthorizedResponse(httpServletResponse, message),
                () -> continueWithRequest(httpServletRequest, httpServletResponse, filterChain)
        );


    }

    private void continueWithRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) {
        try {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } catch (IOException e) {
            log.error("doFilter error", e);
        } catch (ServletException e) {
            log.error("doFilter servletException", e);
        }
    }

    private void returnProperUnAuthorizedResponse(HttpServletResponse httpServletResponse, String message) {
        try {
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            httpServletResponse.setContentType("application/json");
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.getWriter().print(
                    new ObjectMapper().writer().writeValueAsString(ErrorResponse.builder().message(message).build())
            );
            httpServletResponse.getWriter().flush();
        } catch (IOException e) {
            log.error("writing error", e);
        }
    }

    private Optional<String> getRejectionMessage(String authHeader) {
        if(Objects.isNull(authHeader) || !authHeader.contains("Bearer "))
            return Optional.of("please provide token");
        authHeader = authHeader.replaceAll("Bearer ", "");
        return getProperMessageBasedOnJwtException(authHeader);


    }

    private Optional<String> getProperMessageBasedOnJwtException(String token) {
        try {
            tokenGranter.getUserIdFromToken(token);
            return Optional.empty();
        } catch (ExpiredJwtException exception) {
            log.warn("Request to parse expired JWT : {} failed : {}", token, exception.getMessage());
            return Optional.of("Expired jwt");
        } catch (UnsupportedJwtException exception) {
            log.warn("Request to parse unsupported JWT : {} failed : {}", token, exception.getMessage());
            return Optional.of("Unsupported jwt");
        } catch (MalformedJwtException exception) {
            log.warn("Request to parse invalid JWT : {} failed : {}", token, exception.getMessage());
            return Optional.of("malformed jwt");
        } catch (Exception exception){
            log.warn("Request to parse invalid JWT : {} failed : {}", token, exception.getMessage());
            return Optional.of("invalid jwt");
        }
    }
}
