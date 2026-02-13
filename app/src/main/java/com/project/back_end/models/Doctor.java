package com.project.back_end.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@Entity
@Table(name = "doctors")
public class Doctor {

    // Primary Key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Doctor Name
    @NotNull
    @Size(min = 3, max = 100)
    private String name;

    // Specialty
    @NotNull
    @Size(min = 3, max = 50)
    private String specialty;

    // Email
    @NotNull
    @Email
    private String email;

    // Password (write-only for security)
    @NotNull
    @Size(min = 6)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    // Phone Number
    @NotNull
    @Pattern(regexp = "^[0-9]{10}$")
    private String phone;

    // ✅ Available Time Slots (FIXED)
    @ElementCollection
    @CollectionTable(
        name = "doctor_available_times",
        joinColumns = @JoinColumn(name = "doctor_id")
    )
    @Column(name = "available_time")
    private List<String> availableTimes;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<String> getAvailableTimes() {
        return availableTimes;
    }

    public void setAvailableTimes(List<String> availableTimes) {
        this.availableTimes = availableTimes;
    }
}


