package com.auth.moto.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.auth.moto.entity.Role;
import com.auth.moto.entity.User;
import com.auth.moto.entity.dto.LoginDto;
import com.auth.moto.entity.dto.RegisterDto;
import com.auth.moto.exception.GlobalExceptionHandler;
import com.auth.moto.exception.NoSuchUserException;
import com.auth.moto.service.impl.AuthServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebMvcTest
class AuthControllerTest {

  private MockMvc mockMvc;
  @Mock
  private AuthServiceImpl authService;
  @Mock
  private UserDetailsService userDetailsService;
  @Autowired
  private ObjectMapper objectMapper;
  @InjectMocks
  private AuthController authController;
  UserDetails userDetails;
  User user;

  @BeforeEach
  public void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(authController)
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();

    userDetails =
        new org.springframework.security.core.userdetails.User("user", "12345",
            List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    user = User.builder().id(1L).username("user").email("user@mail.com").password("12345")
        .refreshToken("12345").roles(Set.of(new Role("ROLE_ADMIN"), new Role("ROLE_USER"))).build();

    // given
    given(userDetailsService.loadUserByUsername("user")).willReturn(userDetails);
  }


  @Test
  void givenLoginDto_whenLogin_thenReturnJwtAuthResponse() throws Exception {

    // given
    LoginDto loginDto = new LoginDto("user", "12345");
    Map<String, String> map = new HashMap<>();
    map.put("Refresh-Token", "12345");
    map.put("Access-Token", "12345");
    given(authService.login(loginDto)).willReturn(map);

    // when
    ResultActions response = mockMvc.perform(post("/api/auth/login")
        .content(objectMapper.writeValueAsString(loginDto))
        .contentType(MediaType.APPLICATION_JSON));


    // then
    response.andDo(print()) // выводит в консоль значения
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.refreshToken",
                is(map.get("Refresh-Token"))))
        .andExpect(
            jsonPath("$.accessToken",
                is(map.get("Access-Token"))))
        .andExpect(
            jsonPath("$.tokenType",
                is("Bearer")));
  }

  @Test
  void givenRegisterDto_whenRegister_thenReturnString() throws Exception {
    // given
    RegisterDto registerDto =
        RegisterDto.builder().name("User").username("user").password("12345").email("user@mail.com")
            .build();
    given(authService.register(registerDto)).willReturn("User has been created");

    // when
    ResultActions response = mockMvc.perform(post("/api/auth/register")
        .content(objectMapper.writeValueAsString(registerDto))
        .contentType(MediaType.APPLICATION_JSON));

    // then
    response.andDo(print()) // выводит в консоль значения
        .andExpect(status().isCreated())
        .andExpect(content().string("User has been created"));
  }


  @Test
  void givenRegisterDtoWithNonExistingUsername_whenRegister_thenThrowNoSuchUserException()
      throws Exception {
    // given
    RegisterDto registerDto =
        RegisterDto.builder().name("NonExistingUser").username("nonExistingUser").password("12345")
            .email("nonExistingUser@mail.com")
            .build();
    given(authService.register(registerDto)).willThrow(
        new NoSuchUserException("User with such username already exists"));

    // when
    ResultActions response = mockMvc.perform(post("/api/auth/register")
        .content(objectMapper.writeValueAsString(registerDto))
        .contentType(MediaType.APPLICATION_JSON));

    // then
    response.andDo(print()) // выводит в консоль значения
        .andExpect(status().isNotFound());
  }

  @Test
  void givenRegisterDtoWithNonExistingEmail_whenRegister_thenThrowNoSuchUserException()
      throws Exception {
    // given
    RegisterDto registerDto =
        RegisterDto.builder().name("NonExistingEmail").username("nonExistingEmail")
            .password("12345").email("nonExistingEmail@mail.com")
            .build();
    given(authService.register(registerDto)).willThrow(
        new NoSuchUserException("User with such email already exists"));

    // when
    ResultActions response = mockMvc.perform(post("/api/auth/register")
        .content(objectMapper.writeValueAsString(registerDto))
        .contentType(MediaType.APPLICATION_JSON));

    // then
    response.andDo(print()) // выводит в консоль значения
        .andExpect(status().isNotFound());
  }

  @Test
  void generatePairOfTokens() throws Exception {
    // given
    String oldRefreshToken = "oldRefreshToken12345";
    String oldAccessToken = "oldAccessToken12345";

    String newRefreshToken = "newRefreshToken12345";
    String newAccessToken = "newAccessToken12345";

    Map<String, String> map = new HashMap<>();
    map.put("RefreshToken", newRefreshToken);
    map.put("AccessToken", newAccessToken);

    given(authService.generateRefreshAndAccessTokenByExpireTime(oldRefreshToken,
        oldAccessToken)).willReturn(map);

    // when
    ResultActions response = mockMvc.perform(
        post("/api/auth/refresh").header("Refresh-Token", oldRefreshToken)
            .header("Access-Token", oldAccessToken)
            .contentType(MediaType.APPLICATION_JSON));

    // then
    response.andDo(print()) // выводит в консоль значения
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.RefreshToken", is(map.get("RefreshToken"))))
        .andExpect(jsonPath("$.AccessToken", is(map.get("AccessToken"))));
  }
}