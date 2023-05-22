package it.unipd.webapp.controller;

import it.unipd.webapp.entity.User;
import it.unipd.webapp.helpers.ResponseHelper;
import it.unipd.webapp.model.AuthenticationResponse;
import it.unipd.webapp.model.PatientDto;
import it.unipd.webapp.model.RegisterRequest;
import it.unipd.webapp.service.PatientService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.PUT,RequestMethod.DELETE,RequestMethod.OPTIONS,RequestMethod.HEAD,RequestMethod.GET,RequestMethod.POST,RequestMethod.PATCH})
@RequestMapping(path = "api/v1/patient")
@PreAuthorize("hasAnyRole('ADMIN','DOCTOR','PATIENT')")
public class PatientController {

    private final PatientService patientService;

    @Autowired
    private ModelMapper modelMapper;

//    for dependency injection we use this, preventing from new Patient Service inside the controller also Component or
//    service on the other side
    @Autowired
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public List<PatientDto> getPatients() {
        List<PatientDto> patientDtos= patientService.getPatients()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return patientDtos;
//        return patientService.getPatients();
    }

    @GetMapping(path = "{patientId}")
    public PatientDto getPatient(@PathVariable("patientId") Long patientId) {
        try {
            return convertToDto(patientService.getPatientsById(patientId));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Object> registerNewPatient(RegisterRequest request) {
        try {
            AuthenticationResponse response = patientService.addNewPatient(request);
            return ResponseHelper.okay(response, HttpStatus.CREATED);
        } catch (IOException e) {
            return ResponseHelper.error("Error occurred while adding new patient: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(path = "{patientId}")
    public void deletePatient(@PathVariable("patientId") Long patientId){
        patientService.deletePatient(patientId);
    }

    @PatchMapping(path = "{patientId}")
    public PatientDto updatePatient(@PathVariable("patientId") Long patientId,
                              @RequestBody User patient){
        return convertToDto(patientService.patchPatient(patientId, patient));
    }

    @GetMapping("/search")
    public ResponseEntity<List<PatientDto>> searchPatients(
            @RequestParam(name = "firstname", required = false) String firstname,
            @RequestParam(name = "lastname", required = false) String lastname) {
        List<PatientDto> patients = patientService.searchPatients(firstname, lastname)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
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

    private PatientDto convertToDto(User patient) {
        return modelMapper.map(patient, PatientDto.class);
    }
}
