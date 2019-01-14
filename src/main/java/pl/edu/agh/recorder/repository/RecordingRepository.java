package pl.edu.agh.recorder.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import pl.edu.agh.recorder.entity.ApplicationUser;
import pl.edu.agh.recorder.entity.Recording;

import java.util.List;
import java.util.Optional;

public interface RecordingRepository extends CrudRepository<Recording, Long> {
    List<Recording> findByUser(ApplicationUser user);

    @Query("SELECT r FROM Recording r WHERE r.id IN (SELECT p.recording.id FROM Permission p WHERE p.user = :user)")
    List<Recording> findAllUserCanAccess(@Param("user")ApplicationUser user);

    @Query("SELECT r FROM Recording r WHERE r.id IN (SELECT m.recording.id FROM Mark m WHERE m.id = :markId)")
    Optional<Recording> findByMarkId(@Param("markId") Long markId);
}
