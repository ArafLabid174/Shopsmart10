package com.shopsmart.shopsmart_server.repository;

import com.shopsmart.shopsmart_server.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")  // Assumes a test profile is configured for MongoDB
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindUserByEmail() {
        // Arrange: Create and save a User
        User user = new User();
        user.setId("1");
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("passwordHash");
        userRepository.save(user);

        // Act: Retrieve User by email
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");

        // Assert: Verify the User is found
        assertTrue(foundUser.isPresent());
        assertEquals("test@example.com", foundUser.get().getEmail());
        assertEquals("Test User", foundUser.get().getFullName());
    }

    @Test
    void shouldReturnTrueIfEmailExists() {
        // Arrange: Create and save a User
        User user = new User();
        user.setId("2");
        user.setEmail("unique@example.com");
        user.setFullName("Unique User");
        user.setPassword("passwordHash");
        userRepository.save(user);

        // Act: Check existence of the email
        boolean exists = userRepository.existsByEmail("unique@example.com");

        // Assert: Verify the existence
        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseIfEmailDoesNotExist() {
        // Act & Assert: Check non-existence of an email
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");
        assertFalse(exists);
    }
}