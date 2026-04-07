package ua.university.auth;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String username;
    private final String passwordHash;
    private final Role role;
    private final int permissions;

    public User(String username, String password, Role role) {
        this.username = username;
        this.passwordHash = Integer.toHexString(password.hashCode());
        this.role = role;
        // Призначення масок: 1-Read, 2-Write, 4-Delete, 8-Manage
        this.permissions = switch (role) {
            case ADMIN -> 0b1111;   // 15
            case MANAGER -> 0b0111; // 7
            case TEACHER -> 0b0011; // 3
            case USER -> 0b0001;    // 1
        };
    }

    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public Role getRole() { return role; }
    public int getPermissions() { return permissions; }
}