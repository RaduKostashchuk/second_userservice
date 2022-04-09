package my.seconduserservice.service;

import my.seconduserservice.dto.LoginDto;
import my.seconduserservice.dto.UserDto;
import my.seconduserservice.model.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetServiceTest {
    @Mock
    private RestTemplate client;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private JwtDecoder decoder;

    private final String loginUrl = "loginUrl";
    private final String getAllUrl = "getAllUrl";

    @Test
    public void whenFailedAuthenticationThenThrowException() {
        LoginDto loginDto = LoginDto.of("user", "12345");
        when(client.exchange(loginUrl, HttpMethod.POST, new HttpEntity<>(loginDto), Void.class))
                .thenThrow(HttpClientErrorException.class);
        GetService getService = new GetService(client, loginUrl, getAllUrl, decoder);
        assertThat(HttpStatus.UNAUTHORIZED, equalTo(getService.getAll(loginDto).getStatusCode()));
    }

    @Test
    public void whenResponseFromFirstServiceDoesntHaveAuthorizationHeaderThenThrowException() {
        LoginDto loginDto = LoginDto.of("user", "12345");
        when(client.exchange(loginUrl, HttpMethod.POST, new HttpEntity<>(loginDto), Void.class))
                .thenReturn(ResponseEntity.ok().build());
        GetService getService = new GetService(client, loginUrl, getAllUrl, decoder);
        assertThat(HttpStatus.UNAUTHORIZED, equalTo(getService.getAll(loginDto).getStatusCode()));
    }

    @Test
    public void whenUserDoesntHaveAdminRoleThenThrowException() {
        LoginDto loginDto = LoginDto.of("user", "12345");
        when(client.exchange(loginUrl, HttpMethod.POST, new HttpEntity<>(loginDto), Void.class))
                .thenReturn(ResponseEntity.ok().header("Authorization", "Bearer token").build());
        GetService getService = new GetService(client, loginUrl, getAllUrl, decoder);
        when(decoder.decode(any(String.class)).getClaimAsString("scope")).thenReturn("ROLE_USER");
        assertThat(HttpStatus.FORBIDDEN, equalTo(getService.getAll(loginDto).getStatusCode()));
    }

    @Test
    public void whenSuccess() {
        LoginDto loginDto = LoginDto.of("user", "12345");
        Role role = Role.of("ROLE_ADMIN");
        UserDto userDto = UserDto.of(1, "user", role);
        when(client.exchange(loginUrl, HttpMethod.POST, new HttpEntity<>(loginDto), Void.class))
                .thenReturn(ResponseEntity.ok().header("Authorization", "Bearer token").build());
        GetService getService = new GetService(client, loginUrl, getAllUrl, decoder);
        when(decoder.decode(any(String.class)).getClaimAsString("scope")).thenReturn("ROLE_ADMIN");
        when(client.exchange(
                eq(getAllUrl),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(new ParameterizedTypeReference<List<UserDto>>() { })))
                .thenReturn(new ResponseEntity<>(List.of(userDto), HttpStatus.OK));
        assertThat(new ResponseEntity<>(List.of(userDto), HttpStatus.OK), equalTo(getService.getAll(loginDto)));
    }

}