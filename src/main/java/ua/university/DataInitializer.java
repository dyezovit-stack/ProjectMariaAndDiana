package ua.university;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.nio.file.Paths;

public class DataInitializer {
    public static void loadInitialData() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            // Зчитування з файлу у вашу модель даних
            UniversityRegistry registry = mapper.readValue(new File("data.json"), UniversityRegistry.class);
            System.out.println("Дані успішно завантажено!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
