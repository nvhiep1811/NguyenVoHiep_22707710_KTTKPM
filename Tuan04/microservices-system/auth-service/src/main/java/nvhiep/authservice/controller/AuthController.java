package nvhiep.authservice.controller;

import jakarta.validation.Valid;
import nvhiep.authservice.dto.LoginRequest;
import nvhiep.authservice.dto.LoginResponse;
import nvhiep.authservice.dto.RegisterRequest;
import nvhiep.authservice.dto.AuthUserResponse;
import nvhiep.authservice.service.AuthUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthUserService authUserService;

    public AuthController(AuthUserService authUserService) {
        this.authUserService = authUserService;
    }

    @PostMapping("/register")
    public AuthUserResponse register(@Valid @RequestBody RegisterRequest request) {
        return authUserService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authUserService.login(request);
    }
}
