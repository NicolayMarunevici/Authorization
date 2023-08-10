package com.auth.moto.service.impl;

import com.auth.moto.entity.Role;
import com.auth.moto.entity.User;
import com.auth.moto.entity.dto.LoginDto;
import com.auth.moto.entity.dto.RegisterDto;
import com.auth.moto.exception.MotoSharingException;
import com.auth.moto.repository.RoleRepository;
import com.auth.moto.repository.UserRepository;
import com.auth.moto.security.JwtTokenProvider;
import com.auth.moto.service.AuthService;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;

  public AuthServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository,
                         RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                         JwtTokenProvider jwtTokenProvider) {
    this.authenticationManager = authenticationManager;
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  // Method, which allows to authenticate user
  @Override
  public String login(LoginDto loginDto) {
    log.info("Attempt to authorize user ");
    Authentication authentication =
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
            loginDto.usernameOrEmail(), loginDto.password()));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    String token = jwtTokenProvider.generateToken(authentication);
    log.info("User is authorized");
    return token;
  }

  @Override
  public String register(RegisterDto registerDto) {
    if (userRepository.existsUserByUsername(registerDto.username())) {
      throw new MotoSharingException(
          "User with such username already exists", HttpStatus.BAD_REQUEST);
    } else if (userRepository.existsUserByEmail(registerDto.email())) {
      throw new MotoSharingException(
          "User with such email already exists", HttpStatus.BAD_REQUEST);
    } else {
      Set<Role> roles = new HashSet<>();
      String userRole = "ROLE_USER";
      roles.add(roleRepository.findByName(userRole).orElseThrow(
          () -> new MotoSharingException(
              String.format("Role %s does not exist in database", userRole),
              HttpStatus.BAD_REQUEST)));
      User newUser =
          new User(registerDto.username(), passwordEncoder.encode(registerDto.password()),
              registerDto.email(), roles);
      userRepository.save(newUser);
      return "User has been created";
    }
  }
}
