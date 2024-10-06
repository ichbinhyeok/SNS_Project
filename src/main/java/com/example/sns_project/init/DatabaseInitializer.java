package com.example.sns_project.init;

// 데이터베이스 초기화 작업을 수행하는 클래스
import com.example.sns_project.model.Role;
import com.example.sns_project.model.User;
import com.example.sns_project.repository.RoleRepository;
import com.example.sns_project.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final UserRepository userRepository;  // UserRepository 의존성 주입
    private final RoleRepository roleRepository;  // RoleRepository 의존성 주입

    public DatabaseInitializer(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // 초기 데이터 (예: 기본 사용자 및 역할) 삽입 로직
        Role userRole = new Role();
        userRole.setName("USER");
        roleRepository.save(userRole);

        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@example.com");
        admin.setPassword("password");  // 비밀번호는 실제로 암호화해야 함
        userRepository.save(admin);

        // 앞으로: 추가적인 초기 데이터 삽입 로직 추가
    }
}
