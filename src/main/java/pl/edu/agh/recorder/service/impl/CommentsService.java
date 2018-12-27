package pl.edu.agh.recorder.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import pl.edu.agh.recorder.entity.Comment;
import pl.edu.agh.recorder.entity.Mark;
import pl.edu.agh.recorder.repository.CommentRepository;
import pl.edu.agh.recorder.service.IAuthenticationService;
import pl.edu.agh.recorder.service.ICommentService;

import java.util.List;

@Service
public class CommentsService implements ICommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private IAuthenticationService authenticationService;

    @Override
    @PreAuthorize("@guard.checkUserCanAccessMark(authentication, #mark.id)")
    public Comment addComment(Mark mark, Comment comment) {
        comment.setMark(mark);
        return commentRepository.save(comment);
    }

    @Override
    @PreAuthorize("@guard.checkUserCanAccessMark(authentication, #markId)")
    public List<Comment> getComments(Long markId) {
        return commentRepository.findAllByMark_Id(markId);
    }

    @Override
    @PreAuthorize("@guard.checkUserOwnsMark(authentication, #markId)")
    public void deleteCommentsRelatedToMark(Long markId) {
        commentRepository.deleteAllByMark_Id(markId);
    }

    @Override
    @PreAuthorize("@guard.checkUserOwnsRecording(authentication, #recordingId)")
    public void deleteCommentsRelatedToRecording(Long recordingId) {
        commentRepository.deleteAllRelatedToRecording(recordingId);
    }
}
