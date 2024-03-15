package com.example.onlineshopfigma.security;


import com.example.onlineshopfigma.domain.Authority;
import com.example.onlineshopfigma.repository.UserRepository;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.User;

import java.util.List;
import java.util.stream.Collectors;

@Component("userDetailService")
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String lowerCaseUsername = username.toLowerCase();
        try {
            return userRepository
                    .findByUsername(lowerCaseUsername)
                    .map(user -> {
                        try {
                            return createSpringSecurityUser(lowerCaseUsername, user);
                        } catch (UserNotActivateException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .orElseThrow(() -> new UserNotActivateException("User " + username + " was not found in the database"));
        } catch (UserNotActivateException e) {
            throw new RuntimeException(e);
        }

    }

    private org.springframework.security.core.userdetails.User createSpringSecurityUser(String username, com.example.onlineshopfigma.domain.User user) throws UserNotActivateException {
        if (!user.isActivated()) {
            throw new UserNotActivateException("User " + username + " was not activated");
        }
        List<GrantedAuthority> grantedAuthorities = user
                .getAuthorities()
                .stream()
                .map(Authority::getName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return new User(username, user.getPassword(), grantedAuthorities);
    }
}
