package it.unipd.webapp.controller;

import it.unipd.webapp.entity.Appointment;
import it.unipd.webapp.model.AppointmentDto;
import it.unipd.webapp.model.exception.AppointmentConflictException;
import it.unipd.webapp.model.exception.AppointmentNotFoundException;
import it.unipd.webapp.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
    public ResponseEntity<?> createAppointment(@RequestBody AppointmentDto appointmentDto) {
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
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {

        try {
            List<Appointment> appointments = appointmentService.listAppointments(doctorId, patientId, date);
            List<AppointmentDto> responseDtos = appointments.stream()
                    .map(appointment -> modelMapper.map(appointment, AppointmentDto.class))
                    .collect(Collectors.toList());
//            Map<String, Object> response = new HashMap<>();
//            response.put("message", "Appointments retrieved successfully.");
//            response.put("appointments", responseDtos);
            return ResponseEntity.ok(responseDtos);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PutMapping("/{id}")
    @PreAuthorize("(hasRole('PATIENT') and #appointmentDto.getPatientId() == authentication.principal.getId()) or (hasRole('DOCTOR') and #appointmentDto.getDoctorId() == authentication.principal.getId()) or hasRole('ADMIN')")
    public ResponseEntity<?> updateAppointment(@PathVariable Long id, @RequestBody AppointmentDto appointmentDto) {
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

}
