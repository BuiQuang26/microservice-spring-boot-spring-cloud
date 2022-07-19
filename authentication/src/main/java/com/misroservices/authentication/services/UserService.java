package com.misroservices.authentication.services;

import com.misroservices.authentication.entities.Role;
import com.misroservices.authentication.entities.User;
import com.misroservices.authentication.repositories.RoleRepository;
import com.misroservices.authentication.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void register(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public void addRole(String roleID, Long user_id){
        User user = userRepository.findById(user_id).orElseThrow();
        Role role = roleRepository.findById(roleID).orElseThrow();
        user.addRole(role);
        userRepository.save(user);
    }

    public void removeRole(String roleID, Long user_id){
        User user = userRepository.findById(user_id).orElseThrow();
        Role role = roleRepository.findById(roleID).orElseThrow();
        user.removeRole(role);
        userRepository.save(user);
    }

    public User getInfo(Long user_id){
        return userRepository.findById(user_id).orElseThrow();
    }
}
