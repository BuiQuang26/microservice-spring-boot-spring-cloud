package com.misroservices.authentication.securities;

import com.misroservices.authentication.entities.User;
import com.misroservices.authentication.repositories.UserRepository;
import org.springdoc.ui.SpringDocUIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElse(null);
        if(user == null) throw new UsernameNotFoundException(username);
        Collection<SimpleGrantedAuthority> authorityCollections = new ArrayList<>();
        user.getRoles().forEach(role -> {
            authorityCollections.add(new SimpleGrantedAuthority("ROLE_" + role));
        });
        return new org.springframework.security.core.userdetails.User(username, user.getPassword(), authorityCollections);
    }
}
