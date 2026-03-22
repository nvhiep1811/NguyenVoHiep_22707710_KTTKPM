package nvhiep.monolithicapp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import nvhiep.monolithicapp.dto.CreateUserRequest;
import nvhiep.monolithicapp.dto.MonolithUserResponse;
import nvhiep.monolithicapp.dto.UpdateUserProfileRequest;
import nvhiep.monolithicapp.service.MonolithUserService;

@RestController
@RequestMapping("/api/mono/users")
public class MonolithUserController {

    private final MonolithUserService monolithUserService;

    public MonolithUserController(MonolithUserService monolithUserService) {
        this.monolithUserService = monolithUserService;
    }

    @GetMapping
    public List<MonolithUserResponse> findAll() {
        return monolithUserService.findAllUsers();
    }

    @GetMapping("/{id}")
    public MonolithUserResponse findById(@PathVariable Long id) {
        return monolithUserService.findUserById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MonolithUserResponse create(@Valid @RequestBody CreateUserRequest request) {
        return monolithUserService.createUser(request);
    }

    @PutMapping("/{id}/profile")
    public MonolithUserResponse updateProfile(@PathVariable Long id,
                                              @Valid @RequestBody UpdateUserProfileRequest request) {
        return monolithUserService.updateProfile(id, request.profile());
    }
}
