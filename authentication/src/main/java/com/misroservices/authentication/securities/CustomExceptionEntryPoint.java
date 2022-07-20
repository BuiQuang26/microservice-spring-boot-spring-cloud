package com.misroservices.authentication.securities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.misroservices.authentication.helper.ErrorHttpCode;
import com.misroservices.authentication.helper.ErrorHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class CustomExceptionEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        if(response.getStatus() == 401){
            objectMapper.writeValue(response.getOutputStream(),
                    new ErrorHttpResponse(request.getRequestURI(), 401, "TOKEN_EXPIRED", "Authentication token is expired"));
        }else if(response.getStatus() == 4011){
            response.setStatus(403);
            objectMapper.writeValue(response.getOutputStream(),
                    new ErrorHttpResponse(request.getRequestURI(), 403, "TOKEN_INVALID", "Authentication token is invalid"));
        }else {
            response.setStatus(403);
            objectMapper.writeValue(response.getOutputStream(),
                    new ErrorHttpResponse(request.getRequestURI(), 401, "UNAUTHORIZED", "Access denied"));
        }
    }
}
