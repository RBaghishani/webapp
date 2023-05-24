package it.unipd.webapp.config;

import it.unipd.webapp.model.RegisterRequest;
import it.unipd.webapp.service.AuthenticationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static it.unipd.webapp.enums.Role.ADMIN;
import static it.unipd.webapp.enums.Role.DOCTOR;

@Configuration
public class UserConfig {

    /*@Bean
    CommandLineRunner commandLineRunner(PatientRepository repository){
        String pass = BCrypt.withDefaults().hashToString(10, "pass".toCharArray());
        String word = BCrypt.withDefaults().hashToString(10, "word".toCharArray());

        return args -> {
            Patient mariam = new Patient(
                    "mariam",
                    "fagnioli",
                    Gender.FEMALE,
                    "09999999",
                    "via santa lucia",
                    LocalDate.of(2000, Month.APRIL, 26),
                    "mariam@hi.com",
                    pass);
            Patient alex = new Patient(
                    "alex",
                    "castagnoli",
                    Gender.MALE,
                    "0999888",
                    "via santa lucia",
                    LocalDate.of(2005, Month.MARCH, 7),
                    "alex@hi.com",
                    word);

            repository.saveAll(
                    List.of(mariam, alex)
            );
        };
    }*/

    @Bean
    public CommandLineRunner commandLineRunner(
            AuthenticationService service
    ) {
        return args -> {
            var admin = RegisterRequest.builder()
                    .firstname("Admin")
                    .lastname("Admin")
                    .email("admin@mail.com")
                    .password("password")
                    .role(ADMIN)
                    .build();
            System.out.println("Admin token: " + service.register(admin).getAccessToken());

            var manager = RegisterRequest.builder()
                    .firstname("Manager")
                    .lastname("Manager")
                    .email("manager@mail.com")
                    .password("password")
                    .role(DOCTOR)
                    .build();
            System.out.println("Doctor token: " + service.register(manager).getAccessToken());

        };
    }
}
