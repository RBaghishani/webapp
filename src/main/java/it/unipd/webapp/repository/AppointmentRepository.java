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

    List<Appointment> findByDoctorOrPatient(User doctor, User patient);
    Optional<Appointment> findById(Long id);

    List<Appointment> findByDoctorOrPatientAndTimeBetween(User doctor, User patient,  LocalDateTime startOfDay, LocalDateTime endOfDay);

    List<Appointment> findByDoctorAndTimeBetween(User doctor, LocalDateTime startOfDay, LocalDateTime endOfDay);

    boolean existsByDoctorAndTimeBetween(User doctor, LocalDateTime startTime, LocalDateTime endTime);

    boolean existsByPatientAndTimeBetween(User patient, LocalDateTime startTime, LocalDateTime endTime);
}
