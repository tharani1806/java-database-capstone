package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Login;
import com.project.back_end.repositories.AppointmentRepository;
import com.project.back_end.repositories.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TokenService tokenService;

    /* -------------------- GET DOCTOR AVAILABILITY -------------------- */
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {

        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) return Collections.emptyList();

        Doctor doctor = doctorOpt.get();

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        List<Appointment> appointments =
                appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                        doctorId, start, end
                );

        Set<String> bookedSlots = appointments.stream()
                .map(a -> a.getAppointmentTime().toLocalTime().toString())
                .collect(Collectors.toSet());

        return doctor.getAvailableTimes().stream()
                .filter(slot -> !bookedSlots.contains(slot))
                .collect(Collectors.toList());
    }

    /* -------------------- SAVE DOCTOR -------------------- */
    public int saveDoctor(Doctor doctor) {
        try {
            if (doctorRepository.findByEmail(doctor.getEmail()) != null) {
                return -1;
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /* -------------------- UPDATE DOCTOR -------------------- */
    public int updateDoctor(Doctor doctor) {
        try {
            if (!doctorRepository.existsById(doctor.getId())) {
                return -1;
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /* -------------------- GET ALL DOCTORS -------------------- */
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    /* -------------------- DELETE DOCTOR -------------------- */
    public int deleteDoctor(long id) {
        try {
            if (!doctorRepository.existsById(id)) {
                return -1;
            }
            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /* -------------------- VALIDATE DOCTOR LOGIN -------------------- */
    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {

        Map<String, String> response = new HashMap<>();
        Doctor doctor = doctorRepository.findByEmail(login.getEmail());

        if (doctor == null || !doctor.getPassword().equals(login.getPassword())) {
            response.put("message", "Invalid credentials");
            return ResponseEntity.status(401).body(response);
        }

        String token = tokenService.generateToken(doctor.getId(), "doctor");
        response.put("token", token);
        response.put("role", "doctor");
        return ResponseEntity.ok(response);
    }

    /* -------------------- FIND DOCTOR BY NAME -------------------- */
    public Map<String, Object> findDoctorByName(String name) {
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctorRepository.findByNameLike("%" + name + "%"));
        return response;
    }

    /* -------------------- FILTER: NAME + SPECIALTY + TIME -------------------- */
    public Map<String, Object> filterDoctorsByNameSpecilityandTime(
            String name, String specialty, String amOrPm) {

        List<Doctor> doctors =
                doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(
                        name, specialty
                );

        Map<String, Object> response = new HashMap<>();
        response.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return response;
    }

    /* -------------------- FILTER: NAME + TIME -------------------- */
    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {

        List<Doctor> doctors =
                doctorRepository.findByNameContainingIgnoreCase(name);

        Map<String, Object> response = new HashMap<>();
        response.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return response;
    }

    /* -------------------- FILTER: NAME + SPECIALTY -------------------- */
    public Map<String, Object> filterDoctorByNameAndSpecility(String name, String specilty) {
        Map<String, Object> response = new HashMap<>();
        response.put("doctors",
                doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(
                        name, specilty
                ));
        return response;
    }

    /* -------------------- FILTER: SPECIALTY + TIME -------------------- */
    public Map<String, Object> filterDoctorByTimeAndSpecility(String specilty, String amOrPm) {

        List<Doctor> doctors =
                doctorRepository.findBySpecialtyIgnoreCase(specilty);

        Map<String, Object> response = new HashMap<>();
        response.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return response;
    }

    /* -------------------- FILTER: SPECIALTY -------------------- */
    public Map<String, Object> filterDoctorBySpecility(String specilty) {
        Map<String, Object> response = new HashMap<>();
        response.put("doctors",
                doctorRepository.findBySpecialtyIgnoreCase(specilty));
        return response;
    }

    /* -------------------- FILTER: TIME -------------------- */
    public Map<String, Object> filterDoctorsByTime(String amOrPm) {

        List<Doctor> doctors = doctorRepository.findAll();

        Map<String, Object> response = new HashMap<>();
        response.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return response;
    }

    /* -------------------- PRIVATE TIME FILTER -------------------- */
    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {

        return doctors.stream()
                .filter(d ->
                        d.getAvailableTimes().stream().anyMatch(time -> {
                            int hour = Integer.parseInt(time.split(":")[0]);
                            return amOrPm.equalsIgnoreCase("AM")
                                    ? hour < 12
                                    : hour >= 12;
                        })
                )
                .collect(Collectors.toList());
    }
}

