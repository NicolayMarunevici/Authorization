package com.auth.moto.service.impl;

import com.auth.moto.entity.User;
import com.auth.moto.exception.MotoSharingException;
import com.auth.moto.exception.NoSuchUserException;
import com.auth.moto.repository.UserRepository;
import com.auth.moto.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;

  public List<User> getAll() {
    log.info("Get All Users");
    return userRepository.findAll();
  }

  public User getById(long id) {
    log.info("Find User By Id");
    return userRepository.findById(id)
        .orElseThrow(() -> new MotoSharingException("User with id " + id + " does not exist",
            HttpStatus.NOT_FOUND));
  }

  public User create(User user) {
    log.info("Create User");
    return userRepository.save(user);
  }

  public User update(User user, long id) {
    log.info("Update User");
    User userById = userRepository.findById(id)
        .orElseThrow(() -> new NoSuchUserException("User was not found with with username"));

    userById.setUsername(user.getUsername());
    userById.setEmail(user.getEmail());
    userById.setPassword(user.getPassword());
    userById.setRefreshToken(user.getRefreshToken());
    userById.setRoles(user.getRoles());
    userRepository.save(userById);

    return userById;
  }


  @Override
  public User getByEmail(String email) {
    log.info("Find User By Email");
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new NoSuchUserException("User was not found with such email"));
  }

  @Override
  public User getByUsernameOrEmail(String username, String email) {
    log.info("Find User By Username or Email");
    return userRepository.findByUsernameOrEmail(username, email)
        .orElseThrow(
            () -> new NoSuchUserException("User was not found with such username or email"));
  }

  @Override
  public User getByUsername(String username) {
    log.info("Find User By Username");
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new NoSuchUserException("User was not found with such username"));
  }

  @Override
  public Boolean existsUserByUsername(String username) {
    log.info("Check if User exists by Username");
    return userRepository.existsUserByUsername(username);
  }

  @Override
  public Boolean existsUserByEmail(String email) {
    log.info("Check if User exists by Email");
    return userRepository.existsUserByEmail(email);
  }
}





