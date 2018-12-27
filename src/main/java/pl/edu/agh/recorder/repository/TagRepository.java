package pl.edu.agh.recorder.repository;

import org.springframework.data.repository.CrudRepository;
import pl.edu.agh.recorder.entity.Tag;

import java.util.Optional;

public interface TagRepository extends CrudRepository<Tag, Long> {
    Optional<Tag> findByTag(String tag);
}