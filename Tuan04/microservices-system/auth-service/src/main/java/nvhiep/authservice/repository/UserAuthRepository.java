package nvhiep.authservice.repository;

import nvhiep.authservice.entity.UserAuth;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {

    @EntityGraph(attributePaths = "roles")
    List<UserAuth> findAll();

    @EntityGraph(attributePaths = "roles")
    Optional<UserAuth> findWithRolesById(Long id);

    @EntityGraph(attributePaths = "roles")
    Optional<UserAuth> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
