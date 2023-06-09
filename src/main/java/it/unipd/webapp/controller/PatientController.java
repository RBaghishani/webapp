package it.unipd.webapp.controller;

import it.unipd.webapp.entity.User;
import it.unipd.webapp.enums.Role;
import it.unipd.webapp.helpers.ResponseHelper;
import it.unipd.webapp.model.AuthenticationResponse;
import it.unipd.webapp.model.PatientDto;
import it.unipd.webapp.model.RegisterRequest;
import it.unipd.webapp.service.UserService;
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
@PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
public class PatientController {

    private final UserService userService;

    @Autowired
    private ModelMapper modelMapper;

//    for dependency injection we use this, preventing from new Patient Service inside the controller also Component or
//    service on the other side
    @Autowired
    public PatientController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<PatientDto>> getPatients() {
        List<PatientDto> patientDtos= userService.getUsers(Role.PATIENT)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseHelper.okay(patientDtos, HttpStatus.OK);
    }

    @GetMapping(path = "{patientId}")
    @PreAuthorize("(hasRole('PATIENT') and #patientId == authentication.principal.getId()) or hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<PatientDto> getPatient(@PathVariable("patientId") Long patientId) {
        try {
            PatientDto patientDto = convertToDto(userService.getUserByIdAndRole(patientId, Role.PATIENT));
            return ResponseHelper.okay(patientDto, HttpStatus.OK);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Object> registerNewPatient(RegisterRequest request) {
        try {
            AuthenticationResponse response = userService.addNewUser(request, Role.PATIENT);
            return ResponseHelper.okay(response, HttpStatus.CREATED);
        } catch (IOException e) {
            return ResponseHelper.error("Error occurred while adding new patient: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(path = "{patientId}")
    public ResponseEntity<Void> deletePatient(@PathVariable("patientId") Long patientId){
        userService.deleteUser(patientId);
        return ResponseHelper.okay(null, HttpStatus.NO_CONTENT);
    }

    @PatchMapping(path = "{patientId}")
    @PreAuthorize("hasRole('PATIENT') and #patientId == authentication.principal.getId() or hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<PatientDto> updatePatient(@PathVariable("patientId") Long patientId,
                                                    @RequestBody User patient){
        PatientDto patientDto = convertToDto(userService.patchUser(patientId, patient));
        return ResponseHelper.okay(patientDto, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PatientDto>> searchPatients(
            @RequestParam(name = "firstname", required = false) String firstname,
            @RequestParam(name = "lastname", required = false) String lastname) {
        List<PatientDto> patients = userService.searchUsers(firstname, lastname, Role.PATIENT)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseHelper.okay(patients, HttpStatus.OK);
    }

    @PatchMapping("/{patientId}/uploadProfilePicture")
    @PreAuthorize("hasRole('PATIENT') and #patientId == authentication.principal.getId() or hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<Void> uploadProfilePicture(@PathVariable("patientId") Long patientId,
                                                     @RequestParam("file") MultipartFile file) {

        try {
            userService.uploadProfilePicture(patientId, file);
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
