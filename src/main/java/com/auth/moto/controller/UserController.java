package com.auth.moto.controller;

import com.auth.moto.entity.User;
import com.auth.moto.service.impl.UserServiceImpl;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
public class UserController {

  private UserServiceImpl userServiceImpl;

  public UserController(UserServiceImpl userServiceImpl) {
    this.userServiceImpl = userServiceImpl;
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
  public ResponseEntity<List<User>> getAll() {

    return ResponseEntity.ok(userServiceImpl.getAll());
  }

  @PreAuthorize(value = "{hasRole('USER'), hasRole('ADMIN')}")
  @GetMapping("/{id}")
  public ResponseEntity<User> getById(@PathVariable long id) {
    return ResponseEntity.ok(userServiceImpl.getById(id));
  }
}
