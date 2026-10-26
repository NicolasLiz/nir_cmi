package br.com.nircmi.config;

import br.com.nircmi.util.Crypto;
import br.com.nircmi.model.User;
import br.com.nircmi.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class AdminInit {

    @Bean
    public CommandLineRunner createDefaultAdmin(UserRepository userRepository) {
        return args -> {
            // Check if any admin user already exists
            List<User> uadmin = userRepository.getUsersByPermission(User.uTypeAdmin);
            boolean adminListEmpty = uadmin.isEmpty();

            if (adminListEmpty) {
                User admin = new User();
                admin.userID = java.util.UUID.randomUUID();
                admin.userName = "admin";
                admin.userEmail = "admin@cmi.com";
                admin.userPassword = Crypto.hashSha256("123456");  
                admin.userPermissions = User.uTypeAdmin; // ~0L
                admin.userActive = 1;                           // Required schema field (1 = Active)
                admin.userRegisterDate = LocalDateTime.now();   // Required schema field

                userRepository.createUser(admin);
                System.out.println("created admin account");
            }
        };
    }
}
