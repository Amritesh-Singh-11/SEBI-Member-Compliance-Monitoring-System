package com.sebi.compliance.config;

import com.sebi.compliance.user.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(new Role(RoleName.ROLE_ADMIN)));
        Role officerRole = roleRepository.findByName(RoleName.ROLE_COMPLIANCE_OFFICER)
                .orElseGet(() -> roleRepository.save(new Role(RoleName.ROLE_COMPLIANCE_OFFICER)));
        Role memberRole = roleRepository.findByName(RoleName.ROLE_MEMBER_USER)
                .orElseGet(() -> roleRepository.save(new Role(RoleName.ROLE_MEMBER_USER)));

        createOrUpdateUser("admin", "Admin@123", "admin@regtech-prototype.academic", "System Administrator", adminRole);
        createOrUpdateUser("officer", "Officer@123", "officer@regtech-prototype.academic", "Senior Compliance Officer", officerRole);
        createOrUpdateUser("member_user", "Member@123", "compliance@abc-securities.demo", "ABC Securities Officer", memberRole);
    }

    private void createOrUpdateUser(String username, String rawPassword, String email, String fullName, Role role) {
        User user = userRepository.findByUsername(username).orElseGet(() -> {
            User newUser = new User(username, passwordEncoder.encode(rawPassword), email, fullName);
            newUser.setRoles(Collections.singleton(role));
            return newUser;
        });

        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setEmail(email);
        user.setFullName(fullName);
        user.setStatus("ACTIVE");
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(Collections.singleton(role));
        }
        userRepository.save(user);
    }
}
