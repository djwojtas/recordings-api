package pl.edu.agh.recorder.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import pl.edu.agh.recorder.entity.Recording;
import pl.edu.agh.recorder.entity.RecordingTag;
import pl.edu.agh.recorder.entity.Tag;
import pl.edu.agh.recorder.exception.application.TagAlreadyExistsException;
import pl.edu.agh.recorder.exception.application.TagDoesNotExistException;
import pl.edu.agh.recorder.repository.RecordingTagRepository;
import pl.edu.agh.recorder.repository.TagRepository;
import pl.edu.agh.recorder.service.ITagService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TagService implements ITagService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private RecordingTagRepository recordingTagRepository;

    @Override
    public Tag addTag(String tag) throws TagAlreadyExistsException {
        Optional<Tag> existingTag = tagRepository.findByTag(tag);

        if (existingTag.isPresent()) throw new TagAlreadyExistsException();
        return tagRepository.save(Tag.builder().tag(tag).build());
    }

    @Override
    @PreAuthorize("@guard.checkUserOwnsRecording(authentication, #recording.id)")
    public List<RecordingTag> addTagsToRecording(Recording recording, List<String> tags) {
        return tags.stream()
                .map(tag -> tagRepository.findByTag(tag))
                .map(tag -> tag.orElseThrow(TagDoesNotExistException::new))
                .map(tag -> recordingTagRepository.save(RecordingTag.builder()
                        .recording(recording)
                        .tag(tag)
                        .build()))
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("@guard.checkUserCanAccessRecording(authentication, #recordingId)")
    public List<RecordingTag> getRecordingTags(Long recordingId) {
        return recordingTagRepository.findAllByRecording_Id(recordingId);
    }

    @Override
    @PreAuthorize("@guard.checkUserOwnsRecording(authentication, #recordingId)")
    public void deleteRecordingTags(Long recordingId) {
        List<RecordingTag> recordingTags = getRecordingTags(recordingId);
        recordingTags.forEach(tag -> recordingTagRepository.delete(tag));
    }

    @Override
    @PreAuthorize("@guard.checkUserOwnsRecording(authentication, #recordingId)")
    public void deleteRecordingTag(Long recordingId, Long tagId) {
        Optional<RecordingTag> tag = recordingTagRepository.findById(tagId);
        if (!tag.isPresent()) throw new TagDoesNotExistException();

        recordingTagRepository.delete(tag.get());
    }
}
