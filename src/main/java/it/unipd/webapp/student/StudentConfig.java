package it.unipd.webapp.student;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Configuration
public class StudentConfig {

    @Bean
    CommandLineRunner commandLineRunner(StudentRepository repository){
        return args -> {
            Student mariam = new Student(
                    "mariam",
                    LocalDate.of(2000, Month.APRIL, 26),
                    "hi@hi.com");
            Student alex = new Student(
                    "alex",
                    LocalDate.of(2005, Month.MARCH, 7),
                    "alex@hi.com");
            repository.saveAll(
                    List.of(mariam, alex)
            );
        };
    }
}
