package com.auth.moto.service.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import com.auth.moto.entity.Role;
import com.auth.moto.entity.User;
import com.auth.moto.entity.dto.LoginDto;
import com.auth.moto.security.JwtTokenProvider;
import com.auth.moto.service.UserService;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
@ExtendWith(MockitoExtension.class)
public class TestLoginExampleTest {
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    public void testLogin() {

      User user = User.builder().id(1L).username("user").email("user@mail.com").password("12345")
          .refreshToken("12345").roles(Set.of(new Role("ROLE_ADMIN"), new Role("ROLE_USER"))).build();


      LoginDto loginDto = new LoginDto("user", "12345");

      Authentication authentication = Mockito.mock(Authentication.class);
      given(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
          .willReturn(authentication);

      String accessToken = "access_token";
      given(jwtTokenProvider.generateToken(authentication)).willReturn(accessToken);

      String refreshToken = "refresh_token";
      given(jwtTokenProvider.getUsername(anyString())).willReturn("specific-access-token");
      given(userService.getByEmail(Mockito.anyString())).willReturn(new User());
//      given(userService.update(any(User.class), anyLong())).willReturn(null);

//      doNothing().when(userService).update(any(User.class), anyLong());
      doReturn(null).when(userService).update(any(User.class), anyLong());

//      given(userService.update(User.class))

      Map<String, String> result = authService.login(loginDto);

      Assertions.assertEquals(refreshToken, result.get("Refresh-Token"));
      Assertions.assertEquals(accessToken, result.get("Access-Token"));
    }
  }