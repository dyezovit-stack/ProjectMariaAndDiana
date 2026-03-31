package ua.university.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Чернетка авторизації: вхід + роль користувач/менеджер для демонстрації доступу.
 */
public class AuthService {
    private final Map<String, User> users = new HashMap<>();
    private User currentUser = null;

    public AuthService() {
        // Default users for demo
        register("student", "login", Role.USER);
        register("teacher", "P@ssw0rd", Role.TEACHER);
        register("manager", "pupupu", Role.MANAGER);
    }

    public void register(String username, String password, Role role) {
        users.put(username, new User(username, password, role));
    }

    public boolean login(String username, String password) {
        Optional<User> found = Optional.ofNullable(users.get(username));
        return found.filter(u -> u.getPasswordHash().equals(Integer.toHexString(password.hashCode())))
                .map(u -> { currentUser = u; return true; })
                .orElse(false);
    }

    public void logout() {
        currentUser = null;
    }

    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean hasRole(Role role) {
        return isLoggedIn() && currentUser.getRole() == role;
    }

    public boolean isManager() {
        return hasRole(Role.MANAGER);
    }
}
