package pl.edu.agh.recorder.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import pl.edu.agh.recorder.entity.Comment;

import java.util.List;

public interface CommentRepository extends CrudRepository<Comment, Long> {
    List<Comment> findAllByMark_Id(Long markId);

    void deleteAllByMark_Id(Long markId);

    @Query("DELETE FROM Comment c WHERE c.id IN (SELECT m.id FROM Mark m WHERE m.recording.id = :recordingId)")
    void deleteAllRelatedToRecording(@Param("recordingId") Long recordingId);
}