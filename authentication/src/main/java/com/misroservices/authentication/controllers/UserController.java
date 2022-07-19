package com.misroservices.authentication.controllers;

import com.misroservices.authentication.entities.User;
import com.misroservices.authentication.services.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;

@RestController
@SecurityRequirement(name = "BearerAuth")
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public void login(@RequestBody User user){}

    @PostMapping("/register")
    public void register(@RequestBody User user){
        userService.register(user);
    }

    @PostMapping("/user/{user_id}/add-role/{roleID}")
    public void add_role(@PathVariable String roleID, @PathVariable Long user_id){
        userService.addRole(roleID, user_id);
    }

    @DeleteMapping("/user/{user_id}/remove-role/{roleID} ")
    public void remove_role(@PathVariable String roleID, @PathVariable Long user_id){
        userService.removeRole(roleID, user_id);
    }

    @GetMapping("/user")
    public User getInfo(ServletRequest request){
        Long user_id = (Long) request.getAttribute("user_id");
        return userService.getInfo(user_id);
    }

}
