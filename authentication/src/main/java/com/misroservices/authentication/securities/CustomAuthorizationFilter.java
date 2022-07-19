package com.misroservices.authentication.securities;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class CustomAuthorizationFilter extends BasicAuthenticationFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final List<String> pathPass = List.of("/api/auth/register", "/api/auth/login");

    public CustomAuthorizationFilter(AuthenticationManager authManager, JwtTokenProvider jwtTokenProvider) {
        super(authManager);
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if(!pathPass.contains(request.getServletPath()) &&
                request.getHeader(AUTHORIZATION) != null &&
                request.getHeader(AUTHORIZATION).startsWith("Bearer ")){
            try {
                String token = request.getHeader(AUTHORIZATION).substring("Bearer ".length());
                Authentication authentication = jwtTokenProvider.getAuthentication(token, request);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }catch (ExpiredJwtException e){
                response.setStatus(401);
            }catch (MalformedJwtException e){
                response.setStatus(4011);
            }
        }
        filterChain.doFilter(request, response);
    }
}
