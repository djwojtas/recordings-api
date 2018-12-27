package pl.edu.agh.recorder.service;

import org.springframework.security.access.prepost.PreAuthorize;
import pl.edu.agh.recorder.entity.Comment;
import pl.edu.agh.recorder.entity.Mark;

import java.util.List;

public interface ICommentService {
    @PreAuthorize("@guard.checkUserCanAccessRecording(authentication, #recording.id)")
    Comment addComment(Mark recording, Comment comment);

    @PreAuthorize("@guard.checkUserCanAccessMark(authentication, #markId)")
    List<Comment> getComments(Long markId);

    @PreAuthorize("@guard.checkUserOwnsMark(authentication, #markId)")
    void deleteCommentsRelatedToMark(Long markId);

    @PreAuthorize("@guard.checkUserOwnsRecording(authentication, #recordingId)")
    void deleteCommentsRelatedToRecording(Long recordingId);
}
