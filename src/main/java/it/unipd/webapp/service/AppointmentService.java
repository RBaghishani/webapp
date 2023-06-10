package it.unipd.webapp.service;

import it.unipd.webapp.entity.Appointment;
import it.unipd.webapp.entity.User;
import it.unipd.webapp.enums.Role;
import it.unipd.webapp.model.AppointmentDto;
import it.unipd.webapp.model.exception.AppointmentConflictException;
import it.unipd.webapp.model.exception.AppointmentNotFoundException;
import it.unipd.webapp.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class AppointmentService {

    private AppointmentRepository appointmentRepository;

    private UserService userService;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository, UserService userService) {
        this.appointmentRepository = appointmentRepository;
        this.userService = userService;
    }

    public Appointment createAppointment(AppointmentDto appointmentDto) throws IOException {
        User doctor = userService.getUserByIdAndRole(appointmentDto.getDoctorId(), Role.DOCTOR);
        User patient = userService.getUserByIdAndRole(appointmentDto.getPatientId(), Role.PATIENT);
        LocalDateTime time = appointmentDto.getTime();
        LocalDateTime timePlusOne = time.plusHours(1L);
        String prescription = appointmentDto.getPrescription();

        if (appointmentRepository.existsByDoctorAndTimeBetween(doctor, time, timePlusOne))
            throw new AppointmentConflictException("An appointment already exists for this doctor at this time.", HttpStatus.CONFLICT);

        if (appointmentRepository.existsByPatientAndTimeBetween(doctor, time, timePlusOne))
            throw new AppointmentConflictException("An appointment already exists for this patient at this time.", HttpStatus.CONFLICT);

        Appointment appointment = Appointment.builder()
                .doctor(doctor)
                .patient(patient)
                .time(time)
                .prescription(prescription)
                .build();

        return appointmentRepository.save(appointment);
    }

    public List<Appointment> listAppointments(Long doctorId, Long patientId, LocalDateTime date) throws IOException {
        User doctor = null;
        User patient = null;
        if (doctorId != null) {
            doctor = userService.getUserByIdAndRole(doctorId, Role.DOCTOR);
        }
        if (patientId != null) {
            patient = userService.getUserByIdAndRole(patientId, Role.PATIENT);
        }
        if (date != null) {
            LocalDateTime startOfDay = date.with(LocalTime.MIN);
            LocalDateTime endOfDay = date.with(LocalTime.MAX);
            return appointmentRepository.findByDoctorOrPatientAndTimeBetween(doctor, patient, startOfDay, endOfDay);
        } else if (patient != null || doctor!= null){
            return appointmentRepository.findByDoctorOrPatient(doctor, patient);
        } else {
            return appointmentRepository.findAll();
        }
    }

    public Appointment updateAppointment(Long id, AppointmentDto appointmentDto) throws IOException {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found.", HttpStatus.NOT_FOUND));

        User doctor = userService.getUserByIdAndRole(appointmentDto.getDoctorId(), Role.DOCTOR);
        User patient = userService.getUserByIdAndRole(appointmentDto.getPatientId(), Role.PATIENT);
        LocalDateTime time = appointmentDto.getTime();
        LocalDateTime timePlusOne = time.plusHours(1L);
        String prescription = appointmentDto.getPrescription();

        if (appointmentRepository.existsByDoctorAndTimeBetween(doctor, time, timePlusOne) && !appointment.getDoctor().equals(doctor))
            throw new AppointmentConflictException("An appointment already exists for this doctor at this time.", HttpStatus.CONFLICT);

        if (appointmentRepository.existsByPatientAndTimeBetween(doctor, time, timePlusOne) && !appointment.getPatient().equals(patient))
            throw new AppointmentConflictException("An appointment already exists for this patient at this time.", HttpStatus.CONFLICT);

        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setTime(time);
        appointment.setPrescription(prescription);

        return appointmentRepository.save(appointment);
    }
}
