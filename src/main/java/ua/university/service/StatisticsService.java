package ua.university.service;

import ua.university.domain.Student;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatisticsService {
    // Звіт 1: Кількість студентів по курсах
    public Map<Integer, Long> getCountByCourse(List<Student> students) {
        return students.stream()
                .collect(Collectors.groupingBy(Student::getCourse, Collectors.counting()));
    }

    // Звіт 2: Пошук відмінників (приклад фільтрації)
    public List<Student> getTopStudents(List<Student> students) {
        return students.stream()
                .filter(s -> s.getCourse() > 1) // Наприклад, старшокурсники
                .limit(5)
                .collect(Collectors.toList());
    }
}