package pl.edu.agh.recorder.service;

import org.springframework.security.access.prepost.PreAuthorize;
import pl.edu.agh.recorder.entity.Mark;
import pl.edu.agh.recorder.exception.application.MarkDoesNotExistException;

import java.util.List;

public interface IMarksService {
    @PreAuthorize("@guard.checkUserCanAccessRecording(authentication, #mark.id)")
    Mark createMark(Mark mark);

    @PreAuthorize("@guard.checkUserCanAccessMark(authentication, #id)")
    Mark getMark(Long id) throws MarkDoesNotExistException;

    @PreAuthorize("@guard.checkUserCanAccessRecording(authentication, #id)")
    List<Mark> getRecordingMarks(Long id);

    @PreAuthorize("@guard.checkUserOwnsMark(authentication, #mark.id)")
    Mark updateMark(Mark mark);

    @PreAuthorize("@guard.checkUserOwnsMark(authentication, #mark.id)")
    void deleteMark(Mark mark);

    @PreAuthorize("@guard.checkUserOwnsRecording(authentication, #recordingId)")
    void deleteMarksRelatedToRecording(Long recordingId);
}
