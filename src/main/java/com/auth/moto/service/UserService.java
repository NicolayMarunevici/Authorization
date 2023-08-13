package com.auth.moto.service;

import com.auth.moto.entity.User;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

  List<User> getAll();

  User getById(long id);

  User update(User user, long id);

  User create(User user);

  User getByEmail(String email);

  User getByUsernameOrEmail(String username, String email);

  User getByUsername(String username);

  Boolean existsUserByUsername(String username);

  Boolean existsUserByEmail(String email);
}
