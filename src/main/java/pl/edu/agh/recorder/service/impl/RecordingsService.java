package pl.edu.agh.recorder.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.agh.recorder.entity.Permission;
import pl.edu.agh.recorder.entity.Recording;
import pl.edu.agh.recorder.exception.application.InvalidFileFormatException;
import pl.edu.agh.recorder.exception.application.RecordingDoesNotExistException;
import pl.edu.agh.recorder.repository.PermissionRepository;
import pl.edu.agh.recorder.repository.RecordingRepository;
import pl.edu.agh.recorder.service.*;
import ws.schild.jave.EncoderException;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class RecordingsService implements IRecordingsService {

    @Autowired
    private IRecordingsFileService fileService;

    @Autowired
    private IAuthenticationService authenticationService;

    @Autowired
    private RecordingRepository recordingRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private IMarksService marksService;

    @Autowired
    private IPermissionService permissionService;

    @Autowired
    private ICommentService commentService;

    @Autowired
    private ITagService tagService;

    @Override
    public List<Recording> getUserRecordings() {
        return recordingRepository.findByUser(authenticationService.getAuthenticatedUser());
    }

    @Override
    @PreAuthorize("@guard.checkUserCanAccessRecording(authentication, #id)")
    public Recording getRecordingById(Long id) throws RecordingDoesNotExistException {
        Optional<Recording> recording = recordingRepository.findById(id);

        if (!recording.isPresent()) throw new RecordingDoesNotExistException();
        return recording.get();
    }

    @Override
    public Recording uploadRecording(MultipartFile file) throws IOException, InvalidFileFormatException, EncoderException {
        fileService.checkIfFileIsMp3(file);

        long duration = fileService.getMp3Duration(file);
        String fileName = fileService.uploadRecording(file);

        Recording recording = Recording.builder()
                .title("Untitled")
                .duration(duration)
                .uploadTime(new Timestamp(new Date().getTime()))
                .user(authenticationService.getAuthenticatedUser())
                .fileName(fileName)
                .build();

        recordingRepository.save(recording);
        permissionRepository.save(
                Permission.builder()
                        .recording(recording)
                        .user(authenticationService.getAuthenticatedUser())
                        .build());
        return recording;
    }

    @Override
    @PreAuthorize("@guard.checkUserOwnsRecording(authentication, #recording.id)")
    public Recording updateRecording(Recording recording) {
        return recordingRepository.save(recording);
    }

    @Override
    @PreAuthorize("@guard.checkUserOwnsRecording(authentication, #recording.id)")
    public void deleteRecording(Recording recording) {
        fileService.deleteRecording(recording.getFileName());
        commentService.deleteCommentsRelatedToRecording(recording.getId());
        tagService.deleteRecordingTags(recording.getId());
        marksService.deleteMarksRelatedToRecording(recording.getId());
        permissionService.revokePermissionsRelatedToRecording(recording.getId());
        recordingRepository.delete(recording);
    }
}