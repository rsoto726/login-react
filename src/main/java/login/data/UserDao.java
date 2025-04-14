package login.data;

import login.model.User;

import java.util.List;

public interface UserDao {
    List<User> findAll();

    User findByEmail(String email) throws Exception;
    User add(User user);
    boolean update(User user);
}
