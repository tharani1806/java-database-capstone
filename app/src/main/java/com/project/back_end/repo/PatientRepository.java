package com.project.back_end.repo;

import com.project.back_end.models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on Patient entities.
 * Extends JpaRepository to inherit standard data access methods.
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Finds a patient by their email address.
     *
     * @param email the email of the patient
     * @return an Optional containing the patient if found, otherwise empty
     */
    Optional<Patient> findByEmail(String email);

    /**
     * Finds a patient by either their email address or phone number.
     * Useful when checking for duplicates during registration or validation.
     *
     * @param email the email of the patient
     * @param phone the phone number of the patient
     * @return an Optional containing the patient if found, otherwise empty
     */
    Optional<Patient> findByEmailOrPhone(String email, String phone);
}
