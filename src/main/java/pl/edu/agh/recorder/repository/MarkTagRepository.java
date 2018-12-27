package pl.edu.agh.recorder.repository;

import org.springframework.data.repository.CrudRepository;
import pl.edu.agh.recorder.entity.MarkTag;

public interface MarkTagRepository extends CrudRepository<MarkTag, Long> {
}