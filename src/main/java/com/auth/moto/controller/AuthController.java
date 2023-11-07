package com.auth.moto.controller;

import com.auth.moto.entity.dto.JwtAuthResponse;
import com.auth.moto.entity.dto.LoginDto;
import com.auth.moto.entity.dto.RegisterDto;
import com.auth.moto.service.AuthService;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  //Build Login REST API
  @PostMapping(value = {"/login", "/signin"})
  public ResponseEntity<JwtAuthResponse> login(@RequestBody LoginDto loginDto) {
    Map<String, String> tokens = authService.login(loginDto);

    JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
    jwtAuthResponse.setAccessToken(tokens.get("Access-Token"));
    jwtAuthResponse.setRefreshToken(tokens.get("Refresh-Token"));

    return ResponseEntity.ok(jwtAuthResponse);
  }

  // Build Register REST API
  @PostMapping(value = {"/register", "/signup"})
  public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
    String response = authService.register(registerDto);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }


  @PostMapping("refresh")
  public ResponseEntity<Map<String, String>> generatePairOfTokens(
      @RequestHeader("Refresh-Token") String refreshToken,
      @RequestHeader("Access-Token") String accessToken) {
    Map<String, String> map = authService.generateRefreshAndAccessTokenByExpireTime(refreshToken, accessToken);
    return ResponseEntity.ok(map);
  }
}
