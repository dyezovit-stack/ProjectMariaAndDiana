package ua.university;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ua.university.domain.Student;
import ua.university.exception.DuplicateStudentException;
import ua.university.exception.InvalidCourseException;
import ua.university.exception.StudentNotFoundException;
import ua.university.repository.StudentRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class StudentRepositoryTest {

    private StudentRepository repo;
    private Student alice;
    private Student bob;

    @BeforeEach
    void setUp() {
        repo = new StudentRepository();

        alice = new Student("Alice Kovalenko", LocalDate.of(2002, 3, 15),
                "alice@uni.ua", "111", 2, "КН-21", "BK-001");

        bob = new Student("Bob Petrenko", LocalDate.of(2001, 7, 20),
                "bob@uni.ua", "222", 3, "КН-31", "BK-002");

        repo.save(alice);
        repo.save(bob);
    }

    @Test
    @DisplayName("Має повертати всіх збережених студентів")
    void testSaveAndFindAll() {
        List<Student> all = repo.findAll();
        assertEquals(2, all.size(), "Кількість студентів має бути 2");
    }

    @Test
    @DisplayName("Пошук за ім'ям має ігнорувати регістр (якщо реалізовано)")
    void testFindByNameLambda() {
        List<Student> result = repo.findByName("Alice");
        assertFalse(result.isEmpty(), "Студент Alice має бути знайдений");
        assertEquals("Alice Kovalenko", result.get(0).getFullName());
    }

    @Test
    @DisplayName("Фільтрація за курсом через Lambda")
    void testFindByCourseWithLambda() {
        List<Student> course2 = repo.findByCourse(2);
        assertEquals(1, course2.size());
        assertEquals("Alice Kovalenko", course2.get(0).getFullName());
    }

    @Test
    @DisplayName("Сортування за ім'ям")
    void testSortedByNameLambda() {
        List<Student> sorted = repo.findAllSortedByName();
        assertEquals("Alice Kovalenko", sorted.get(0).getFullName(), "Першою має бути Alice");
        assertEquals("Bob Petrenko", sorted.get(1).getFullName(), "Другим має бути Bob");
    }

    @Test
    @DisplayName("Пошук через Optional за ID")
    void testOptionalFindById() {
        Optional<Student> found = repo.findById("BK-001");
        assertTrue(found.isPresent(), "Студент з ID BK-001 має існувати");
        assertEquals("Alice Kovalenko", found.get().getFullName());
    }

    @Test
    void testOptionalNotFound() {
        Optional<Student> notFound = repo.findById("nonexistent-id");
        assertTrue(notFound.isEmpty(), "Для неіснуючого ID Optional має бути порожнім");
    }

    @Test
    @DisplayName("Викидання винятку, якщо студента не знайдено")
    void testGetByIdOrThrowThrowsException() {
        assertThrows(StudentNotFoundException.class,
                () -> repo.getByIdOrThrow("bad-id"),
                "Має бути викинуто StudentNotFoundException");
    }

    @Test
    @DisplayName("Заборона дублікатів за номером квитка (idCard)")
    void testDuplicateStudentIdThrows() {
        Student duplicate = new Student("Copy Alice", LocalDate.of(2002, 1, 1),
                "copy@uni.ua", "333", 1, "КН-11", "BK-001");

        assertThrows(DuplicateStudentException.class, () -> repo.save(duplicate),
                "Репозиторій не повинен дозволяти збереження з однаковим BK-001");
    }

    @Test
    @DisplayName("Валідація курсу (1-6)")
    void testInvalidCourseThrows() {
        assertThrows(InvalidCourseException.class, () ->
                new Student("Bad", LocalDate.of(2000, 1, 1), "b@uni.ua", "0", 7, "X-1", "ERR-1"));
    }

    @Test
    @DisplayName("Групування студентів за курсом (Map)")
    void testGroupByCourseMap() {
        Map<Integer, List<Student>> grouped = repo.groupByCourse();
        assertAll(
                () -> assertTrue(grouped.containsKey(2)),
                () -> assertTrue(grouped.containsKey(3)),
                () -> assertEquals(1, grouped.get(2).size()),
                () -> assertEquals("Alice Kovalenko", grouped.get(2).get(0).getFullName())
        );
    }

    @Test
    @DisplayName("Видалення студента з репозиторію")
    void testDeleteStudent() {
        repo.delete("BK-001");
        assertEquals(1, repo.findAll().size(), "Після видалення має залишитися 1 студент");
        assertTrue(repo.findById("BK-001").isEmpty());
    }

    @Test
    @DisplayName("Розрахунок віку через Java Time API")
    void testJavaTimeApiAge() {
        Student young = new Student("Young One", LocalDate.of(2005, 1, 1),
                "y@uni.ua", "0", 1, "КН-11", "YOUNG-1");
        int age = young.getAge();
        assertTrue(age >= 18 && age < 30, "Вік студента має бути в адекватному діапазоні");
    }
}