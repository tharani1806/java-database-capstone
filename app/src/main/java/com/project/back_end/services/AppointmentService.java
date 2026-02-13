package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repositories.AppointmentRepository;
import com.project.back_end.repositories.DoctorRepository;
import com.project.back_end.repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private TokenService tokenService;

    /* -------------------- BOOK APPOINTMENT -------------------- */
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /* -------------------- UPDATE APPOINTMENT -------------------- */
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {

        Map<String, String> response = new HashMap<>();

        Optional<Appointment> existingOpt =
                appointmentRepository.findById(appointment.getId());

        if (existingOpt.isEmpty()) {
            response.put("message", "Appointment not found");
            return ResponseEntity.badRequest().body(response);
        }

        // Optional validation hook (if implemented elsewhere)
        // service.validateAppointment(appointment);

        appointmentRepository.save(appointment);
        response.put("message", "Appointment updated successfully");
        return ResponseEntity.ok(response);
    }

    /* -------------------- CANCEL APPOINTMENT -------------------- */
    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {

        Map<String, String> response = new HashMap<>();

        Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);

        if (appointmentOpt.isEmpty()) {
            response.put("message", "Appointment not found");
            return ResponseEntity.badRequest().body(response);
        }

        Appointment appointment = appointmentOpt.get();

        Long patientIdFromToken = tokenService.getUserIdFromToken(token);

        if (!appointment.getPatient().getId().equals(patientIdFromToken)) {
            response.put("message", "Unauthorized to cancel this appointment");
            return ResponseEntity.status(403).body(response);
        }

        appointmentRepository.delete(appointment);
        response.put("message", "Appointment cancelled successfully");
        return ResponseEntity.ok(response);
    }

    /* -------------------- GET APPOINTMENTS -------------------- */
    public Map<String, Object> getAppointment(
            String pname,
            LocalDate date,
            String token
    ) {

        Map<String, Object> response = new HashMap<>();

        Long doctorId = tokenService.getUserIdFromToken(token);

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        List<Appointment> appointments =
                appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                        doctorId, start, end
                );

        if (pname != null && !pname.isEmpty()) {
            appointments = appointments.stream()
                    .filter(a -> a.getPatient().getName()
                            .toLowerCase()
                            .contains(pname.toLowerCase()))
                    .collect(Collectors.toList());
        }

        response.put("appointments", appointments);
        return response;
    }
}




