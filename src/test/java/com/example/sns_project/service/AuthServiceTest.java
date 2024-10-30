package com.example.sns_project.service;

import com.example.sns_project.dto.LoginRequest;
import com.example.sns_project.dto.UserDTO;
import com.example.sns_project.dto.UserRegistrationDTO;
import com.example.sns_project.model.Role;
import com.example.sns_project.model.User;
import com.example.sns_project.repository.RoleRepository;
import com.example.sns_project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthServiceTest {

    @Mock
    private UserRepository userRepository; // 사용자 리포지토리 모킹

    @Mock
    private RoleRepository roleRepository; // 역할 리포지토리 모킹

    @InjectMocks
    private AuthService authService; // 주입된 모킹을 사용하는 인증 서비스

    private BCryptPasswordEncoder passwordEncoder; // 비밀번호 인코더

    @BeforeEach
    public void setUp() {
        // Mockito 초기화
        MockitoAnnotations.openMocks(this);
        // BCryptPasswordEncoder 초기화
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    @DisplayName("로그인 성공 시 '로그인 성공' 반환")
    public void testLogin_Success() {
        // Arrange
        String username = "testUser"; // 테스트용 사용자 이름
        String password = "password"; // 테스트용 비밀번호
        String encodedPassword = passwordEncoder.encode(password); // 인코딩된 비밀번호

        // 사용자 객체 생성 및 설정
        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);

        // 로그인 요청 DTO 생성
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        // 사용자 리포지토리의 반환값 설정
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(user));

        // Act: 로그인 메서드 호출
        String result = authService.login(loginRequest);

        // Assert: 결과 검증
        assertThat(result).isEqualTo("로그인 성공");
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 로그인 시 '로그인 실패' 반환")
    public void testLogin_Failure_UserNotFound() {
        // 로그인 요청 DTO 생성
        String username = "nonExistentUser"; // 존재하지 않는 사용자 이름
        String password = "password";

        // 로그인 요청 DTO 설정
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        // 사용자 리포지토리의 반환값 설정
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act: 로그인 메서드 호출
        String result = authService.login(loginRequest);

        // Assert: 결과 검증
        assertThat(result).isEqualTo("로그인 실패");
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 시 '로그인 실패' 반환")
    public void testLogin_Failure_IncorrectPassword() {
        // 로그인 요청 DTO 생성
        String username = "testUser";
        String password = "password";
        String incorrectPassword = "wrongPassword"; // 잘못된 비밀번호
        String encodedPassword = passwordEncoder.encode(password); // 인코딩된 비밀번호

        // 사용자 객체 생성 및 설정
        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);

        // 로그인 요청 DTO 설정
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(incorrectPassword);

        // 사용자 리포지토리의 반환값 설정
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(user));

        // Act: 로그인 메서드 호출
        String result = authService.login(loginRequest);

        // Assert: 결과 검증
        assertThat(result).isEqualTo("로그인 실패");
    }

    @Test
    @DisplayName("회원가입 성공 시 사용자 정보 반환")
    public void testRegister_Success() {
        // Arrange
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO(); // 회원가입 DTO 생성
        registrationDTO.setUsername("testUser");
        registrationDTO.setEmail("test@example.com");
        registrationDTO.setPassword("password");

        Role userRole = new Role(); // 역할 객체 생성
        userRole.setName("ROLE_USER");

        // 역할 리포지토리의 반환값 설정
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));

        // Act: 회원가입 메서드 호출
        UserDTO result = authService.register(registrationDTO);

        // Assert: 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testUser");
        assertThat(result.getEmail()).isEqualTo("test@example.com");

        // 사용자 리포지토리의 save 메서드 호출 검증
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("기본 역할이 존재하지 않을 경우 RuntimeException 발생")
    public void testRegister_RoleNotFound() {
        // Arrange
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO(); // 회원가입 DTO 생성
        registrationDTO.setUsername("testUser");
        registrationDTO.setEmail("test@example.com");
        registrationDTO.setPassword("password");

        // 역할 리포지토리의 반환값 설정
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.empty());

        // Act & Assert: 역할이 없을 경우 예외 발생 검증
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.register(registrationDTO);
        });
        assertThat(exception.getMessage()).isEqualTo("기본 역할이 존재하지 않습니다.");
    }
}
