package com.auth.moto.service;

import com.auth.moto.entity.User;
import java.util.List;

public interface UserService {

  List<User> getAll();

  User getById(long id);

  Long update(User user, Long id);

  User create(User user);

  User getByEmail(String email);

  User getByUsernameOrEmail(String username, String email);

  User getByUsername(String username);

  Boolean existsUserByUsername(String username);

  Boolean existsUserByEmail(String email);
}
