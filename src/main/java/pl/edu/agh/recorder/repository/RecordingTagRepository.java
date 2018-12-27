package pl.edu.agh.recorder.repository;

import org.springframework.data.repository.CrudRepository;
import pl.edu.agh.recorder.entity.RecordingTag;

import java.util.List;

public interface RecordingTagRepository extends CrudRepository<RecordingTag, Long> {
    List<RecordingTag> findAllByRecording_Id(Long recordingId);
}