package it.unipd.webapp.patient;

import at.favre.lib.crypto.bcrypt.BCrypt;
import it.unipd.webapp.enums.Gender;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Configuration
public class PatientConfig {

    @Bean
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
    }
}
