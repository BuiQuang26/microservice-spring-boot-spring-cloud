package com.misroservices.authentication.helper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HttpResponse {

    private Boolean success;
    private int status;
    private String message;
    private Object data;

}
