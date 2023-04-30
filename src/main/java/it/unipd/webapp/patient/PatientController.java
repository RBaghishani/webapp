package it.unipd.webapp.patient;

import it.unipd.webapp.helpers.ResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
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
    public List<Patient> getPatients() {
        return patientService.getPatients();
    }

    @GetMapping(path = "{patientId}")
    public Patient getPatient(@PathVariable("patientId") Long patientId) {
        return patientService.getPatientsById(patientId);
    }

    @PostMapping
    public Patient registerNewPatient(@RequestBody Patient patient) {
        return patientService.addNewPatient(patient);
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
}
