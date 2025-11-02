import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Manages all user-related operations: registration, login, secure password hashing,
 * and exporting user credentials to a JSON file for the front-end.
 */
public class UserManager {
    private Map<String, String> users = new HashMap<>();
    private static final String USERS_DATA_FILE = "users.dat";
    private static final String USERS_JSON_FILE = "users.json";

    public UserManager() {
        loadUsers();
    }

    private String hashPassword(String password) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public void register(Scanner sc) {
        System.out.print("Enter email ID to register: ");
        String email = sc.nextLine().trim();
        if (!email.contains("@")) {
            System.out.println("❌ Invalid email format.");
            return;
        }
        if (users.containsKey(email.toLowerCase())) {
            System.out.println("❌ This email is already registered.");
            return;
        }
        System.out.print("Create a password: ");
        String password = sc.nextLine();
        users.put(email.toLowerCase(), hashPassword(password));
        saveUsers();
        generateUsersJSON();
        System.out.println("✅ Registration successful!");
    }

    public String login(Scanner sc) {
        System.out.print("Enter your email ID: ");
        String email = sc.nextLine().trim().toLowerCase();
        System.out.print("Enter password: ");
        String password = sc.nextLine();
        String storedHash = users.get(email);
        if (storedHash != null && storedHash.equals(hashPassword(password))) {
            System.out.println("✅ Login successful!");
            return email;
        } else {
            System.out.println("❌ Invalid email or password.");
            return null;
        }
    }

    public void generateUsersJSON() {
        try (PrintWriter w = new PrintWriter(new FileWriter(USERS_JSON_FILE))) {
            String json = users.entrySet().stream()
                    .map(entry -> "\"" + entry.getKey() + "\":\"" + entry.getValue() + "\"")
                    .collect(Collectors.joining(",", "{", "}"));
            w.println(json);
        } catch (IOException e) {
            System.out.println("❌ Error generating users.json file.");
        }
    }

    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_DATA_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            System.out.println("❌ Error saving user data.");
        }
    }

    @SuppressWarnings("unchecked")
    private void loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USERS_DATA_FILE))) {
            users = (Map<String, String>) ois.readObject();
        } catch (FileNotFoundException e) { /* Normal on first run */
        } catch (Exception e) {
            System.out.println("❌ Error loading user data.");
        }
    }
}