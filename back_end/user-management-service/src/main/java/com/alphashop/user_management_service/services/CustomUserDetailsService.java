package com.alphashop.user_management_service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.alphashop.user_management_service.repositories.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        // Cerchiamo l'utente su MongoDB
        com.alphashop.user_management_service.models.User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato con ID: " + userId));

        // Convertiamo il nostro modello User in quello richiesto da Spring Security
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUserId())
                .password(user.getPassword())
                .roles(user.getRoles().toArray(new String[0]))
                .disabled(!user.isActive())
                .build();
    }
}