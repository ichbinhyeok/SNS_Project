package com.example.sns_project.security;
import com.example.sns_project.model.User;
import com.example.sns_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
@Service
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // DTO를 사용하여 필요한 정보만 조회
        return userRepository.findUserDetailsDTO(username)
                .map(dto -> new org.springframework.security.core.userdetails.User(
                        dto.getUsername(),
                        dto.getPassword(),
                        dto.getAuthorities()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}