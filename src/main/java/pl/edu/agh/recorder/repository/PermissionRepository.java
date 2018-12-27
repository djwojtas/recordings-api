package pl.edu.agh.recorder.repository;

import org.springframework.data.repository.CrudRepository;
import pl.edu.agh.recorder.entity.ApplicationUser;
import pl.edu.agh.recorder.entity.Permission;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends CrudRepository<Permission, Long> {
    Optional<Permission> findByUserAndRecording_Id(ApplicationUser user, Long recordingId);

    List<Permission> findAllByRecording_Id(Long recordingId);

    void deleteAllByRecording_Id(Long recordingId);
}