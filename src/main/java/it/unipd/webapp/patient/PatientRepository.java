package it.unipd.webapp.patient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository
        extends JpaRepository<Patient, Long> {

    //SELECT * FROM Patient WHERE email = ?
    @Query("SELECT p FROM Patient p where p.email=?1")
    Optional<Patient> findPatientByEmail(String email);

    // Query method to find patients by firstname and lastname
    List<Patient> findByFirstnameContainingIgnoreCaseOrLastnameContainingIgnoreCase(String firstname, String lastname);

}
