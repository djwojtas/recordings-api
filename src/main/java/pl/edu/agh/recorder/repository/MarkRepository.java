package pl.edu.agh.recorder.repository;

import org.springframework.data.repository.CrudRepository;
import pl.edu.agh.recorder.entity.Mark;

import java.util.List;

public interface MarkRepository extends CrudRepository<Mark, Long> {
    List<Mark> findAllByRecording_Id(Long recordingId);

    void deleteAllByRecording_Id(Long recordingId);
}