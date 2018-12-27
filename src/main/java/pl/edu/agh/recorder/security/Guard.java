package pl.edu.agh.recorder.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pl.edu.agh.recorder.entity.ApplicationUser;
import pl.edu.agh.recorder.entity.Mark;
import pl.edu.agh.recorder.entity.Permission;
import pl.edu.agh.recorder.entity.Recording;
import pl.edu.agh.recorder.exception.application.RecordingDoesNotExistException;
import pl.edu.agh.recorder.repository.MarkRepository;
import pl.edu.agh.recorder.repository.PermissionRepository;
import pl.edu.agh.recorder.repository.RecordingRepository;

import java.util.Optional;

@Component
public class Guard {

    @Autowired
    private MarkRepository markRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RecordingRepository recordingRepository;

    public boolean checkUserCanAccessRecording(Authentication authentication, Long id) {
        Optional<Permission> permission = permissionRepository.findByUserAndRecording_Id(
                (ApplicationUser) authentication.getPrincipal(), id);

        return permission.isPresent();
    }

    public boolean checkUserOwnsRecording(Authentication authentication, Long id) {
        Optional<Recording> recording = recordingRepository.findById(id);

        if (!recording.isPresent()) return false;
        return recording.get().getUser().getId()
                .equals(((ApplicationUser) authentication.getPrincipal()).getId());
    }

    public boolean checkUserCanAccessMark(Authentication authentication, Long id) throws RecordingDoesNotExistException {
        Optional<Recording> recording = recordingRepository.findByMarkId(id);

        if (!recording.isPresent()) throw new RecordingDoesNotExistException();
        return checkUserCanAccessRecording(authentication, recording.get().getId());
    }

    public boolean checkUserOwnsMark(Authentication authentication, Long id) throws RecordingDoesNotExistException {
        Optional<Mark> mark = markRepository.findById(id);
        if (!mark.isPresent()) return false;
        if (mark.get().getUser().getId()
                .equals(((ApplicationUser) authentication.getPrincipal()).getId())) {
            return true;
        }

        Optional<Recording> recording = recordingRepository.findByMarkId(id);
        if (!recording.isPresent()) throw new RecordingDoesNotExistException();
        return recording.get().getUser().getId()
                .equals(((ApplicationUser) authentication.getPrincipal()).getId());
    }
}