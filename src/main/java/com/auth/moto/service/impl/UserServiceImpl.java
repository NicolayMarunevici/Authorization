package com.auth.moto.service.impl;

import com.auth.moto.entity.User;
import com.auth.moto.exception.MotoSharingException;
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
}





