package com.example.sns_project.init;

import com.example.sns_project.model.Role;
import com.example.sns_project.model.User;
import com.example.sns_project.repository.RoleRepository;
import com.example.sns_project.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public DatabaseInitializer(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // 'ROLE_USER' 역할이 존재하지 않는 경우에만 추가
        roleRepository.findByName("ROLE_USER").orElseGet(() -> {
            Role userRole = new Role();
            userRole.setName("ROLE_USER");
            return roleRepository.save(userRole);
        });

        // 기본 사용자 추가
        userRepository.findByUsername("admin").orElseGet(() -> {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword("password");  // 비밀번호는 실제로 암호화해야 함
            // 기본 역할을 사용자에게 할당할 수 있습니다.
            admin.getRoles().add(roleRepository.findByName("ROLE_USER").get()); // 역할 추가
            return userRepository.save(admin);
        });
    }

}
