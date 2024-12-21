package com.example.gamestore.service.impl;

import com.example.gamestore.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.stream.Collectors;

public class AppUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(it -> new org.springframework.security.core.userdetails.User(
                        it.getEmail(),
                        it.getPassword(),
                        it.getRoles().stream()
                                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getAuthority()))
                                .collect(Collectors.toList())
                )).orElseThrow(() -> new UsernameNotFoundException("User with this email: " + email + " was not found!"));
    }
}
