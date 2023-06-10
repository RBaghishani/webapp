package it.unipd.webapp.controller;

import it.unipd.webapp.entity.User;
import it.unipd.webapp.enums.Role;
import it.unipd.webapp.helpers.ResponseHelper;
import it.unipd.webapp.model.AuthenticationResponse;
import it.unipd.webapp.model.DoctorDto;
import it.unipd.webapp.model.RegisterRequest;
import it.unipd.webapp.service.UserService;
import jakarta.annotation.security.PermitAll;
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
@RequestMapping(path = "api/v1/doctor")
@PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
public class DoctorController {

    private final UserService userService;

    @Autowired
    private ModelMapper modelMapper;

//    for dependency injection we use this, preventing from new Doctor Service inside the controller also Component or
//    service on the other side
    @Autowired
    public DoctorController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<DoctorDto>> getDoctors() {
        List<DoctorDto> doctorDtos= userService.getUsers(Role.DOCTOR)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseHelper.okay(doctorDtos, HttpStatus.OK);
    }

    @GetMapping(path = "{doctorId}")
    public ResponseEntity<DoctorDto> getDoctor(@PathVariable("doctorId") Long doctorId) {
        try {
            DoctorDto doctorDto = convertToDto(userService.getUserByIdAndRole(doctorId, Role.DOCTOR));
            return ResponseHelper.okay(doctorDto, HttpStatus.OK);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> registerNewDoctor(RegisterRequest request) {
        try {
            AuthenticationResponse response = userService.addNewUser(request, Role.DOCTOR);
            return ResponseHelper.okay(response, HttpStatus.CREATED);
        } catch (IOException e) {
            return ResponseHelper.error("Error occurred while adding new doctor: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(path = "{doctorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDoctor(@PathVariable("doctorId") Long doctorId){
        userService.deleteUser(doctorId);
        return ResponseHelper.okay(null, HttpStatus.NO_CONTENT);
    }

    @PatchMapping(path = "{doctorId}")
    @PreAuthorize("(hasRole('DOCTOR') and #doctorId == authentication.principal.getId())or hasRole('ADMIN')")
    public ResponseEntity<DoctorDto> updateDoctor(@PathVariable("doctorId") Long doctorId,
                                                    @RequestBody User doctor){
        DoctorDto doctorDto = convertToDto(userService.patchUser(doctorId, doctor));
        return ResponseHelper.okay(doctorDto, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<DoctorDto>> searchDoctors(
            @RequestParam(name = "firstname", required = false) String firstname,
            @RequestParam(name = "lastname", required = false) String lastname) {
        List<DoctorDto> doctors = userService.searchUsers(firstname, lastname, Role.DOCTOR)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseHelper.okay(doctors, HttpStatus.OK);
    }

    @PatchMapping("/{doctorId}/uploadProfilePicture")
    @PreAuthorize("(hasRole('DOCTOR') and #doctorId == authentication.principal.getId()) or hasRole('ADMIN')")
    public ResponseEntity<Void> uploadProfilePicture(@PathVariable("doctorId") Long doctorId,
                                                     @RequestParam("file") MultipartFile file) {

        try {
            userService.uploadProfilePicture(doctorId, file);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok().build();
    }

    private DoctorDto convertToDto(User doctor) {
        return modelMapper.map(doctor, DoctorDto.class);
    }
}
