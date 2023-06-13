package it.unipd.webapp.model;

import jakarta.validation.constraints.Future;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDto {
    private Long doctorId;
    private Long patientId;
    @Future
    private LocalDateTime time;
    private String prescription;
    //todo add file also
}