package it.unipd.webapp.controller;

import it.unipd.webapp.entity.Appointment;
import it.unipd.webapp.model.AppointmentDto;
import it.unipd.webapp.model.exception.AppointmentConflictException;
import it.unipd.webapp.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;

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
}
