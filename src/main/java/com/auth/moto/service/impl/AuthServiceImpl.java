package com.auth.moto.service.impl;

import com.auth.moto.entity.Role;
import com.auth.moto.entity.User;
import com.auth.moto.entity.dto.LoginDto;
import com.auth.moto.entity.dto.RegisterDto;
import com.auth.moto.exception.InvalidTokenException;
import com.auth.moto.exception.MotoSharingException;
import com.auth.moto.exception.NoSuchUserException;
import com.auth.moto.repository.RoleRepository;
import com.auth.moto.security.JwtTokenProvider;
import com.auth.moto.service.AuthService;
import com.auth.moto.service.UserService;
import jakarta.validation.Valid;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
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
  private final UserService userService;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private Random rand = SecureRandom.getInstanceStrong();
  public AuthServiceImpl(AuthenticationManager authenticationManager, UserService userService,
                         RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                         JwtTokenProvider jwtTokenProvider) throws NoSuchAlgorithmException {
    this.authenticationManager = authenticationManager;
    this.userService = userService;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  // Method, which allows to authenticate user
  @Override
  public Map<String, String> login(LoginDto loginDto) {
    log.info("Attempt to authorize user");
    Authentication authentication =
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
            loginDto.usernameOrEmail(), loginDto.password()));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    String accessToken = jwtTokenProvider.generateToken(authentication);
    String refreshToken = generateRefreshTokenByRandomString(accessToken);
    Map<String, String> map = new HashMap<>();
    map.put("Refresh-Token", refreshToken);
    map.put("Access-Token", accessToken);

    User user = userService.getByEmail(jwtTokenProvider.getUsername(accessToken));
    user.setRefreshToken(refreshToken);
    userService.update(user, user.getId());

    log.info("User is authorized");
    return map;
  }

  @Override
  public String register(@Valid RegisterDto registerDto) {
    log.info("Attempt to register user");
    if (userService.existsUserByUsername(registerDto.username())) {
      throw new NoSuchUserException(
          "User with such username already exists");
    } else if (userService.existsUserByEmail(registerDto.email())) {
      throw new NoSuchUserException(
          "User with such email already exists");
    } else {
      Set<Role> roles = new HashSet<>();
      String userRole = "ROLE_USER";
      roles.add(roleRepository.findByName(userRole).orElseThrow(
          () -> new MotoSharingException(
              String.format("Role %s does not exist in database", userRole),
              HttpStatus.BAD_REQUEST)));

      Map<String, String> pairOfTokens = generateRefreshAndAccessToken(registerDto.username());

      User newUser =
          new User(registerDto.username(), passwordEncoder.encode(registerDto.password()),
              registerDto.email(), roles, pairOfTokens.get("Refresh-Token"));
      userService.create(newUser);
      log.info("User has been registered");

      return "User has been created";
    }
  }


  public Map<String, String> generateRefreshAndAccessTokenByExpireTime(String refreshToken,
                                                                        String accessToken) {
    log.info("Attempt to generate refresh and access token");
    if (validateRefreshAndAccessToken(refreshToken, accessToken)) {
      String newAccessToken =
          jwtTokenProvider.generateToken(jwtTokenProvider.getUsername(accessToken));

      String newRefreshToken = generateRefreshTokenByRandomString(newAccessToken);
      Map<String, String> map = new HashMap<>();
      map.put("RefreshToken", newRefreshToken);
      map.put("AccessToken", newAccessToken);

      User user = userService.getByEmail(jwtTokenProvider.getUsername(accessToken));
      user.setRefreshToken(newRefreshToken);
      userService.update(user, user.getId());
      return map;
    } else {
      throw new InvalidTokenException("This token is invalid");
    }
  }

  public Map<String, String> generateRefreshAndAccessToken(String username) {
    String accessToken = jwtTokenProvider.generateToken(username);
    String refreshToken = generateRefreshTokenByRandomString(accessToken);
    Map<String, String> map = new HashMap<>();
    map.put("Refresh-Token", refreshToken);
    map.put("Access-Token", accessToken);
    return map;
  }


  private String generateRefreshTokenByRandomString(String accessToken) {
    return rand.ints(48, 123)
        .filter(num -> (num < 58 || num > 64) && (num < 91 || num > 96))
        .limit(15)
        .mapToObj(c -> (char) c)
        .collect(StringBuffer::new, StringBuffer::append, StringBuffer::append)
        .toString().concat("_" + accessToken.substring(accessToken.length() - 6));
  }

  private boolean validateRefreshAndAccessToken(String refreshToken, String accessToken) {
    log.info("Validating of refresh and access token");
    if (refreshToken.substring(refreshToken.length() - 6)
        .equals(accessToken.substring(accessToken.length() - 6))) {
      User user = userService.getByEmail(jwtTokenProvider.getUsername(accessToken));
      if (user.getRefreshToken().equals(refreshToken)) {
        return jwtTokenProvider.validateToken(accessToken);
      }
    }
    return false;
  }
}

// verify by 7 characters
// verify in db
// verify access
// generate new refresh and access token
// update in db refresh token
// return Map<> tokens

// unit test
// vault integration
// sentry integration
// library rabbit mq
// send messages in queue
