package ua.university.ui;

import ua.university.auth.AuthService;
import ua.university.auth.Role;
import ua.university.domain.Student;
import ua.university.repository.StudentRepository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConsoleMenu {
    private final StudentRepository studentRepository = new StudentRepository();
    private final AuthService authService = new AuthService();
    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        System.out.println("=== DigiUni Registry: Checkpoint 2 ===");
        loginMenu();

        while (true) {
            printMainMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> addStudent();
                case "2" -> showAllStudents();
                case "3" -> searchStudent();
                case "4" -> showSortedByName();
                case "5" -> showByCourse();
                case "6" -> showGroupedByCourse();
                case "7" -> {
                    if (authService.isManager()) deleteStudent();
                    else System.out.println("Доступ заборонено! Потрібна роль MANAGER.");
                }
                case "8" -> showCurrentUser();
                case "0" -> {
                    System.out.println("Вихід з програми...");
                    return;
                }
                default -> System.out.println("Помилка: невірний вибір!");
            }
        }
    }

    private void loginMenu() {
        System.out.println("\nДоступні облікові записи для демо:");
        System.out.println("  user1 / pass1  (роль: USER)");
        System.out.println("  manager1 / pass2  (роль: MANAGER)");

        while (!authService.isLoggedIn()) {
            System.out.print("\nЛогін: ");
            String username = scanner.nextLine().trim();
            System.out.print("Пароль: ");
            String password = scanner.nextLine().trim();

            if (authService.login(username, password)) {
                authService.getCurrentUser().ifPresent(u ->
                    System.out.println("Вхід успішний! " + u));
            } else {
                System.out.println("Невірний логін або пароль. Спробуйте ще раз.");
            }
        }
    }

    private void printMainMenu() {
        String role = authService.getCurrentUser()
                .map(u -> "[" + u.getRole() + "]")
                .orElse("");
        System.out.println("\n--- Головне меню " + role + " ---");
        System.out.println("1. Додати студента");
        System.out.println("2. Показати всіх студентів");
        System.out.println("3. Пошук студента за ПІБ");
        System.out.println("4. Студенти відсортовані за ПІБ (лямбда)");
        System.out.println("5. Пошук за курсом (лямбда)");
        System.out.println("6. Групування по курсах (Map)");
        if (authService.isManager()) System.out.println("7. Видалити студента [MANAGER]");
        System.out.println("8. Мій профіль");
        System.out.println("0. Вихід");
        System.out.print("Виберіть опцію: ");
    }

    private void addStudent() {
        System.out.print("ПІБ: ");
        String name = scanner.nextLine().trim();
        if (name.isBlank()) { System.out.println("Помилка: ПІБ порожнє!"); return; }

        System.out.print("Група: ");
        String group = scanner.nextLine().trim();

        System.out.print("Курс (1-6): ");
        int course;
        try {
            course = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Помилка: невірний формат курсу!"); return;
        }

        System.out.print("Залікова книжка: ");
        String idCard = scanner.nextLine().trim();

        try {
            Student student = new Student(name, LocalDate.of(2000, 1, 1),
                    "email@university.ua", "000", course, group, idCard);
            studentRepository.save(student);
            System.out.println("Студента успішно додано!");
        } catch (Exception e) {
            System.out.println("Помилка: " + e.getMessage());
        }
    }

    private void showAllStudents() {
        List<Student> students = studentRepository.findAll();
        if (students.isEmpty()) { System.out.println("Список порожній."); return; }
        students.forEach(System.out::println);
        System.out.println("Всього: " + students.size());
    }

    private void searchStudent() {
        System.out.print("Введіть ПІБ: ");
        String name = scanner.nextLine().trim();
        // Lambda usage: filter by name
        List<Student> result = studentRepository.findByName(name);
        if (result.isEmpty()) System.out.println("Не знайдено.");
        else result.forEach(System.out::println);
    }

    private void showSortedByName() {
        // Lambda: sort by name
        List<Student> sorted = studentRepository.findAllSortedByName();
        if (sorted.isEmpty()) { System.out.println("Список порожній."); return; }
        System.out.println("--- Відсортовано за ПІБ ---");
        sorted.forEach(System.out::println);
    }

    private void showByCourse() {
        System.out.print("Курс: ");
        try {
            int course = Integer.parseInt(scanner.nextLine().trim());
            // Lambda: filter by course
            List<Student> result = studentRepository.findByCourse(course);
            if (result.isEmpty()) System.out.println("Студентів на " + course + " курсі не знайдено.");
            else result.forEach(System.out::println);
        } catch (NumberFormatException e) {
            System.out.println("Невірний формат.");
        }
    }

    private void showGroupedByCourse() {
        // Map: group by course
        Map<Integer, List<Student>> grouped = studentRepository.groupByCourse();
        if (grouped.isEmpty()) { System.out.println("Список порожній."); return; }
        grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> {
                    System.out.println("\n  Курс " + e.getKey() + ":");
                    e.getValue().forEach(s -> System.out.println("    " + s));
                });
    }

    private void deleteStudent() {
        System.out.print("Введіть ID студента для видалення: ");
        String id = scanner.nextLine().trim();
        try {
            studentRepository.delete(id);
            System.out.println("Студента видалено.");
        } catch (Exception e) {
            System.out.println("Помилка: " + e.getMessage());
        }
    }

    private void showCurrentUser() {
        authService.getCurrentUser().ifPresent(u ->
            System.out.println("Поточний користувач: " + u));
    }
}
