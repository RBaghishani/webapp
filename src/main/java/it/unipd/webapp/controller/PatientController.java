package it.unipd.webapp.controller;

import it.unipd.webapp.entity.Patient;
import it.unipd.webapp.helpers.ResponseHelper;
import it.unipd.webapp.model.PatientModel;
import it.unipd.webapp.service.PatientService;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.PUT,RequestMethod.DELETE,RequestMethod.OPTIONS,RequestMethod.HEAD,RequestMethod.GET,RequestMethod.POST,RequestMethod.PATCH})
@RequestMapping(path = "api/v1/patient")
public class PatientController {

    private final PatientService patientService;

//    for dependency injection we use this, preventing from new Patient Service inside the controller also Component or
//    service on the other side
    @Autowired
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
//    @PreAuthorize("hasRole('USER')")
    @PermitAll
    public List<Patient> getPatients() {
        return patientService.getPatients();
    }

    @GetMapping(path = "{patientId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Patient getPatient(@PathVariable("patientId") Long patientId) {
        try {
            return patientService.getPatientsById(patientId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public Patient registerNewPatient(@ModelAttribute PatientModel patient) {
        Patient p = new Patient(patient.getFirstname(),patient.getLastname(),patient.getGender(),patient.getPhoneNumber(),patient.getAddress(),patient.getDob(),patient.getEmail(),patient.getPassword());
        MultipartFile avatar = patient.getAvatar();
        try {
            return patientService.addNewPatient(p, avatar);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping(path = "{patientId}")
    public void deletePatient(@PathVariable("patientId") Long patientId){
        patientService.deletePatient(patientId);
    }

    @PatchMapping(path = "{patientId}")
    public Patient updatePatient(@PathVariable("patientId") Long patientId,
                              @RequestBody Patient patient){
        return patientService.patchPatient(patientId, patient);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Patient>> searchPatients(
            @RequestParam(name = "firstname", required = false) String firstname,
            @RequestParam(name = "lastname", required = false) String lastname) {
        List<Patient> patients = patientService.searchPatients(firstname, lastname);
        return ResponseHelper.okay(patients, HttpStatus.OK);
    }

    @PatchMapping("/{patientId}/uploadProfilePicture")
    public ResponseEntity<Void> uploadProfilePicture(@PathVariable("patientId") Long patientId,
                                                     @RequestParam("file") MultipartFile file) {

        try {
            patientService.uploadProfilePicture(patientId, file);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok().build();
    }
}