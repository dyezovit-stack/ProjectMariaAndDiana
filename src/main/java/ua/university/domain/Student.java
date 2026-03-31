package ua.university.domain;

import ua.university.exception.InvalidCourseException;

import java.time.LocalDate;
import java.time.Period;

public class Student extends Person {
    private int course;
    private String group;
    private String studentIdCard;
    private LocalDate enrollmentDate;

    public Student(String fullName, LocalDate birthDate, String email, String phone,
                   int course, String group, String studentIdCard) {
        super(fullName, birthDate, email, phone);
        if (course < 1 || course > 6) {
            throw new InvalidCourseException("Курс має бути від 1 до 6, отримано: " + course);
        }
        this.course = course;
        this.group = group;
        this.studentIdCard = studentIdCard;
        this.enrollmentDate = LocalDate.now();
    }

    public int getAge() {
        return Period.between(getBirthDate(), LocalDate.now()).getYears();
    }

    public long getYearsEnrolled() {
        return Period.between(enrollmentDate, LocalDate.now()).getYears();
    }

    public int getCourse() { return course; }
    public void setCourse(int course) {
        if (course < 1 || course > 6) throw new InvalidCourseException("Курс має бути від 1 до 6");
        this.course = course;
    }
    public String getGroup() { return group; }
    public void setGroup(String group) { this.group = group; }
    public String getStudentIdCard() { return studentIdCard; }
    public LocalDate getEnrollmentDate() { return enrollmentDate; }

    @Override
    public String toString() {
        return String.format("Студент: %s | Курс: %d | Група: %s | ID: %s | Вік: %d",
                getFullName(), course, group, studentIdCard, getAge());
    }
}
