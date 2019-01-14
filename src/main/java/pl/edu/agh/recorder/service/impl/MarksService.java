package pl.edu.agh.recorder.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import pl.edu.agh.recorder.entity.Mark;
import pl.edu.agh.recorder.exception.application.MarkDoesNotExistException;
import pl.edu.agh.recorder.repository.MarkRepository;
import pl.edu.agh.recorder.service.IAuthenticationService;
import pl.edu.agh.recorder.service.ICommentService;
import pl.edu.agh.recorder.service.IMarksService;

import java.util.List;
import java.util.Optional;

@Service
public class MarksService implements IMarksService {

    @Autowired
    private ICommentService commentService;

    @Autowired
    private IAuthenticationService authenticationService;

    @Autowired
    private MarkRepository markRepository;

    @Override
    @PreAuthorize("@guard.checkUserCanAccessRecording(authentication, #mark.recording.id)")
    public Mark createMark(Mark mark) {
        return markRepository.save(mark);
    }

    @Override
    @PreAuthorize("@guard.checkUserCanAccessMark(authentication, #id)")
    public Mark getMark(Long id) throws MarkDoesNotExistException {
        Optional<Mark> mark = markRepository.findById(id);

        if (!mark.isPresent()) throw new MarkDoesNotExistException();
        return mark.get();
    }

    @Override
    @PreAuthorize("@guard.checkUserCanAccessRecording(authentication, #id)")
    public List<Mark> getRecordingMarks(Long id) {
        return markRepository.findAllByRecording_Id(id);
    }

    @Override
    @PreAuthorize("@guard.checkUserOwnsMark(authentication, #mark.id)")
    public Mark updateMark(Mark mark) {
        return markRepository.save(mark);
    }

    @Override
    @PreAuthorize("@guard.checkUserOwnsMark(authentication, #mark.id)")
    public void deleteMark(Mark mark) {
        commentService.deleteCommentsRelatedToMark(mark.getId());
        markRepository.delete(mark);
    }

    @Override
    @PreAuthorize("@guard.checkUserOwnsRecording(authentication, #recordingId)")
    public void deleteMarksRelatedToRecording(Long recordingId) {
        commentService.deleteCommentsRelatedToRecording(recordingId);
        markRepository.deleteAllByRecording_Id(recordingId);
    }
}
