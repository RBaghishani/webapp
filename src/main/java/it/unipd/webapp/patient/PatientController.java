package it.unipd.webapp.patient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
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
    public void registerNewPatient(@RequestBody Patient patient) {
        patientService.addNewPatient(patient);
    }

    @DeleteMapping(path = "{patientId}")
    public void deletePatient(@PathVariable("patientId") Long patientId){
        patientService.deletePatient(patientId);
    }

    @PatchMapping(path = "{patientId}")
    public void updatePatient(@PathVariable("patientId") Long patientId,
                              @RequestParam(required = false) String name, @RequestParam(required = false) String email){
        patientService.patchPatient(patientId, name, email);
    }
}
