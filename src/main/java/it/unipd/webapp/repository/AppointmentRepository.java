package it.unipd.webapp.repository;

import it.unipd.webapp.entity.Appointment;
import it.unipd.webapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Optional<Appointment> findByDoctor(Appointment doctor);
    Optional<Appointment> findById(Long id);

    List<Appointment> findByPatient(Appointment patient);

    boolean existsByDoctorAndTimeBetween(User doctor, LocalDateTime startTime, LocalDateTime endTime);

    boolean existsByPatientAndTimeBetween(User patient, LocalDateTime startTime, LocalDateTime endTime);
}
