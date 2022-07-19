package com.misroservices.authentication.helper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorHttpResponse {
    private String uri;
    private int status;
    private String errorCode;
    private String message;
}
