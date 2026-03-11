package ua.university.auth;

public class User {
    private final String username;
    private final String passwordHash; // stored as hash (simple demo)
    private final Role role;

    public User(String username, String password, Role role) {
        this.username = username;
        this.passwordHash = Integer.toHexString(password.hashCode());
        this.role = role;
    }

    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public Role getRole() { return role; }

    @Override
    public String toString() {
        return String.format("User[%s, role=%s]", username, role);
    }
}
