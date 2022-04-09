package my.seconduserservice.controller;

import my.seconduserservice.dto.LoginDto;
import my.seconduserservice.service.GetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    private final GetService service;

    public Controller(GetService service) {
        this.service = service;
    }

    @PostMapping("/")
    public ResponseEntity<?> get(@RequestBody LoginDto loginDto) {
        return service.getAll(loginDto);
    }
}
