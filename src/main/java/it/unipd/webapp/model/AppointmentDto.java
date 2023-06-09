package it.unipd.webapp.model;

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
    private LocalDateTime time;
    private String prescription;
    //todo add file also
}