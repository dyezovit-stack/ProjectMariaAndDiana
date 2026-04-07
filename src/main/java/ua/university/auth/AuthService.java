package ua.university.auth;

import java.util.*;

public class AuthService {
    public static final int READ = 0b0001;
    public static final int WRITE = 0b0010;
    public static final int DELETE = 0b0100;
    public static final int MANAGE = 0b1000;

    private final Map<String, User> users = new HashMap<>();
    private User currentUser = null;

    public AuthService() {
        register("admin", "admin123", Role.ADMIN);
        register("manager", "pupupu", Role.MANAGER);
        register("student", "login", Role.USER);
    }

    public void register(String username, String password, Role role) {
        users.put(username, new User(username, password, role));
    }

    public boolean login(String username, String password) {
        String hash = Integer.toHexString(password.hashCode());
        User user = users.get(username);
        if (user != null && user.getPasswordHash().equals(hash)) {
            currentUser = user;
            return true;
        }
        return false;
    }

    public boolean can(int permission) {
        return currentUser != null && (currentUser.getPermissions() & permission) == permission;
    }

    public Optional<User> getCurrentUser() { return Optional.ofNullable(currentUser); }
    public boolean isLoggedIn() { return currentUser != null; }
    public List<User> getAllUsers() { return new ArrayList<>(users.values()); }
}