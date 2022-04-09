package my.seconduserservice.service;

import my.seconduserservice.dto.LoginDto;
import my.seconduserservice.dto.UserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class GetService {
    private final RestTemplate client;
    private final String loginUrl;
    private final String getAllUrl;
    private final JwtDecoder decoder;

    public GetService(RestTemplate client,
                      @Value("${first.service.login.url}") String loginUrl,
                      @Value("${first.service.getAll.url}") String getAllUrl,
                      JwtDecoder decoder) {
        this.client = client;
        this.loginUrl = loginUrl;
        this.getAllUrl = getAllUrl;
        this.decoder = decoder;
    }

    public ResponseEntity<?> getAll(LoginDto loginDto) {
        ResponseEntity<?> response = authenticate(loginDto);
        if (response.getStatusCode().value() == 200) {
            String token = response.getHeaders().getFirst("Authorization");
            response = token != null
                    ? makeRequest(token)
                    : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return response;
    }

    private ResponseEntity<?> authenticate(LoginDto loginDto) {
        HttpEntity<LoginDto> httpEntity = new HttpEntity<>(loginDto);
        ResponseEntity<Void> result;
        try {
            result = client.exchange(loginUrl, HttpMethod.POST, httpEntity, Void.class);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return result;
    }

    private ResponseEntity<?> makeRequest(String token) {
        ResponseEntity<?> result;
        String role = decoder
                .decode(token.substring(7))
                .getClaimAsString("scope");
        if (role.equals("ROLE_ADMIN")) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", token);
            HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
            result = client.exchange(
                    getAllUrl,
                    HttpMethod.GET,
                    httpEntity,
                    new ParameterizedTypeReference<List<UserDto>>() {
                    });
        } else {
            result = ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return result;
    }
}
