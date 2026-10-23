package br.com.nircmi.repository;

import br.com.nircmi.util.Crypto;
import br.com.nircmi.model.User;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AdminInit{
    @Bean
    public CommandLineRunner createDefaultAdmin(UserRepository userRepository) {
        return args -> {
            // Check if any admin user already exists
            List<User> uadmin = userRepository.getUsersByPermission(User.uTypeAdmin);
            boolean adminExists = uadmin.isEmpty();

            if (adminExists) {
                User admin = new User();
                admin.userID = java.util.UUID.randomUUID();
                admin.userName = "admin";
                admin.userEmail = "admin@cmi.com";
                admin.userPassword = Crypto.hashSha256("123456"); 
                admin.userPermissions = User.uTypeAdmin; // ~0L

                userRepository.createUser(admin);
                System.out.println("created admin account");
            }
        };
    }

}
