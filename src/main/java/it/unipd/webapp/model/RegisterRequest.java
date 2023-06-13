package it.unipd.webapp.model;

import it.unipd.webapp.enums.Gender;
import it.unipd.webapp.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank
    @Size(min = 2, max = 30)
    private String firstname;
    @NotBlank
    @Size(min = 2, max = 30)
    private String lastname;
    @NotBlank
    @Email
    private String email;
    @NonNull
    @Size(min = 8)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Gender gender;
    @NotBlank
    @Pattern(regexp = "^(\\+\\d{1,2}\\s?)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$")
    private String phoneNumber;
    private String address;
    private String specialization;
    private LocalDate dob;
    @Size(max = 10000000)
    private MultipartFile avatar;
}