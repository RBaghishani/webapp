package it.unipd.webapp.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import it.unipd.webapp.entity.User;
import it.unipd.webapp.enums.Role;
import it.unipd.webapp.model.AuthenticationResponse;
import it.unipd.webapp.model.RegisterRequest;
import it.unipd.webapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static it.unipd.webapp.service.Utils.encodeFileToBase64;
import static it.unipd.webapp.service.Utils.saveFile;

@Service
public class PatientService {

    private final UserRepository userRepository;

    private final AuthenticationService authenticationService;

    @Autowired
    public PatientService(UserRepository userRepository, AuthenticationService authenticationService) {
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
    }

    public List<User> getPatients() {
        return userRepository.findByRole(Role.PATIENT);
    }

    public AuthenticationResponse addNewPatient(RegisterRequest request) throws IOException {
        request.setRole(Role.PATIENT);
        return authenticationService.register(request);
    }

    public void deletePatient(Long patientId) {
        /*Optional<User> userOptional = userRepository.findUserById(patientId)
                .orElseThrow(() -> new IllegalStateException("Patient not found - " + patientId));*/
        boolean exists = userRepository.existsById(patientId);
        if (!exists) {
            throw new IllegalStateException("patient with indicated id doesn't exists");
        }
        userRepository.deleteById(patientId);
        //todo remove files related to this patient
    }

    @Transactional
    public User patchPatient(Long patientId, User patientToUpdate) {
        //todo let it to update the file here
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new IllegalStateException("Patient not found - " + patientId));


        if (patientToUpdate.getFirstname() != null && patientToUpdate.getFirstname().trim().length() > 0 &&
                !Objects.equals(patient.getFirstname(), patientToUpdate.getFirstname())) {
            patient.setFirstname(patientToUpdate.getFirstname());
        }

        if (patientToUpdate.getLastname() != null && patientToUpdate.getLastname().trim().length() > 0 &&
                !Objects.equals(patient.getLastname(), patientToUpdate.getLastname())) {
            patient.setLastname(patientToUpdate.getLastname());
        }

        if (patientToUpdate.getGender() != null && !Objects.equals(patient.getGender().toString(), patientToUpdate.getGender().toString())) {
            patient.setGender(patientToUpdate.getGender());
        }

        if (patientToUpdate.getPhoneNumber() != null && patientToUpdate.getPhoneNumber().trim().length() > 0 &&
                !Objects.equals(patient.getPhoneNumber(), patientToUpdate.getPhoneNumber())) {
            patient.setPhoneNumber(patientToUpdate.getPhoneNumber());
        }

        if (patientToUpdate.getAddress() != null && patientToUpdate.getAddress().trim().length() > 0 &&
                !Objects.equals(patient.getAddress(), patientToUpdate.getAddress())) {
            patient.setAddress(patientToUpdate.getAddress());
        }

        if (patientToUpdate.getDob() != null && !Objects.equals(patient.getDob(), patientToUpdate.getDob())) {
            patient.setDob(patientToUpdate.getDob());
        }

        if (patientToUpdate.getEmail() != null && patientToUpdate.getEmail().trim().length() > 0 && !Objects.equals(patient.getEmail(), patientToUpdate.getEmail())) {
            Optional<User> patientOptional = userRepository.findByEmail(patientToUpdate.getEmail());
            if (patientOptional.isPresent()) {
                throw new IllegalStateException("email taken before!");
            }
            patient.setEmail(patientToUpdate.getEmail());
        }

        if (patientToUpdate.getPassword() != null && patientToUpdate.getPassword().trim().length() > 0 &&
                !BCrypt.verifyer().verify(patientToUpdate.getPassword().toCharArray(), patient.getPassword()).verified ) {
            String bcryptHashString = BCrypt.withDefaults().hashToString(10, patientToUpdate.getPassword().toCharArray());
            patient.setPassword(bcryptHashString);
        }


        return userRepository.save(patient);
    }

    public User getPatientsById(Long patientId) throws IOException {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new IllegalStateException("Patient not found - " + patientId));
        if (patient.getProfilePicture() != null){
            File directory = new File("uploads");
            String base64Img = encodeFileToBase64(directory.getAbsolutePath() + File.separator + patient.getProfilePicture());
            patient.setAvatar(base64Img);
        }
        return patient;
    }

    public List<User> searchPatients(String firstname, String lastname) {
        System.out.println("Searching for patients with firstname: " + firstname + ", lastname: " + lastname);
        return userRepository.findByFirstnameContainingIgnoreCaseOrLastnameContainingIgnoreCase(firstname, lastname);
    }

    public void uploadProfilePicture(Long patientId, MultipartFile file) throws IOException {
        //todo remove avatar  (previous one if exists)
        Optional<User> optionalPatient = userRepository.findById(patientId);
        if (!optionalPatient.isPresent()) {
            throw new IllegalArgumentException("Patient not found - " + patientId);
        }

        User patient = optionalPatient.get();

        String filename = saveFile(file);

        // Update patient's profile picture filename
        patient.setProfilePicture(filename);
        userRepository.save(patient);
    }

}
