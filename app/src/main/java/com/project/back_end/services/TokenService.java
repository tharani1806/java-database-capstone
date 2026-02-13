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

/**
 * Service class for generating, extracting, and validating JWT tokens for users.
 * Supports Admin, Doctor, and Patient entities.
 */
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

    // -------------------- GENERATE TOKEN --------------------

    /**
     * Generates a JWT token for the given user identifier.
     * The identifier should be:
     * - Admin: username
     * - Doctor: email
     * - Patient: email
     *
     * @param identifier unique identifier of the user
     * @return JWT token as a String
     */
    public String generateToken(String identifier) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 7L * 24 * 60 * 60 * 1000); // 7 days validity

        return Jwts.builder()
                .setSubject(identifier)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    // -------------------- EXTRACT IDENTIFIER --------------------

    /**
     * Extracts the identifier (subject) from the JWT token.
     *
     * @param token JWT token
     * @return the identifier (email/username) stored in the token
     * @throws io.jsonwebtoken.JwtException if token is invalid or expired
     */
    public String extractIdentifier(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // -------------------- VALIDATE TOKEN --------------------

    /**
     * Validates the JWT token for a specific user type.
     *
     * @param token JWT token to validate
     * @param userType type of user: "admin", "doctor", or "patient"
     * @return true if token is valid and user exists, false otherwise
     */
    public boolean validateToken(String token, String userType) {
        try {
            String identifier = extractIdentifier(token);

            return switch (userType.toLowerCase()) {
                case "admin" -> adminRepository.findByUsername(identifier) != null;
                case "doctor" -> doctorRepository.findByEmail(identifier) != null;
                case "patient" -> patientRepository.findByEmail(identifier) != null;
                default -> false;
            };
        } catch (Exception e) {
            // Could log e.getMessage() for debugging
            return false;
        }
    }

    // -------------------- SIGNING KEY --------------------

    /**
     * Returns the secret key used for signing JWT tokens.
     *
     * @return SecretKey
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
}
