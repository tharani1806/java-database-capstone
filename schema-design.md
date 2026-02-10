## MySQL Database Design


### Table: patients
-id: INT, Primary Key, AUTO_INCREMENT
-name: VARCHAR(100), NOT NULL
-email: VARCHAR(100), NOT NULL, UNIQUE
-password: VARCHAR(255), NOT NULL
-phone: VARCHAR(15), NOT NULL

### Table: appointments
- id: INT, Primary Key, Auto Increment
- doctor_id: INT, Foreign Key → doctors(id)
- patient_id: INT, Foreign Key → patients(id)
- appointment_time: DATETIME, Not Null
- status: INT (0 = Scheduled, 1 = Completed, 2 = Cancelled)

### Table: doctors
-id: INT, Primary Key, AUTO_INCREMENT
-name: VARCHAR(100), NOT NULL
-specialization: VARCHAR(100), NOT NULL
-email: VARCHAR(100), NOT NULL, UNIQUE
-phone: VARCHAR(15), NOT NULL
-is_active: BOOLEAN, DEFAULT TRUE

### Table: admin
-id: INT, Primary Key, AUTO_INCREMENT
-username: VARCHAR(50), NOT NULL, UNIQUE
-password: VARCHAR(255), NOT NULL

### Table: clinic_locations 
-id: INT, Primary Key, AUTO_INCREMENT
-name: VARCHAR(100), NOT NULL
-address: TEXT, NOT NULL
-phone: VARCHAR(15), NOT NULL

### Table: payments 
-id: INT, Primary Key, AUTO_INCREMENT
-appointment_id: INT, Foreign Key → appointments(id)
-amount: DECIMAL(10,2), NOT NULL
-payment_status: VARCHAR(20), NOT NULL
-payment_date: TIMESTAMP, DEFAULT CURRENT_TIMESTAMP



## MongoDB Collection Design


### Collection: prescriptions

```json
{
  "_id": "ObjectId('64abc123456')",
  "patientName": "John Smith",
  "appointmentId": 51,
  "medication": "Paracetamol",
  "dosage": "500mg",
  "doctorNotes": "Take 1 tablet every 6 hours.",
  "refillCount": 2,
  "pharmacy": {
    "name": "Walgreens SF",
    "location": "Market Street"
  }
} 






