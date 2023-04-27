package it.unipd.webapp.patient;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    public void addNewPatient(Patient patient) {
        Optional<Patient> patientOptional = patientRepository.findPatientByEmail(patient.getEmail());
        if (patientOptional.isPresent()) {
            throw new IllegalStateException("email taken before!");
        }
        patientRepository.save(patient);
        System.out.println(patient);
    }

    public void deletePatient(Long patientId) {
        boolean exists = patientRepository.existsById(patientId);
        if (!exists) {
            throw new IllegalStateException("patient with indicated id doesn't exists");
        }
        patientRepository.deleteById(patientId);
    }

    @Transactional
    public void patchPatient(Long patientId, String firstname, String email) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalStateException("Patient not found - " + patientId));
        if (firstname != null && firstname.length() > 0 && !Objects.equals(patient.getFirstname(), firstname)) {
            patient.setFirstname(firstname);
        }

        if (email != null && email.length() > 0 && !Objects.equals(patient.getEmail(), email)) {
            Optional<Patient> patientOptional = patientRepository.findPatientByEmail(email);
            if (patientOptional.isPresent()) {
                throw new IllegalStateException("email taken before!");
            }
            patient.setEmail(email);
        }
    }
}
