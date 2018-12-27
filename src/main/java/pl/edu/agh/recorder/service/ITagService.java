package pl.edu.agh.recorder.service;

import org.springframework.security.access.prepost.PreAuthorize;
import pl.edu.agh.recorder.entity.Recording;
import pl.edu.agh.recorder.entity.RecordingTag;
import pl.edu.agh.recorder.entity.Tag;
import pl.edu.agh.recorder.exception.application.TagAlreadyExistsException;

import java.util.List;

public interface ITagService {
    Tag addTag(String tag) throws TagAlreadyExistsException;

    @PreAuthorize("@guard.checkUserOwnsRecording(authentication, #recording.id)")
    List<RecordingTag> addTagsToRecording(Recording recording, List<String> tags);

    @PreAuthorize("@guard.checkUserCanAccessRecording(authentication, #recordingId)")
    List<RecordingTag> getRecordingTags(Long recordingId);

    @PreAuthorize("@guard.checkUserOwnsRecording(authentication, #recordingId)")
    void deleteRecordingTags(Long recordingId);

    @PreAuthorize("@guard.checkUserOwnsRecording(authentication, #recordingId)")
    void deleteRecordingTag(Long recordingId, Long tagId);
}
