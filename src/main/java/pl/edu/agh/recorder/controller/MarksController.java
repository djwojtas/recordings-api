package pl.edu.agh.recorder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.recorder.entity.Comment;
import pl.edu.agh.recorder.entity.Mark;
import pl.edu.agh.recorder.exception.application.MarkDoesNotExistException;
import pl.edu.agh.recorder.mapper.CommentMapper;
import pl.edu.agh.recorder.mapper.MarkMapper;
import pl.edu.agh.recorder.message.DefaultResponse;
import pl.edu.agh.recorder.rest.CommentPostRequest;
import pl.edu.agh.recorder.rest.CommentResponse;
import pl.edu.agh.recorder.rest.MarkResponse;
import pl.edu.agh.recorder.rest.MarkUpdateRequest;
import pl.edu.agh.recorder.service.ICommentService;
import pl.edu.agh.recorder.service.IMarksService;
import pl.edu.agh.recorder.service.impl.AuthenticationService;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/marks")
public class MarksController {

    @Autowired
    private ICommentService commentService;

    @Autowired
    private IMarksService marksService;

    @Autowired
    private MarkMapper markMapper;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CommentMapper commentMapper;

    @PostMapping("/{markId}/comments")
    public CommentResponse postComment(@PathVariable("markId") Long markId, @RequestBody CommentPostRequest commentPostRequest) throws MarkDoesNotExistException {
        Mark mark = marksService.getMark(markId);
        return commentMapper.toResponse(commentService.addComment(mark, Comment.builder()
                .comment(commentPostRequest.getComment())
                .commentTime(new Timestamp(new Date().getTime()))
                .user(authenticationService.getAuthenticatedUser())
                .build()));
    }

    @GetMapping("/{markId}/comments")
    public List<CommentResponse> getComments(@PathVariable("markId") Long markId, @RequestBody CommentPostRequest commentPostRequest) throws MarkDoesNotExistException {
        return commentMapper.toResponseList(commentService.getComments(markId));
    }

    @PutMapping
    public MarkResponse updateMark(@RequestBody MarkUpdateRequest markUpdateRequest) throws MarkDoesNotExistException {
        Mark mark = marksService.getMark(markUpdateRequest.getId());

        mark.setName(markUpdateRequest.getName());
        mark.setMarkTime(markUpdateRequest.getMarkTime());

        return markMapper.toResponse(marksService.updateMark(mark));
    }

    @DeleteMapping("/{id}")
    public DefaultResponse deleteMark(@PathVariable("id") Long id) throws MarkDoesNotExistException {
        Mark mark = marksService.getMark(id);
        marksService.deleteMark(mark);
        return new DefaultResponse("success");
    }
}
