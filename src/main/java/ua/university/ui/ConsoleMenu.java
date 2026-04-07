package ua.university.ui;

import ua.university.auth.AuthService;
import ua.university.auth.User;
import ua.university.domain.Student;
import ua.university.repository.StudentRepository;
import ua.university.repository.DataStorage; // Клас для NIO.2
import ua.university.service.StatisticsService; // Клас для Stream API

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class ConsoleMenu {
    private final StudentRepository studentRepository = new StudentRepository();
    private final AuthService authService = new AuthService();
    private final StatisticsService statsService = new StatisticsService();
    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        System.out.println("=== DigiUni Registry: Final Version 2026 ===");

        // 1. NIO.2: Автоматичне завантаження при старті
        try {
            List<Student> loadedData = DataStorage.load();
            studentRepository.setStudents(loadedData);
            System.out.println("[Система] Дані завантажено успішно.");
        } catch (Exception e) {
            System.out.println("[Система] Файл не знайдено, створена нова база.");
        }

        loginMenu();

        while (true) {
            printMainMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    if (authService.can(AuthService.WRITE)) addStudent();
                    else System.out.println("Помилка: У вас немає прав на ЗАПИС (WRITE).");
                }
                case "2" -> showAllStudents();
                case "3" -> generateReports(); // Stream API звіти
                case "4" -> {
                    if (authService.can(AuthService.DELETE)) deleteStudent();
                    else System.out.println("Помилка: У вас немає прав на ВИДАЛЕННЯ (DELETE).");
                }
                case "5" -> saveToDisk(); // NIO.2 збереження
                case "6" -> {
                    if (authService.can(AuthService.MANAGE)) showAllUsers();
                    else System.out.println("Доступ лише для ADMIN.");
                }
                case "0" -> {
                    System.out.println("Завершення роботи...");
                    return;
                }
                default -> System.out.println("Невірний вибір.");
            }
        }
    }

    private void loginMenu() {
        while (!authService.isLoggedIn()) {
            System.out.println("\n--- Авторизація ---");
            System.out.print("Логін: ");
            String login = scanner.nextLine().trim();
            System.out.print("Пароль: ");
            String pass = scanner.nextLine().trim();

            if (authService.login(login, pass)) {
                authService.getCurrentUser().ifPresent(u ->
                        System.out.println("Вхід успішний! Вітаємо, " + u.getUsername()));
            } else {
                System.out.println("Невірний логін або пароль!");
            }
        }
    }

    private void printMainMenu() {
        User current = authService.getCurrentUser().orElse(null);
        String role = current != null ? current.getRole().name() : "GUEST";

        System.out.println("\n--- Головне меню [" + role + "] ---");
        System.out.println("1. Додати студента");
        System.out.println("2. Показати всіх студентів");
        System.out.println("3. Статистика та звіти (Stream API)");
        if (authService.can(AuthService.DELETE)) System.out.println("4. Видалити студента (DELETE)");
        System.out.println("5. Зберегти дані на диск (NIO.2)");
        if (authService.can(AuthService.MANAGE)) System.out.println("6. Керування користувачами (ADMIN)");
        System.out.println("0. Вихід");
        System.out.print("> ");
    }

    // --- Stream API: Звіти ---
    private void generateReports() {
        List<Student> students = studentRepository.findAll();
        if (students.isEmpty()) {
            System.out.println("Немає даних для аналізу.");
            return;
        }

        System.out.println("\n--- Аналітика системи ---");
        // Звіт 1 через Stream
        long count = students.stream().count();
        System.out.println("Загальна кількість студентів: " + count);

        // Звіт 2: Групування по курсах
        statsService.getCountByCourse(students).forEach((course, n) ->
                System.out.println("Курс " + course + ": " + n + " студентів"));
    }

    // --- NIO.2: Збереження ---
    private void saveToDisk() {
        try {
            DataStorage.save(studentRepository.findAll());
            System.out.println("Дані успішно збережено у файл.");
        } catch (IOException e) {
            System.out.println("Помилка запису: " + e.getMessage());
        }
    }

    private void addStudent() {
        System.out.print("ПІБ: ");
        String name = scanner.nextLine().trim();
        System.out.print("Курс: ");
        int course = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Група: ");
        String group = scanner.nextLine().trim();

        Student s = new Student(name, LocalDate.now(), "email", "000", course, group, "ID" + System.currentTimeMillis());
        studentRepository.save(s);
        System.out.println("Студента додано.");
    }

    private void showAllStudents() {
        studentRepository.findAll().forEach(System.out::println);
    }

    private void deleteStudent() {
        System.out.print("Введіть ID для видалення: ");
        String id = scanner.nextLine().trim();
        studentRepository.delete(id);
        System.out.println("Видалено.");
    }

    private void showAllUsers() {
        System.out.println("\n--- Користувачі системи (Admin View) ---");
        authService.getAllUsers().forEach(u ->
                System.out.println(u.getUsername() + " | Роль: " + u.getRole() + " | Маска: " + Integer.toBinaryString(u.getPermissions())));
    }
}