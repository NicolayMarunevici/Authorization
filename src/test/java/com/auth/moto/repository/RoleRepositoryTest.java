package com.auth.moto.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.auth.moto.entity.Role;
import com.auth.moto.exception.NoSuchUserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RoleRepositoryTest {

  @Autowired
  private RoleRepository roleRepository;

  Role role;

  @BeforeEach
  public void setup() {
    role = new Role();
    role.setId(1L);
    role.setName("ROLE_USER");
  }

  @Test
  void findByName() {
    // given
    roleRepository.save(role);

    // when
    Role roleByName = roleRepository.findByName(role.getName())
        .orElseThrow(() -> new NoSuchUserException("This Role does not exists"));

    //then
    assertThat(role).usingRecursiveComparison().isEqualTo(roleByName);

  }
}