package pl.edu.agh.recorder.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import pl.edu.agh.recorder.entity.ApplicationUser;
import pl.edu.agh.recorder.entity.Recording;

import java.util.List;
import java.util.Optional;

public interface RecordingRepository extends CrudRepository<Recording, Long> {
    List<Recording> findByUser(ApplicationUser user);

    @Query("SELECT r FROM Recording r WHERE r.id = (SELECT m.id FROM Mark m WHERE m.id = :markId)")
    Optional<Recording> findByMarkId(Long markId);
}
