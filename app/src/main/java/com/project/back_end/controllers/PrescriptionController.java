package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.path}" + "prescription")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private TokenService service;

    /* -------------------------------------------------
       1. SAVE PRESCRIPTION (DOCTOR ONLY)
     ------------------------------------------------- */
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> savePrescription(
            @PathVariable String token,
            @Valid @RequestBody Prescription prescription
    ) {

        Map<String, String> response = new HashMap<>();

        if (!service.validateToken(token, "doctor")) {
            response.put("message", "Invalid or unauthorized token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        prescriptionService.savePrescription(prescription);

        response.put("message", "Prescription saved successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /* -------------------------------------------------
       2. GET PRESCRIPTION BY APPOINTMENT ID
     ------------------------------------------------- */
    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<Map<String, Object>> getPrescription(
            @PathVariable long appointmentId,
            @PathVariable String token
    ) {

        Map<String, Object> response = new HashMap<>();

        if (!service.validateToken(token, "doctor")) {
            response.put("message", "Invalid or unauthorized token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        Prescription prescription =
                prescriptionService.getPrescription(appointmentId);

        if (prescription == null) {
            response.put("message", "No prescription found for this appointment");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        response.put("prescription", prescription);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
