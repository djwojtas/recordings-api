package pl.edu.agh.recorder.repository;

import org.springframework.data.repository.CrudRepository;
import pl.edu.agh.recorder.entity.ApplicationUser;

import java.util.Optional;

public interface ApplicationUserRepository extends CrudRepository<ApplicationUser, Long> {
    Optional<ApplicationUser> findByUsername(String username);
}
