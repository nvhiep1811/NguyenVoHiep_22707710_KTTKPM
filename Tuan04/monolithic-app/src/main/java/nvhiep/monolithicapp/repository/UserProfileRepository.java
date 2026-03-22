package nvhiep.monolithicapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import nvhiep.monolithicapp.entity.User;
import nvhiep.monolithicapp.entity.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByUser(User user);
}
