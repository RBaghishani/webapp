package it.unipd.webapp.model;

import it.unipd.webapp.enums.Gender;
import jakarta.persistence.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public class PatientModel {

    private String firstname;
    private String lastname;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String phoneNumber;
    private String address;
    private LocalDate dob;
    private String email;
    private String password;
    private MultipartFile avatar;

    public PatientModel(String firstname, String lastname, Gender gender, String phoneNumber, String address, LocalDate dob, String email, String password, MultipartFile avatar) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.dob = dob;
        this.email = email;
        this.password = password;
        this.avatar = avatar;
    }


    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public Gender getGender() {
        return gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public LocalDate getDob() {
        return dob;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public MultipartFile getAvatar() {
        return avatar;
    }
}

