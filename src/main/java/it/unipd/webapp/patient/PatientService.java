package it.unipd.webapp.patient;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    @Autowired
    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<Patient> getPatients() {
        return patientRepository.findAll();
    }

    public Patient addNewPatient(Patient patient, MultipartFile avatar) throws IOException {
        Optional<Patient> patientOptional = patientRepository.findPatientByEmail(patient.getEmail());
        if (patientOptional.isPresent()) {
            throw new IllegalStateException("email taken before!");
        }
        if (patient.getPassword() == null) throw new IllegalStateException("password cannot be null!");
        String bcryptHashString = BCrypt.withDefaults().hashToString(10, patient.getPassword().toCharArray());
        patient.setPassword(bcryptHashString);
        String filename = saveFile(avatar);
        // Update patient's profile picture filename
        patient.setProfilePicture(filename);
        System.out.println(patient);
        return patientRepository.save(patient);
    }

    public void deletePatient(Long patientId) {
        boolean exists = patientRepository.existsById(patientId);
        if (!exists) {
            throw new IllegalStateException("patient with indicated id doesn't exists");
        }
        patientRepository.deleteById(patientId);
        //todo remove files related to this patient
    }

    @Transactional
    public Patient patchPatient(Long patientId, Patient patientToUpdate) {
        //todo let it to update the file here
        Patient patient = patientRepository.findById(patientId)
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
            Optional<Patient> patientOptional = patientRepository.findPatientByEmail(patientToUpdate.getEmail());
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


        return patientRepository.save(patient);
    }

    public Patient getPatientsById(Long patientId) throws IOException {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalStateException("Patient not found - " + patientId));
        if (patient.getProfilePicture() != null){
            File directory = new File("uploads");
            String base64Img = encodeFileToBase64(directory.getAbsolutePath() + File.separator + patient.getProfilePicture());
            patient.setAvatar(base64Img);
        }
        return patient;
    }

    public List<Patient> searchPatients(String firstname, String lastname) {
        System.out.println("Searching for patients with firstname: " + firstname + ", lastname: " + lastname);
        return patientRepository.findByFirstnameContainingIgnoreCaseOrLastnameContainingIgnoreCase(firstname, lastname);
    }

    public void uploadProfilePicture(Long patientId, MultipartFile file) throws IOException {
        //todo remove avatar  (previous one if exists)
        Optional<Patient> optionalPatient = patientRepository.findById(patientId);
        if (!optionalPatient.isPresent()) {
            throw new IllegalArgumentException("Patient not found - " + patientId);
        }

        Patient patient = optionalPatient.get();

        String filename = saveFile(file);

        // Update patient's profile picture filename
        patient.setProfilePicture(filename);
        patientRepository.save(patient);
    }

    private String saveFile(MultipartFile file) throws IOException {
        // Save file to disk with randomized filename
        String filename = UUID.randomUUID().toString() + "." + getFileExtension(file.getOriginalFilename());
        File directory = new File("uploads");
        if (!directory.exists()) {
            directory.mkdir();
        }
        Path path = Paths.get(directory.getAbsolutePath() + File.separator + filename);
        try {
            Files.write(path, file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Failed to save file!");
        }
        return filename;
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    public static String encodeFileToBase64(String filePath) throws IOException {
        File file = new File(filePath);
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        fis.read(buffer);
        fis.close();
        return Base64.getEncoder().encodeToString(buffer);
    }

}
