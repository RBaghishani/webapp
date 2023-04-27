package it.unipd.webapp.patient;

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
        return args -> {
            Patient mariam = new Patient(
                    "mariam",
                    LocalDate.of(2000, Month.APRIL, 26),
                    "hi@hi.com");
            Patient alex = new Patient(
                    "alex",
                    LocalDate.of(2005, Month.MARCH, 7),
                    "alex@hi.com");
            repository.saveAll(
                    List.of(mariam, alex)
            );
        };
    }
}
