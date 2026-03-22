package nvhiep.monolithicapp.controller;

import jakarta.validation.Valid;
import nvhiep.monolithicapp.dto.MonolithLoginRequest;
import nvhiep.monolithicapp.dto.MonolithLoginResponse;
import nvhiep.monolithicapp.dto.MonolithRegisterRequest;
import nvhiep.monolithicapp.dto.MonolithUserResponse;
import nvhiep.monolithicapp.service.MonolithUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mono/auth")
public class MonolithAuthController {

    private final MonolithUserService monolithUserService;

    public MonolithAuthController(MonolithUserService monolithUserService) {
        this.monolithUserService = monolithUserService;
    }

    @PostMapping("/register")
    public MonolithUserResponse register(@Valid @RequestBody MonolithRegisterRequest request) {
        return monolithUserService.register(request);
    }

    @PostMapping("/login")
    public MonolithLoginResponse login(@Valid @RequestBody MonolithLoginRequest request) {
        return monolithUserService.login(request);
    }
}
