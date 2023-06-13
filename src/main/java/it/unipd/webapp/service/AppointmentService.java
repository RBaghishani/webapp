package it.unipd.webapp.service;

import it.unipd.webapp.entity.Appointment;
import it.unipd.webapp.entity.User;
import it.unipd.webapp.enums.Role;
import it.unipd.webapp.model.AppointmentDto;
import it.unipd.webapp.exception.AppointmentConflictException;
import it.unipd.webapp.exception.AppointmentNotFoundException;
import it.unipd.webapp.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private AppointmentRepository appointmentRepository;

    private UserService userService;

    private final int duration = 30;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository, UserService userService) {
        this.appointmentRepository = appointmentRepository;
        this.userService = userService;
    }

    public Appointment createAppointment(AppointmentDto appointmentDto) throws IOException {
        LocalDateTime time = appointmentDto.getTime();
        if (!isTimeSlotAvailable(appointmentDto.getDoctorId(),time,duration))
            throw new AppointmentConflictException("There is not available time slot for this doctor at this time.", HttpStatus.NOT_FOUND);
        User doctor = userService.getUserByIdAndRole(appointmentDto.getDoctorId(), Role.DOCTOR);
        User patient = userService.getUserByIdAndRole(appointmentDto.getPatientId(), Role.PATIENT);
        LocalDateTime timePlusOne = time.plusMinutes(duration);
        String prescription = appointmentDto.getPrescription();

        if (appointmentRepository.existsByDoctorAndTimeBetween(doctor, time, timePlusOne))
            throw new AppointmentConflictException("An appointment already exists for this doctor at this time.", HttpStatus.CONFLICT);

        if (appointmentRepository.existsByPatientAndTimeBetween(patient, time, timePlusOne))
            throw new AppointmentConflictException("An appointment already exists for this patient at this time.", HttpStatus.CONFLICT);

        Appointment appointment = Appointment.builder()
                .doctor(doctor)
                .patient(patient)
                .time(time)
                .prescription(prescription)
                .build();

        return appointmentRepository.save(appointment);
    }

    public List<Appointment> listAppointments(Long doctorId, Long patientId, LocalDate date) throws IOException {
        User doctor = null;
        User patient = null;
        if (doctorId != null) {
            doctor = userService.getUserByIdAndRole(doctorId, Role.DOCTOR);
        }
        if (patientId != null) {
            patient = userService.getUserByIdAndRole(patientId, Role.PATIENT);
        }
        if (date != null) {
            LocalDateTime startOfDay = date.atStartOfDay().plusHours(8);
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX).minusHours(8);
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
        LocalDateTime time = appointmentDto.getTime();
        if (!isTimeSlotAvailable(appointmentDto.getDoctorId(),time,duration))
            throw new AppointmentConflictException("There is not available time slot for this doctor at this time.", HttpStatus.NOT_FOUND);

        User doctor = userService.getUserByIdAndRole(appointmentDto.getDoctorId(), Role.DOCTOR);
        User patient = userService.getUserByIdAndRole(appointmentDto.getPatientId(), Role.PATIENT);
        LocalDateTime timePlusOne = time.plusMinutes(30);
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

    public List<LocalDateTime> listAvailableTimeSlots(Long doctorId, LocalDate date) throws IOException {
        User doctor = userService.getUserByIdAndRole(doctorId, Role.DOCTOR);
        LocalDateTime startOfDay = date.atStartOfDay().plusHours(8);
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX).minusHours(8);
        List<Appointment> appointments = appointmentRepository.findByDoctorAndTimeBetween(doctor, startOfDay, endOfDay);
        List<LocalDateTime> allTimeSlots = getAllTimeSlots(startOfDay, endOfDay);
        List<LocalDateTime> bookedTimeSlots = appointments.stream()
                .map(Appointment::getTime)
                .collect(Collectors.toList());
        return allTimeSlots.stream()
                .filter(timeSlot -> !bookedTimeSlots.contains(timeSlot))
                .collect(Collectors.toList());
    }

    private List<LocalDateTime> getAllTimeSlots(LocalDateTime start, LocalDateTime end) {
        List<LocalDateTime> timeSlots = new ArrayList<>();
        LocalDateTime current = start;
        while (current.isBefore(end)) {
            timeSlots.add(current);
            current = current.plusMinutes(duration);
        }
        return timeSlots;
    }

    public boolean isTimeSlotAvailable(Long doctorId, LocalDateTime time, int duration) throws IOException {
        List<LocalDateTime> availableTimeSlots = listAvailableTimeSlots(doctorId, time.toLocalDate());
        LocalDateTime timePlusDuration = time.plusMinutes(duration);
        for (LocalDateTime availableTimeSlot : availableTimeSlots) {
            LocalDateTime availableTimeSlotPlusDuration = availableTimeSlot.plusMinutes(duration);
            if ((time.isAfter(availableTimeSlot) && time.isBefore(availableTimeSlotPlusDuration))
                    || (timePlusDuration.isAfter(availableTimeSlot) && timePlusDuration.isBefore(availableTimeSlotPlusDuration))
                    || (time.isBefore(availableTimeSlot) && timePlusDuration.isAfter(availableTimeSlotPlusDuration))) {
                return false;
            }
        }
        return availableTimeSlots.contains(time);
    }
}
