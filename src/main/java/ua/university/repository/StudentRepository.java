package ua.university.repository;

import ua.university.domain.Student;
import ua.university.exception.DuplicateStudentException;
import ua.university.exception.StudentNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

public class StudentRepository {
    private final List<Student> students = new ArrayList<>();
    private final Set<String> studentIds = new HashSet<>();
    private final Map<String, Student> studentById = new HashMap<>();

    public void save(Student student) {
        if (studentIds.contains(student.getStudentIdCard())) {
            throw new DuplicateStudentException(
                "Студент із залікової книжкою " + student.getStudentIdCard() + " вже існує!");
        }
        students.add(student);
        studentIds.add(student.getStudentIdCard());
        studentById.put(student.getId(), student);
    }

    public List<Student> findAll() {
        return new ArrayList<>(students);
    }

    public Optional<Student> findById(String id) {
        return Optional.ofNullable(studentById.get(id));
    }

    public List<Student> findByName(String name) {
        return students.stream()
                .filter(s -> s.getFullName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Student> findByCourse(int course) {
        return students.stream()
                .filter(s -> s.getCourse() == course)
                .collect(Collectors.toList());
    }

    public List<Student> findAllSortedByName() {
        return students.stream()
                .sorted(Comparator.comparing(Student::getFullName))
                .collect(Collectors.toList());
    }

    public List<Student> findAllSortedByCourse() {
        return students.stream()
                .sorted(Comparator.comparingInt(Student::getCourse))
                .collect(Collectors.toList());
    }

    public Student getByIdOrThrow(String id) {
        return findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Студента з ID " + id + " не знайдено!"));
    }

    public Map<Integer, List<Student>> groupByCourse() {
        return students.stream()
                .collect(Collectors.groupingBy(Student::getCourse));
    }

    public int count() {
        return students.size();
    }

    public void delete(String id) {
        Student student = getByIdOrThrow(id);
        students.remove(student);
        studentIds.remove(student.getStudentIdCard());
        studentById.remove(id);
    }
}
