package com.microservice.userService.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class index {

    @Value("${server.port}")
    private String port;

    @GetMapping(value = "")
    public ResponseEntity<?> getInfo(){
        return new ResponseEntity<>("This is test service port: " + port, HttpStatus.OK);
    }

    @GetMapping(value = "/hello")
    public ResponseEntity<?> hello(){
        return new ResponseEntity<>("hello from Test service, port: " + port, HttpStatus.OK);
    }

}
