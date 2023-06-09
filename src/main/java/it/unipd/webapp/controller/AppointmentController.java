package it.unipd.webapp.controller;

import it.unipd.webapp.entity.Appointment;
import it.unipd.webapp.model.AppointmentDto;
import it.unipd.webapp.exception.AppointmentConflictException;
import it.unipd.webapp.exception.AppointmentNotFoundException;
import it.unipd.webapp.service.AppointmentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.PUT,RequestMethod.DELETE,RequestMethod.OPTIONS,RequestMethod.HEAD,RequestMethod.GET,RequestMethod.POST,RequestMethod.PATCH})
@RequestMapping("/api/v1/appointment")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping
    @PreAuthorize("(hasRole('PATIENT') and #appointmentDto.getPatientId() == authentication.principal.getId()) or (hasRole('DOCTOR') and #appointmentDto.getDoctorId() == authentication.principal.getId()) or hasRole('ADMIN')")
    public ResponseEntity<?> createAppointment(@Valid @RequestBody AppointmentDto appointmentDto) {
        try {
            Appointment appointment = appointmentService.createAppointment(appointmentDto);
            AppointmentDto responseDto = modelMapper.map(appointment, AppointmentDto.class);
            return ResponseEntity.ok(responseDto);
        } catch (AppointmentConflictException e) {
            return ResponseEntity.status(e.getStatus()).body(Collections.singletonMap("message", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    @GetMapping
    @PreAuthorize("(hasRole('PATIENT') and #patientId == authentication.principal.getId()) or (hasRole('DOCTOR') and #doctorId == authentication.principal.getId()) or hasRole('ADMIN')")
    public ResponseEntity<?> listAppointments(
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        try {
            List<Appointment> appointments = appointmentService.listAppointments(doctorId, patientId, date);
            List<AppointmentDto> responseDtos = appointments.stream()
                    .map(appointment -> modelMapper.map(appointment, AppointmentDto.class))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responseDtos);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PutMapping("/{id}")
    @PreAuthorize("(hasRole('PATIENT') and #appointmentDto.getPatientId() == authentication.principal.getId()) or (hasRole('DOCTOR') and #appointmentDto.getDoctorId() == authentication.principal.getId()) or hasRole('ADMIN')")
    public ResponseEntity<?> updateAppointment(@PathVariable Long id, @Valid @RequestBody AppointmentDto appointmentDto) {
        try {
            Appointment appointment = appointmentService.updateAppointment(id, appointmentDto);
            AppointmentDto responseDto = modelMapper.map(appointment, AppointmentDto.class);
            return ResponseEntity.ok(responseDto);
        } catch (AppointmentNotFoundException e) {
            return ResponseEntity.status(e.getStatus()).body(Collections.singletonMap("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    @GetMapping("/available")
    public ResponseEntity<?> listAvailableTimeSlots(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        try {
            List<LocalDateTime> availableTimeSlots = appointmentService.listAvailableTimeSlots(doctorId, date);
            return ResponseEntity.ok(availableTimeSlots);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/check-code/{code}")
    public ResponseEntity<?> checkAppointmentCode(
            @Pattern(regexp="^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")
            @PathVariable String code
    ) {
        try {
            Appointment appointment = appointmentService.getAppointmentByCode(code);
            AppointmentDto responseDto = modelMapper.map(appointment, AppointmentDto.class);
            return ResponseEntity.ok(responseDto);
        } catch (AppointmentNotFoundException e) {
            return ResponseEntity.status(e.getStatus()).body(Collections.singletonMap("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", e.getMessage()));
        }
    }
}
