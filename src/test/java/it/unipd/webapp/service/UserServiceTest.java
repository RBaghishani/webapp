package it.unipd.webapp.service;

import it.unipd.webapp.entity.User;
import it.unipd.webapp.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private UserService userService;

    @Mock
    private MultipartFile mockMultipartFile;

    @Test
    @DisplayName("Should upload and save the profile picture when the user exists")
    void uploadProfilePictureWhenUserExists() {
        Long userId = 1L;
        String filename = "test.jpg";
        User user = new User();
        user.setId(userId);
        Optional<User> optionalUser = Optional.of(user);

        when(userRepository.findById(userId)).thenReturn(optionalUser);
        when(mockMultipartFile.getOriginalFilename()).thenReturn(filename);

        try {
            userService.uploadProfilePicture(userId, mockMultipartFile);
        } catch (IOException e) {
            fail("IOException should not have been thrown");
        }

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
        assertEquals(filename, user.getProfilePicture());
    }

    @Test
    @DisplayName("Should throw an exception when the user does not exist")
    void uploadProfilePictureWhenUserDoesNotExistThenThrowException() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            userService.uploadProfilePicture(userId, mockMultipartFile);
        });

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(0)).save(any(User.class));
    }

}