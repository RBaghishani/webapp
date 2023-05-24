package it.unipd.webapp.entity;

import it.unipd.webapp.enums.Gender;
import it.unipd.webapp.enums.Role;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
public class User implements UserDetails {

    @Id
    @GeneratedValue
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Token> tokens;

    //
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String phoneNumber;
    private String address;
    @Transient
    private Integer age;
    private LocalDate dob;
    @Column(name = "profile_picture")
    private String profilePicture;
    @Transient
    private String avatar;

    private boolean isMfaEnable;
    private String secretKey;
    private int validationCode;
    private String scratchCodes;

    //
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities(); //BECAUSE A USER JUST HAVE ONE ROLE
//        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    //
    public Integer getAge() {
        return Period.between(this.dob, LocalDate.now()).getYears();
    }

    public List<Integer> getScratchCodes() {
        return Arrays.stream(scratchCodes.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    public void setScratchCodes(List<Integer> scratchCodes) {
        this.scratchCodes = String.join(",", scratchCodes.stream().map(Object::toString).collect(Collectors.toList()));
    }
}