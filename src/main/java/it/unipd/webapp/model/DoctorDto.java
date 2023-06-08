package it.unipd.webapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.unipd.webapp.enums.Gender;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDto {

    private String firstname;
    private String lastname;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String phoneNumber;
    private String address;
    private String email;
    private String avatar;
    private String specialization;
    @JsonIgnore
    private boolean isMfaEnable;
}

