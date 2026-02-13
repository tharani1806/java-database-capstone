package com.project.back_end.services;

import com.project.back_end.models.Admin;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repositories.AdminRepository;
import com.project.back_end.repositories.DoctorRepository;
import com.project.back_end.repositories.PatientRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class TokenService {

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public TokenService(
            AdminRepository adminRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository
    ) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    /* -------------------- GENERATE TOKEN -------------------- */
    public String generateToken(String identifier) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + (7L * 24 * 60 * 60 * 1000)); // 7 days

        return Jwts.builder()
                .setSubject(identifier)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /* -------------------- EXTRACT IDENTIFIER -------------------- */
    public String extractIdentifier(String token) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /* -------------------- VALIDATE TOKEN -------------------- */
    public boolean validateToken(String token, String user) {

        try {
            String identifier = extractIdentifier(token);

            switch (user.toLowerCase()) {
                case "admin":
                    Admin admin = adminRepository.findByUsername(identifier);
                    return admin != null;

                case "doctor":
                    Doctor doctor = doctorRepository.findByEmail(identifier);
                    return doctor != null;

                case "patient":
                    Patient patient = patientRepository.findByEmail(identifier);
                    return patient != null;

                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /* -------------------- SIGNING KEY -------------------- */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
}
