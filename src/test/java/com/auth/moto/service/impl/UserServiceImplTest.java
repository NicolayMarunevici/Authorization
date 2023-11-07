package com.auth.moto.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.auth.moto.entity.Role;
import com.auth.moto.entity.User;
import com.auth.moto.exception.GlobalExceptionHandler;
import com.auth.moto.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserServiceImpl userService;
  private MockMvc mockMvc;
  User user1;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(userService)
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();

    user1 =
        User.builder().id(1L).username("johnCena").password("12345").email("john-cena@mail.com")
            .refreshToken("refresh-token-test").roles(
                Set.of(new Role("ROLE_USER"))).build();
  }

  @Test
  void create() {
    // given
    given(userRepository.save(user1)).willReturn(user1);
    // when
    User savedUser = userService.create(user1);

    //then

    assertThat(savedUser).isNotNull();
  }

  @Test
  void update() {
    // given
    User newUser =
        User.builder().id(2L).username("cola").password("12345").email("cola@mail.com")
            .refreshToken("refresh-token-test").roles(
                Set.of(new Role("ROLE_USER"))).build();


    given(userRepository.findById(anyLong())).willReturn(Optional.of(user1));
    given(userRepository.save(any(User.class))).willAnswer(invoke -> invoke.getArgument(0));

    // when
    Long updatedUser = userService.update(newUser, newUser.getId());

    //then
    assertThat(updatedUser).isNotNull();
    assertThat(updatedUser).isEqualTo(1);
  }

  @Test
  void getAll() {
    // given
    User user2 =
        User.builder().id(2L).username("cola").password("12345").email("cola@mail.com")
            .refreshToken("refresh-token-test").roles(
                Set.of(new Role("ROLE_USER"))).build();

    given(userRepository.findAll()).willReturn(List.of(user1, user2));

    // when

    List<User> userList = userService.getAll();

    //then
    assertThat(userList).isNotNull();
    assertThat(userList).hasSize(2);
  }

  @Test
  void getById() {
    // given
    given(userRepository.findById(1L)).willReturn(Optional.of(user1));

    // when
    User userId = userService.getById(user1.getId());

    //then
    assertThat(userId).isNotNull();
    assertThat(userId.getUsername()).isEqualTo("johnCena");
  }


  @Test
  void getByEmail() {
    // given
    given(userRepository.findByEmail(user1.getEmail())).willReturn(Optional.of(user1));

    // when
    User userByEmail = userService.getByEmail(user1.getEmail());

    //then
    assertThat(userByEmail).isNotNull();
    assertThat(userByEmail.getUsername()).isEqualTo("johnCena");
  }

  @Test
  void getByUsernameOrEmail() {
    // given
    String username = user1.getUsername();
    given(userRepository.findByUsernameOrEmail(username, null)).willReturn(Optional.of(user1));

    // when
    User userByEmail = userService.getByUsernameOrEmail(username, null);

    //then
    assertThat(userByEmail).isNotNull();
    assertThat(userByEmail.getUsername()).isEqualTo("johnCena");


    // given
    String email = user1.getEmail();
    given(userRepository.findByUsernameOrEmail(null, email)).willReturn(Optional.of(user1));

    // when
    User userByUsername = userService.getByUsernameOrEmail(null, email);

    //then
    assertThat(userByUsername).isNotNull();
    assertThat(userByUsername.getEmail()).isEqualTo("john-cena@mail.com");
  }

  @Test
  void getByUsername() {
    // given
    given(userRepository.findByUsername(user1.getUsername())).willReturn(Optional.of(user1));

    // when
    User userByEmail = userService.getByUsername(user1.getUsername());

    //then
    assertThat(userByEmail).isNotNull();
    assertThat(userByEmail.getUsername()).isEqualTo("johnCena");
  }

  @Test
  void existsUserByUsername() {
  }

  @Test
  void existsUserByEmail() {
  }
}