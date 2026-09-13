package com.example.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.demo.Service.AuthenticationService;
import com.example.demo.models.User;
import com.example.demo.repository.UserRepository;

// Public signup only creates Customers, so on an empty database nobody could
// ever become Admin. This creates the first Admin at startup from environment
// variables, and does nothing once an Admin exists.
@Component
public class AdminBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final String email;
    private final String password;

    public AdminBootstrap(UserRepository repository,
                          PasswordEncoder passwordEncoder,
                          @Value("${app.bootstrap-admin.email:}") String email,
                          @Value("${app.bootstrap-admin.password:}") String password) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.email = email;
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (repository.existsByRole(AuthenticationService.ROLE_ADMIN)) {
            return;
        }
        if (email.isBlank() || password.isBlank()) {
            log.warn("No Admin account exists and ADMIN_EMAIL / ADMIN_PASSWORD are not set: nobody can manage users.");
            return;
        }
        if (repository.existsByEmail(email)) {
            log.warn("Bootstrap Admin not created: {} already exists as a non-Admin account.", email);
            return;
        }
        repository.save(User.builder()
                .fullName("Administrator")
                .email(email)
                .role(AuthenticationService.ROLE_ADMIN)
                .password(passwordEncoder.encode(password))
                .build());
        log.info("Bootstrap Admin account created: {}", email);
    }
}
