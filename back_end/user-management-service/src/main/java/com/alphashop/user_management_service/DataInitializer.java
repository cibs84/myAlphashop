package com.alphashop.user_management_service;

import java.util.Arrays;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.alphashop.user_management_service.models.User;
import com.alphashop.user_management_service.repositories.UserRepository;

import jakarta.annotation.PostConstruct;

@Component
@Profile("!test") // All profiles except 'test'
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    ModelMapper modelMapper;
    
    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @PostConstruct
    public void initializeData() {
        System.out.println(">>>> TENTATIVO DI INIZIALIZZAZIONE DATI IN CORSO... <<<<");
        try {
            long count = userRepository.count();
            System.out.println(">>>> Record trovati nel DB: " + count);
            
         // Verifichiamo se il database è vuoto prima di inserire
            if (userRepository.count() == 0) {
                
                // Creazione UserAdmin
                User userAdmin = new User();
                userAdmin.setUserId("userAdmin");
                userAdmin.setPassword(passwordEncoder.encode("pass1234"));
                userAdmin.setActive(true);
                userAdmin.setRoles(Arrays.asList("USER", "ADMIN"));
                
                // Creazione UserRead
                User userRead = new User();
                userRead.setUserId("userRead");
                userRead.setPassword(passwordEncoder.encode("pass1234"));
                userRead.setActive(true);
                userRead.setRoles(Arrays.asList("USER"));
                
                userRepository.saveAll(Arrays.asList(userAdmin, userRead));
                
                System.out.println("🟢 DataInitializer: Database MongoDB vuoto. Utenti di test creati.");
            } else {
                System.out.println("🟡 DataInitializer: Database MongoDB già popolato. Salto l'inizializzazione.");
            }
        } catch (Exception e) {
            System.err.println("🔴 ERRORE CRITICO DB: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
