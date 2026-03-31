package ua.university.domain;

import java.util.ArrayList;
import java.util.List;

public class Faculty {
    private String code;
    private String name;
    private Teacher dean;
    private List<Department> departments = new ArrayList<>();

    public Faculty(String code, String name, Teacher dean) {
        this.code = code;
        this.name = name;
        this.dean = dean;
    }

    // Геттери і методи для додавання кафедр
    public String getName() { return name; }
}