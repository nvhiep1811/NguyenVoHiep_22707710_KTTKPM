package nvhiep.authservice.repository;

import nvhiep.authservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {

    List<Role> findByNameIn(Collection<String> names);
}
