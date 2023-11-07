package com.auth.moto.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.auth.moto.entity.Role;
import com.auth.moto.entity.User;
import com.auth.moto.exception.NoSuchUserException;
import java.util.ArrayList;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;
  User user;

  @BeforeEach
  public void setup() {
    user =
        User.builder().id(2L).username("johnCena").password("12345").email("john-cena@mail.com")
            .refreshToken("refresh-token-test").roles(
                Set.of(new Role("ROLE_USER"))).build();
  }

  @Test
  void givenUser_whenFindByEmail_thenReturnUser() {
    // given
    userRepository.save(user);

    // when
    User userByEmail = userRepository.findByEmail(user.getEmail()).orElseThrow(
        () -> new NoSuchUserException("Such user does not exists")
    );

    // then
    assertThat(userByEmail).isNotNull();
    assertThat(userByEmail).usingRecursiveComparison().ignoringFields("roles")
        .isEqualTo(user);
    assertThat(new ArrayList<>(userByEmail.getRoles()).get(0).getName()).isEqualTo(
        new ArrayList<>(user.getRoles()).get(0).getName());
  }

  @Test
  void givenUser_whenFindByUsernameOrEmail_thenReturnUser() {
    // given
    String username = null;
    String email = user.getEmail();
    userRepository.save(user);

    // when username is null
    User userByEmail = userRepository.findByUsernameOrEmail(username, email).orElseThrow(
        () -> new NoSuchUserException("Such user does not exists")
    );

    // then
    assertThat(userByEmail).isNotNull();
    assertThat(userByEmail).usingRecursiveComparison().ignoringFields("roles")
        .isEqualTo(user);
    assertThat(new ArrayList<>(userByEmail.getRoles()).get(0).getName()).isEqualTo(
        new ArrayList<>(user.getRoles()).get(0).getName());


    // given
    username = user.getUsername();
    email = null;

    // when email is null
    User userByUsername = userRepository.findByUsernameOrEmail(username, email).orElseThrow(
        () -> new NoSuchUserException("Such user does not exists")
    );

    // then
    assertThat(userByUsername).isNotNull();
    assertThat(userByUsername).usingRecursiveComparison().ignoringFields("roles")
        .isEqualTo(user);
    assertThat(new ArrayList<>(userByUsername.getRoles()).get(0).getName()).isEqualTo(
        new ArrayList<>(user.getRoles()).get(0).getName());

  }

  @Test
  void givenUser_whenFindByUsername_thenReturnUser() {
    // given
    String username = user.getUsername();
    userRepository.save(user);

    // when email is null
    User userByUsername = userRepository.findByUsername(username).orElseThrow(
        () -> new NoSuchUserException("Such user does not exists")
    );

    // then
    assertThat(userByUsername).isNotNull();
    assertThat(userByUsername).usingRecursiveComparison().ignoringFields("roles")
        .isEqualTo(user);
    assertThat(new ArrayList<>(userByUsername.getRoles()).get(0).getName()).isEqualTo(
        new ArrayList<>(user.getRoles()).get(0).getName());

  }

  @Test
  void existsUserByUsername() {
    // given
    userRepository.save(user);

    // when email is null
    Boolean existsUserByUsername = userRepository.existsUserByUsername(user.getUsername());

    // then
    assertThat(existsUserByUsername).isTrue();

  }

  @Test
  void existsUserByEmail() {
    // given
    userRepository.save(user);

    // when email is null
    Boolean existsUserByEmail = userRepository.existsUserByEmail(user.getEmail());

    // then
    assertThat(existsUserByEmail).isTrue();

  }
}