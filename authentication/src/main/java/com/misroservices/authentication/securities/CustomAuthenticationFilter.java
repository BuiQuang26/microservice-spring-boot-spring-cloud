package com.misroservices.authentication.securities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.misroservices.authentication.entities.User;
import com.misroservices.authentication.helper.ErrorHttpResponse;
import com.misroservices.authentication.helper.HttpResponse;
import com.misroservices.authentication.repositories.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final static Logger logger = LogManager.getLogger(CustomAuthenticationFilter.class);
    private final static ObjectMapper objectMapper = new ObjectMapper();
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public CustomAuthenticationFilter(UserRepository userRepository, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        InputStream inputStream = null;
        User user = null;
        try {
            inputStream = request.getInputStream();
            user = objectMapper.readValue(inputStream, User.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(user.getUsername() == null || user.getPassword() == null){
                response.setStatus(400);
            try {
                objectMapper.writeValue(response.getOutputStream(), "username or password invalid");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        logger.info("login success");
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) authResult.getPrincipal();
        User user1 = userRepository.findByUsername(user.getUsername()).orElseThrow();
        response.setStatus(200);
        response.setContentType(APPLICATION_JSON_VALUE);
        String jwt_token = jwtTokenProvider.generateJwtToken(user1);
        Map<String, String> map = Map.of("token", jwt_token);
        objectMapper.writeValue(response.getOutputStream(),new HttpResponse(true, 200, "Login success", map));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        logger.info("login failed");
        response.setStatus(400);
        response.setContentType(APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(),new ErrorHttpResponse(request.getRequestURI(), 400, "LOGIN_FAILED", "Login failed"));
    }
}
