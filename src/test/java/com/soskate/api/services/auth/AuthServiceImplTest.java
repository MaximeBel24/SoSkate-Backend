package com.soskate.api.services.auth;

import com.soskate.api.dto.auth.login.LoginRequest;
import com.soskate.api.dto.auth.login.LoginResponse;
import com.soskate.api.entities.CustomerEntity;
import com.soskate.api.entities.InstructorEntity;
import com.soskate.api.enums.InstructorStatus;
import com.soskate.api.exceptions.auth.BadCredentialsException;
import com.soskate.api.repositories.CustomerRepository;
import com.soskate.api.repositories.InstructorRepository;
import com.soskate.api.security.CustomUserDetailsService;
import com.soskate.api.security.JwtService;
import com.soskate.api.services.common.EmailValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private InstructorRepository instructorRepository;

    @Mock
    private EmailValidationService emailValidationService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    private CustomerEntity customer;

    private InstructorEntity instructor;

    @BeforeEach
    void setUp() {
        customer = CustomerEntity.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@test.com")
                .password("encodedPassword")
                .phone("0123456789")
                .build();
        customer.setId(1L);

        instructor = InstructorEntity.builder()
                .firstName("Tony")
                .lastName("Hawk")
                .email("tony.hawk@skate.com")
                .password("encodedPassword")
                .status(InstructorStatus.ACTIVE)
                .build();
        instructor.setId(2L);
    }

    @Test
    void login_withValidCustomerCredentials_returnsCustomerResponse() {

        UserDetails userDetails = User.builder()
                .username("john.doe@test.com")
                .password("encodedPassword")
                .authorities(List.of())
                .build();

        when(customerRepository.findByEmail(customer.getEmail()))
                .thenReturn(Optional.of(customer));
        when(passwordEncoder.matches("password", "encodedPassword"))
                .thenReturn(true);
        when(customUserDetailsService.loadUserByUsername(customer.getEmail()))
                .thenReturn(userDetails);
        when(jwtService.generateToken(userDetails))
                .thenReturn("jwt-token");

        LoginRequest request = new LoginRequest("john.doe@test.com", "password");

        LoginResponse response = authService.login(request);

        assertThat(response.role()).isEqualTo(LoginResponse.UserRole.CUSTOMER);
        assertThat(response.email()).isEqualTo("john.doe@test.com");
        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.customerId()).isEqualTo(1L);
        assertThat(response.instructorId()).isNull();
    }

    @Test
    void login_withWrongCustomerPassword_throwsBadCredentials() {

        when(customerRepository.findByEmail(customer.getEmail()))
                .thenReturn(Optional.of(customer));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword"))
                .thenReturn(false);

        LoginRequest request = new LoginRequest("john.doe@test.com", "wrongPassword");

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void login_withValidActiveInstructor_returnsInstructorResponse() {
        UserDetails userDetails = User.builder()
                .username("tony.hawk@skate.com")
                .password("encodedPassword")
                .authorities(List.of())
                .build();

        when(customerRepository.findByEmail(instructor.getEmail()))
                .thenReturn(Optional.empty());
        when(instructorRepository.findByEmailAndDeletedFalse(instructor.getEmail()))
                .thenReturn(Optional.of(instructor));
        when(passwordEncoder.matches("password", "encodedPassword"))
                .thenReturn(true);
        when(customUserDetailsService.loadUserByUsername(instructor.getEmail()))
                .thenReturn(userDetails);
        when(jwtService.generateToken(userDetails))
                .thenReturn("jwt-token");

        LoginRequest request = new LoginRequest("tony.hawk@skate.com", "password");

        LoginResponse response = authService.login(request);

        assertThat(response.role()).isEqualTo(LoginResponse.UserRole.INSTRUCTOR);
        assertThat(response.email()).isEqualTo("tony.hawk@skate.com");
        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.customerId()).isNull();
        assertThat(response.instructorId()).isEqualTo(2L);
    }

    @Test
    void login_withInactiveInstructor_throwsBadCredentials() {
        InstructorEntity suspendedInstructor = InstructorEntity.builder()
                .firstName("Rodney")
                .lastName("Mullen")
                .email("rodney.mullen@skate.com")
                .password("wrongPassword")
                .status(InstructorStatus.SUSPENDED)
                .build();
        when(customerRepository.findByEmail(suspendedInstructor.getEmail()))
                .thenReturn(Optional.empty());
        when(instructorRepository.findByEmailAndDeletedFalse(suspendedInstructor.getEmail()))
                .thenReturn(Optional.of(suspendedInstructor));

        LoginRequest request = new LoginRequest("rodney.mullen@skate.com", "wrongPassword");

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void login_withUnknownEmail_throwsBadCredentials() {
        when(customerRepository.findByEmail("unknown@test.com"))
                .thenReturn(Optional.empty());
        when(instructorRepository.findByEmailAndDeletedFalse("unknown@test.com"))
                .thenReturn(Optional.empty());

        LoginRequest request = new LoginRequest("unknown@test.com", "wrongPassword");

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }

}
