package it.unipd.webapp.repository;
//https://medium.com/@vlad.milytin/google-authenticator-in-java-spring-e7e40e5b9a86

import com.warrenstrange.googleauth.ICredentialRepository;
import it.unipd.webapp.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CredentialRepository implements ICredentialRepository {

    @Autowired
    private UserRepository userRepository;

    @Override
    public String getSecretKey(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow();
        return user.getSecretKey();
    }

    @Override
    public void saveUserCredentials(String email,
                                    String secretKey,
                                    int validationCode,
                                    List<Integer> scratchCodes) {
        User user = userRepository.findByEmail(email)
                .orElseThrow();
        user.setSecretKey(secretKey);
        user.setValidationCode(validationCode);
        user.setScratchCodes(scratchCodes);
        userRepository.save(user);
    }

    public UserTOTP getUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow();
        UserTOTP userTOTP = new UserTOTP(email, user.getSecretKey(), user.getValidationCode(), user.getScratchCodes());
        return userTOTP;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class UserTOTP {
        private String email;
        private String secretKey;
        private int validationCode;
        private List<Integer> scratchCodes;
    }
}