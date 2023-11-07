package com.auth.moto.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.auth.moto.entity.Role;
import com.auth.moto.entity.User;
import com.auth.moto.exception.GlobalExceptionHandler;
import com.auth.moto.exception.NoSuchUserException;
import com.auth.moto.service.UserService;
import com.auth.moto.service.impl.UserServiceImpl;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebMvcTest
class UserControllerTest {

  private MockMvc mockMvc;
  @Mock
  private UserServiceImpl userService;
  @Mock
  private UserDetailsService userDetailsService;
  @InjectMocks
  private UserController userController;

  UserDetails userDetails;
  User user;

  @BeforeEach
  public void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(userController)
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();

    userDetails =
        new org.springframework.security.core.userdetails.User("user", "12345",
            List.of(new SimpleGrantedAuthority("ROLE_ADMIN"),
                new SimpleGrantedAuthority("ROLE_USER")));
    user = User.builder().id(1L).username("user").email("user@mail.com").password("12345")
        .refreshToken("12345").roles(Set.of(new Role("ROLE_ADMIN"))).build();

    // given
    given(userDetailsService.loadUserByUsername("user")).willReturn(userDetails);
  }

  @Test
  void givenListOfUsers_whenGetAllUsers_thenReturnEmployeeList() throws Exception {
    List<User> userList = List.of(user);

    // given
    given(userService.getAll()).willReturn(userList);

    // when
    ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1").contentType(
        MediaType.APPLICATION_JSON));

    // then
    response.andDo(print()) // выводит в консоль значения
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$[0].username",
                is(userList.get(0).getUsername())))
        .andExpect(
            jsonPath("$[0].email",
                is(userList.get(0).getEmail())))
        .andExpect(
            jsonPath("$[0].password",
                is(userList.get(0).getPassword())));
  }

  @Test
  void givenUserId_whenGetById_thenReturnUser() throws Exception {
    // given
    long userId = 1L;
    given(userService.getById(userId)).willReturn(user);

    // when
    ResultActions response =
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/{id}", userId).contentType(
            MediaType.APPLICATION_JSON));

    // then
    response.andDo(print()) // выводит в консоль значения
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.username",
                is(user.getUsername())))
        .andExpect(
            jsonPath("$.email",
                is(user.getEmail())))
        .andExpect(
            jsonPath("$.password",
                is(user.getPassword())));
  }

  @Test
  void givenEmptyObject_whenGetById_thenThrowNoSuchUserException() throws Exception {
    // given
    long userId = 1L;
    given(userService.getById(userId)).willThrow(
        new NoSuchUserException("User with id " + userId + " does not exist"));
    // when
    ResultActions response = mockMvc.perform(
        MockMvcRequestBuilders.get("/api/v1/{id}", userId)
            .contentType(MediaType.APPLICATION_JSON));

    // then
    response.andDo(print())
        .andExpect(status().isNotFound());
  }
}