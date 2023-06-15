package it.unipd.webapp.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import it.unipd.webapp.entity.User;
import it.unipd.webapp.enums.Role;
import it.unipd.webapp.helpers.AesFileUtils;
import it.unipd.webapp.model.AuthenticationResponse;
import it.unipd.webapp.model.RegisterRequest;
import it.unipd.webapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static it.unipd.webapp.service.Utils.saveFile;

@Service
public class UserService {

    private static final String ENCRYPTED_DIRECTORY = "encrypted";
    private final UserRepository userRepository;

    private final AuthenticationService authenticationService;

    @Autowired
    public UserService(UserRepository userRepository, AuthenticationService authenticationService) {
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
    }

    public List<User> getUsers(Role role) {
        return userRepository.findByRole(role);
    }

    public AuthenticationResponse addNewUser(RegisterRequest request, Role role) throws Exception {
        request.setRole(role);
        return authenticationService.register(request);
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found - " + userId));

        // Delete user's profile picture if it exists
        deleteExistingProfilePicture(user);

        userRepository.deleteById(userId);
    }

    @Transactional
    public User patchUser(Long userId, User userToUpdate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found - " + userId));

        if (userToUpdate.getFirstname() != null && !userToUpdate.getFirstname().equals(user.getFirstname())) {
            user.setFirstname(userToUpdate.getFirstname());
        }

        if (userToUpdate.getLastname() != null && !userToUpdate.getLastname().equals(user.getLastname())) {
            user.setLastname(userToUpdate.getLastname());
        }

        if (userToUpdate.getGender() != null && !userToUpdate.getGender().equals(user.getGender())) {
            user.setGender(userToUpdate.getGender());
        }

        if (userToUpdate.getPhoneNumber() != null && !userToUpdate.getPhoneNumber().equals(user.getPhoneNumber())) {
            user.setPhoneNumber(userToUpdate.getPhoneNumber());
        }

        if (userToUpdate.getAddress() != null && !userToUpdate.getAddress().equals(user.getAddress())) {
            user.setAddress(userToUpdate.getAddress());
        }

        if (userToUpdate.getDob() != null && !userToUpdate.getDob().equals(user.getDob())) {
            user.setDob(userToUpdate.getDob());
        }

        if (userToUpdate.getEmail() != null && !userToUpdate.getEmail().equals(user.getEmail())) {
            Optional<User> userOptional = userRepository.findByEmail(userToUpdate.getEmail());
            if (userOptional.isPresent()) {
                throw new IllegalStateException("Email already taken!");
            }
            user.setEmail(userToUpdate.getEmail());
        }

        if (userToUpdate.getPassword() != null && !userToUpdate.getPassword().equals(user.getPassword())) {
            String bcryptHashString = BCrypt.withDefaults().hashToString(10, userToUpdate.getPassword().toCharArray());
            user.setPassword(bcryptHashString);
        }

        return userRepository.save(user);
    }

    public User getUserByIdAndRole(Long userId, Role role) throws Exception {
        User user = userRepository.findByIdAndRole(userId, role)
                .orElseThrow(() -> new IllegalStateException("User not found - " + userId));
        if (user.getProfilePicture() != null){
            // Decrypt the file and set the avatar field
            Path inputFile = Paths.get(ENCRYPTED_DIRECTORY, user.getProfilePicture());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                AesFileUtils.decryptFile(inputFile, outputStream);
            } catch (BadPaddingException e) {
                // Handle exception
                throw new Exception("Error decrypting file: " + e.getMessage());
            }
            byte[] decryptedBytes = outputStream.toByteArray();
            String base64Img = Base64.getEncoder().encodeToString(decryptedBytes);
            user.setAvatar(base64Img);
        }
        return user;
    }

    public List<User> searchUsers(String firstname, String lastname, Role role) {
        System.out.println("Searching for users with firstname: " + firstname + ", lastname: " + lastname);
        return userRepository.findByNameOrLastNameOrRole(firstname, lastname, role);

    }

    public void uploadProfilePicture(Long userId, MultipartFile file) throws Exception {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()) {
            throw new IllegalArgumentException("User not found - " + userId);
        }

        User user = optionalUser.get();

        // Delete previous avatar if it exists
        deleteExistingProfilePicture(user);

        String filename = saveFile(file);

        // Update user's profile picture filename
        user.setProfilePicture(filename);
        userRepository.save(user);
    }

    private static void deleteExistingProfilePicture(User user) {
        if (user.getProfilePicture() != null) {
            File encryptedFile = new File(ENCRYPTED_DIRECTORY + File.separator + user.getProfilePicture());
            if (encryptedFile.exists()) {
                encryptedFile.delete();
            }
        }
    }

}
