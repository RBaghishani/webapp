package it.unipd.webapp.patient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.unipd.webapp.enums.Gender;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.Period;

@Entity
@Table
public class Patient {
    @Id
    @SequenceGenerator(
            name = "patient_sequence",
            sequenceName = "patient_sequence",
            initialValue = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "patient_sequence"
    )
    private Long id;
    private String firstname;
    private String lastname;
    //TODO password should go to account management
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String phoneNumber;
    private String address;
    @Transient
    private Integer age;
    private LocalDate dob;
    private String email;

    @Column(nullable = false) // Make sure password cannot be null
    private String password;

    public Patient() {
    }

    public Patient(Long id, String firstname, String lastname, Gender gender, String phoneNumber,
                   String address, Integer age, LocalDate dob, String email, String password) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.dob = dob;
        this.email = email;
        this.password = password;//new BCryptPasswordEncoder().encode(password);
    }

    public Patient(String firstname, String lastname, Gender gender, String phoneNumber,
                   String address, LocalDate dob, String email, String password) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.dob = dob;
        this.email = email;
        this.password = password; //new BCryptPasswordEncoder().encode(password);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getAge() {
        return Period.between(this.dob, LocalDate.now()).getYears();
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }
    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", gender=" + gender +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", age=" + age +
                ", dob=" + dob +
                ", email='" + email + '\'' +
                '}';
    }
}
