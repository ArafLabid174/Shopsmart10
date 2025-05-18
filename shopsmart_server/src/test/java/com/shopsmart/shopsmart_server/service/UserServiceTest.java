package com.shopsmart.shopsmart_server.service;

import com.shopsmart.shopsmart_server.dto.*;
import com.shopsmart.shopsmart_server.model.User;
import com.shopsmart.shopsmart_server.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.stubbing.Answer;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private final String RAW_PASSWORD = "Password123!";
    private final String ENCODED_PASSWORD = "hashed123";
    private final String EMAIL = "hasan1095@gmail.com";
    private final String USER_ID = "68224414847a464cbd525224";
    private final LocalDateTime CREATED_AT = LocalDateTime.of(2025, 5, 13, 0, 55, 16, 67503700);

    @BeforeEach
    void setUp() {
        // No-op, fields setup by @InjectMocks
    }

    // --- Tests for registerUser ---

    @Test
    void registerUser_missingFullName() {
        SignupRequest req = new SignupRequest();
        req.setEmail(EMAIL);
        req.setPassword(RAW_PASSWORD);
        req.setConfirmPassword(RAW_PASSWORD);
        ApiResponse<User> resp = userService.registerUser(req);
        assertFalse(resp.isSuccess());
        assertEquals("Full name is required", resp.getMessage());
    }

    @Test
    void registerUser_missingEmail() {
        SignupRequest req = new SignupRequest();
        req.setFullName("Hasan");
        req.setPassword(RAW_PASSWORD);
        req.setConfirmPassword(RAW_PASSWORD);
        ApiResponse<User> resp = userService.registerUser(req);
        assertFalse(resp.isSuccess());
        assertEquals("Email is required", resp.getMessage());
    }

    @Test
    void registerUser_missingPassword() {
        SignupRequest req = new SignupRequest();
        req.setFullName("Hasan");
        req.setEmail(EMAIL);
        ApiResponse<User> resp = userService.registerUser(req);
        assertFalse(resp.isSuccess());
        assertEquals("Password is required", resp.getMessage());
    }

    @Test
    void registerUser_passwordsDontMatch() {
        SignupRequest req = new SignupRequest();
        req.setFullName("Hasan");
        req.setEmail(EMAIL);
        req.setPassword("abc");
        req.setConfirmPassword("123");
        ApiResponse<User> resp = userService.registerUser(req);
        assertFalse(resp.isSuccess());
        assertEquals("Passwords do not match", resp.getMessage());
    }

    @Test
    void registerUser_emailAlreadyExists() {
        SignupRequest req = new SignupRequest();
        req.setFullName("Hasan");
        req.setEmail(EMAIL);
        req.setPassword(RAW_PASSWORD);
        req.setConfirmPassword(RAW_PASSWORD);

        when(userRepository.existsByEmail(EMAIL)).thenReturn(true);

        ApiResponse<User> resp = userService.registerUser(req);
        assertFalse(resp.isSuccess());
        assertEquals("Email is already registered", resp.getMessage());
        verify(userRepository).existsByEmail(EMAIL);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void registerUser_success() {
        SignupRequest req = new SignupRequest();
        req.setFullName("Hasan");
        req.setEmail(EMAIL);
        req.setPassword(RAW_PASSWORD);
        req.setConfirmPassword(RAW_PASSWORD);

        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);

        User savedUser = new User("Hasan", EMAIL, ENCODED_PASSWORD);
        savedUser.setId(USER_ID);
        savedUser.setCreatedAt(CREATED_AT);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        ApiResponse<User> resp = userService.registerUser(req);

        assertTrue(resp.isSuccess());
        assertEquals("User registered successfully", resp.getMessage());
        assertNotNull(resp.getData());
        assertEquals(USER_ID, resp.getData().getId());
        assertNull(resp.getData().getPassword(), "Password should be null in the response");
        verify(passwordEncoder).encode(RAW_PASSWORD);
        verify(userRepository).save(any(User.class));
    }

    // --- Tests for signIn ---

    @Test
    void signIn_missingEmail() {
        SignInRequest req = new SignInRequest();
        req.setPassword("pw");
        ApiResponse<User> resp = userService.signIn(req);
        assertFalse(resp.isSuccess());
        assertEquals("Email is required", resp.getMessage());
    }

    @Test
    void signIn_missingPassword() {
        SignInRequest req = new SignInRequest();
        req.setEmail(EMAIL);
        ApiResponse<User> resp = userService.signIn(req);
        assertFalse(resp.isSuccess());
        assertEquals("Password is required", resp.getMessage());
    }

    @Test
    void signIn_invalidCredentials_emailNotExist() {
        SignInRequest req = new SignInRequest();
        req.setEmail(EMAIL);
        req.setPassword(RAW_PASSWORD);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        ApiResponse<User> resp = userService.signIn(req);

        assertFalse(resp.isSuccess());
        assertEquals("Invalid credentials", resp.getMessage());
    }

    @Test
    void signIn_invalidCredentials_wrongPassword() {
        SignInRequest req = new SignInRequest();
        req.setEmail(EMAIL);
        req.setPassword(RAW_PASSWORD);

        User user = new User("Hasan", EMAIL, ENCODED_PASSWORD);
        user.setId(USER_ID);
        user.setCreatedAt(CREATED_AT);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

        ApiResponse<User> resp = userService.signIn(req);
        assertFalse(resp.isSuccess());
        assertEquals("Invalid credentials", resp.getMessage());
    }

    @Test
    void signIn_success() {
        SignInRequest req = new SignInRequest();
        req.setEmail(EMAIL);
        req.setPassword(RAW_PASSWORD);

        User user = new User("Hasan", EMAIL, ENCODED_PASSWORD);
        user.setId(USER_ID);
        user.setCreatedAt(CREATED_AT);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);

        ApiResponse<User> resp = userService.signIn(req);

        assertTrue(resp.isSuccess());
        assertNotNull(resp.getData());
        assertEquals(USER_ID, resp.getData().getId());
        assertEquals("Hasan", resp.getData().getFullName());
        assertNull(resp.getData().getPassword(), "Password should not be in the response");
    }

    // --- Tests for updateUser ---

    @Test
    void updateUser_missingCurrentPassword() {
        UpdateUserRequest req = new UpdateUserRequest();
        req.setFullName("Haaaasan");
        ApiResponse<User> resp = userService.updateUser(USER_ID, req);
        assertFalse(resp.isSuccess());
        assertEquals("Current password is required", resp.getMessage());
    }

    @Test
    void updateUser_userNotFound() {
        UpdateUserRequest req = new UpdateUserRequest();
        req.setCurrentPassword("wrong");
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
        ApiResponse<User> resp = userService.updateUser(USER_ID, req);
        assertFalse(resp.isSuccess());
        assertEquals("User not found", resp.getMessage());
    }

    @Test
    void updateUser_wrongCurrentPassword() {
        UpdateUserRequest req = new UpdateUserRequest();
        req.setCurrentPassword("wrong");
        User oldUser = new User("OldName", EMAIL, ENCODED_PASSWORD);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(oldUser));
        when(passwordEncoder.matches("wrong", ENCODED_PASSWORD)).thenReturn(false);

        ApiResponse<User> resp = userService.updateUser(USER_ID, req);
        assertFalse(resp.isSuccess());
        assertEquals("Current password is incorrect", resp.getMessage());
    }

    @Test
    void updateUser_success_changeNameAndPassword() {
        UpdateUserRequest req = new UpdateUserRequest();
        req.setCurrentPassword("Password123!");
        req.setFullName("Haaaasan");
        req.setNewPassword("newpass");

        User user = new User("Hasan", EMAIL, ENCODED_PASSWORD);
        user.setId(USER_ID);
        user.setCreatedAt(CREATED_AT);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password123!", ENCODED_PASSWORD)).thenReturn(true);
        when(passwordEncoder.encode("newpass")).thenReturn("hashedNewPass");

        // Mock save, return updated user.
        Answer<User> savedUserAnswer = invocation -> {
            User u = invocation.getArgument(0, User.class);
            u.setId(USER_ID);
            u.setCreatedAt(CREATED_AT);
            return u;
        };
        when(userRepository.save(any(User.class))).thenAnswer(savedUserAnswer);

        ApiResponse<User> resp = userService.updateUser(USER_ID, req);

        assertTrue(resp.isSuccess());
        assertEquals("User updated successfully", resp.getMessage());
        assertNotNull(resp.getData());
        assertEquals("Haaaasan", resp.getData().getFullName());
        assertEquals(EMAIL, resp.getData().getEmail());
        assertNull(resp.getData().getPassword());
    }
}