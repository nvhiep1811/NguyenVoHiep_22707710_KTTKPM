package nvhiep.authservice.controller;

import jakarta.validation.Valid;
import nvhiep.authservice.dto.AssignRolesRequest;
import nvhiep.authservice.dto.AuthUserResponse;
import nvhiep.authservice.dto.CreateAuthUserRequest;
import nvhiep.authservice.service.AuthUserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth/users")
public class AuthUserController {

    private final AuthUserService authUserService;

    public AuthUserController(AuthUserService authUserService) {
        this.authUserService = authUserService;
    }

    @GetMapping
    public List<AuthUserResponse> findAll() {
        return authUserService.findAll();
    }

    @GetMapping("/{id}")
    public AuthUserResponse findById(@PathVariable Long id) {
        return authUserService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthUserResponse create(@Valid @RequestBody CreateAuthUserRequest request) {
        return authUserService.create(request);
    }

    @PutMapping("/{id}/roles")
    public AuthUserResponse assignRoles(@PathVariable Long id,
                                        @Valid @RequestBody AssignRolesRequest request) {
        return authUserService.assignRoles(id, request.roles());
    }
}
