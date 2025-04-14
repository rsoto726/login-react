package login.data;

import login.model.User;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.*;
import java.sql.PreparedStatement;
import java.util.List;

import login.data.mappers.UserMapper;

@Repository
public class JdbcUserDao implements UserDao {

    private final JdbcTemplate template;

    public JdbcUserDao(JdbcTemplate template) {
        this.template = template;
    }
    @Override
    public List<User> findAll() {
        final String sql = "SELECT id, username, email, password FROM users;";
        return template.query(sql, new UserMapper());
    }


    @Override
    public User findByEmail(String email) {
        final String sql = "SELECT id, username, email, password FROM users WHERE email = ?;";
        return template.query(sql, new UserMapper(), email).stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public User add(User user) {
        final String sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = template.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            return ps;
        }, keyHolder);

        if (rowsAffected <= 0) {
            return null;
        }

        user.setId(keyHolder.getKey().longValue());
        return user;
    }
    @Override
    public boolean update(User user) {
        final String sql = "UPDATE users SET username = ?, email = ?, password = ? WHERE id = ?;";
        return template.update(sql,
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getId()) > 0;
    }
}
