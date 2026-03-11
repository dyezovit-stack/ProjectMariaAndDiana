package ua.university.domain;

import java.time.LocalDate;
import java.util.UUID;

public abstract class Person {
    private final String id;
    private String fullName;
    private LocalDate birthDate;
    private String email;
    private String phone;

    public Person(String fullName, LocalDate birthDate, String email, String phone) {
        this.id = UUID.randomUUID().toString();
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.email = email;
        this.phone = phone;
    }

    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public LocalDate getBirthDate() { return birthDate; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
}
