package com.project.back_end.controllers;

import com.project.back_end.models.Doctor;
import com.project.back_end.models.Login;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.path}" + "doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private TokenService service;

    /* -------------------------------------------------
       1. GET DOCTOR AVAILABILITY
     ------------------------------------------------- */
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public Map<String, Object> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable Long doctorId,
            @PathVariable String date,
            @PathVariable String token
    ) {

        Map<String, Object> response = new HashMap<>();

        if (!service.validateToken(token, user)) {
            response.put("error", "Invalid token");
            return response;
        }

        List<String> availability =
                doctorService.getDoctorAvailability(doctorId, LocalDate.parse(date));

        response.put("availability", availability);
        return response;
    }

    /* -------------------------------------------------
       2. GET ALL DOCTORS
     ------------------------------------------------- */
    @GetMapping
    public Map<String, Object> getDoctors() {

        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctorService.getDoctors());
        return response;
    }

    /* -------------------------------------------------
       3. ADD NEW DOCTOR (ADMIN ONLY)
     ------------------------------------------------- */
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> addDoctor(
            @RequestBody Doctor doctor,
            @PathVariable String token
    ) {

        Map<String, String> response = new HashMap<>();

        if (!service.validateToken(token, "admin")) {
            response.put("message", "Unauthorized");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        int result = doctorService.saveDoctor(doctor);

        if (result == 1) {
            response.put("message", "Doctor added to db");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else if (result == -1) {
            response.put("message", "Doctor already exists");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        } else {
            response.put("message", "Some internal error occurred");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /* -------------------------------------------------
       4. DOCTOR LOGIN
     ------------------------------------------------- */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin(
            @RequestBody Login login
    ) {
        return doctorService.validateDoctor(login);
    }

    /* -------------------------------------------------
       5. UPDATE DOCTOR DETAILS
     ------------------------------------------------- */
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateDoctor(
            @RequestBody Doctor doctor,
            @PathVariable String token
    ) {

        Map<String, String> response = new HashMap<>();

        if (!service.validateToken(token, "admin")) {
            response.put("message", "Unauthorized");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        int result = doctorService.updateDoctor(doctor);

        if (result == 1) {
            response.put("message", "Doctor updated");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else if (result == -1) {
            response.put("message", "Doctor not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } else {
            response.put("message", "Some internal error occurred");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /* -------------------------------------------------
       6. DELETE DOCTOR
     ------------------------------------------------- */
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> deleteDoctor(
            @PathVariable long id,
            @PathVariable String token
    ) {

        Map<String, String> response = new HashMap<>();

        if (!service.validateToken(token, "admin")) {
            response.put("message", "Unauthorized");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        int result = doctorService.deleteDoctor(id);

        if (result == 1) {
            response.put("message", "Doctor deleted successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else if (result == -1) {
            response.put("message", "Doctor not found with id");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } else {
            response.put("message", "Some internal error occurred");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /* -------------------------------------------------
       7. FILTER DOCTORS
     ------------------------------------------------- */
    @GetMapping("/filter/{name}/{time}/{speciality}")
    public Map<String, Object> filterDoctors(
            @PathVariable String name,
            @PathVariable String time,
            @PathVariable String speciality
    ) {
        return doctorService
                .filterDoctorsByNameSpecilityandTime(name, speciality, time);
    }
}
