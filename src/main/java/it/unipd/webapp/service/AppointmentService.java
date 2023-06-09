package it.unipd.webapp.service;

import it.unipd.webapp.entity.Appointment;
import it.unipd.webapp.entity.User;
import it.unipd.webapp.enums.Role;
import it.unipd.webapp.model.AppointmentDto;
import it.unipd.webapp.model.exception.AppointmentConflictException;
import it.unipd.webapp.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

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

}
