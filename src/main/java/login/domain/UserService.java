package login.domain;

import login.data.JdbcUserDao;
import login.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final JdbcUserDao userDao;
    private final BCryptPasswordEncoder encoder;

    public UserService(JdbcUserDao userDao, BCryptPasswordEncoder encoder) {
        this.userDao = userDao;
        this.encoder = encoder;
    }

    public List<User> findAll() {
        return userDao.findAll();
    }


    public User findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    public User register(User user, String rawPassword) {
        if (findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("Email is already taken");
        }

        if (!isValidPassword(rawPassword)) {
            throw new IllegalArgumentException("Password does not meet the required strength");
        }

        String hashedPassword = encoder.encode(rawPassword);
        user.setPassword(hashedPassword);

        return userDao.add(user);
    }

    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,16}$";
        return password.matches(passwordPattern);
    }

    public boolean resetPasswordByEmail(String email, String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }
        if (!isValidPassword(rawPassword)) {
            throw new IllegalArgumentException("Password must be 8–16 characters long, include at least one uppercase letter, one digit, and one special character (!@#$%^&*).");
        }
        User user = userDao.findByEmail(email);
        if (user == null) {
            return false;
        }
        // Check if the new password matches the existing one
        if (encoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("New password must be different from the current password.");
        }

        String hashedPassword = encoder.encode(rawPassword);
        user.setPassword(hashedPassword);
        return userDao.update(user);
    }

    // Login functionality
    public User login(String email, String rawPassword) {
        User user = userDao.findByEmail(email);
        if (user != null && encoder.matches(rawPassword, user.getPassword())) {
            return user; // Login successful
        }
        return null; // Invalid credentials
    }
}