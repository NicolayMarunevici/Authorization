package com.auth.moto.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.auth.moto.entity.Role;
import com.auth.moto.entity.User;
import com.auth.moto.entity.dto.RegisterDto;
import com.auth.moto.exception.NoSuchUserException;
import com.auth.moto.repository.RoleRepository;
import com.auth.moto.security.JwtTokenProvider;
import com.auth.moto.service.UserService;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

  @Mock
  private AuthenticationManager authenticationManager;
  @Mock
  private UserService userService;
  @Mock
  private RoleRepository roleRepository;
  @Mock
  private JwtTokenProvider jwtTokenProvider;
  @Mock
  private UserDetailsService userDetailsService;
  @Mock
  private PasswordEncoder passwordEncoder;
  @InjectMocks
  private AuthServiceImpl authService;
  UserDetails userDetails;
  User user;

  @BeforeEach
  public void setup() {
    user = User.builder().id(1L).username("user").email("user@mail.com").password("12345")
        .refreshToken("12345").roles(Set.of(new Role("ROLE_ADMIN"), new Role("ROLE_USER"))).build();
  }


  @Test
  public void givenRegisterDto_whenRegister_ThenReturnString() {
    // given
    RegisterDto registerDto =
        RegisterDto.builder().name("User").username(user.getUsername())
            .password(user.getPassword()).email(user.getEmail())
            .build();

    given(userService.existsUserByUsername(anyString())).willReturn(false);
    given(userService.existsUserByEmail(anyString())).willReturn(false);
    given(passwordEncoder.encode(anyString())).willReturn("12345");
    given(roleRepository.findByName(anyString())).willReturn(Optional.of(new Role("ROLE_USER")));
    given(jwtTokenProvider.generateToken(anyString())).willReturn("access-token-example");

    // when
    String registerSuccesfuly = authService.register(registerDto);

    // then
    assertThat(registerSuccesfuly).isNotEmpty();
    assertThat(registerSuccesfuly).isEqualTo("User has been created");
  }

  @Test
  void givenRegisterDtoWithNonExistingUsername_whenRegister_thenThrowNoSuchUserException()
      throws Exception {
    // given
    RegisterDto registerDto =
        RegisterDto.builder().name("NonExistentUser").username(user.getUsername())
            .password(user.getPassword()).email(user.getEmail())
            .build();

    given(userService.existsUserByUsername(anyString())).willReturn(true);

    // when
    assertThrows(NoSuchUserException.class, () -> authService.register(registerDto));


    // then
    verify(userService, never()).getByEmail(anyString());
  }

  @Test
  void givenRegisterDtoWithNonExistingEmail_whenRegister_thenThrowNoSuchUserException()
      throws Exception {
    // given
    RegisterDto registerDto =
        RegisterDto.builder().name("NonExistentUser").username(user.getUsername())
            .password(user.getPassword()).email(user.getEmail())
            .build();

    given(userService.existsUserByEmail(anyString())).willReturn(true);

    // when
    assertThrows(NoSuchUserException.class, () -> authService.register(registerDto));


    // then
    verify(userService, never()).getByEmail(anyString());
  }

//  @Test
//  void generateRefreshAndAccessTokenByExpireTime(){
//    // given
//    given(
//  }

}