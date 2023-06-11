package it.unipd.webapp.model;

import it.unipd.webapp.enums.Gender;
import it.unipd.webapp.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String firstname;
    private String lastname;
    private String email;
    @NonNull
    private String password;
    private Role role;

    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String phoneNumber;
    private String address;
    private String specialization;
    private LocalDate dob;
    private MultipartFile avatar;
}