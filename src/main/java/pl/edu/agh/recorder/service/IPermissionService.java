package pl.edu.agh.recorder.service;

import org.springframework.security.access.prepost.PreAuthorize;
import pl.edu.agh.recorder.entity.Recording;
import pl.edu.agh.recorder.exception.application.PermissionAlreadyGrantedException;
import pl.edu.agh.recorder.exception.application.PermissionDoesNotExistException;
import pl.edu.agh.recorder.exception.application.UserNotFoundException;
import pl.edu.agh.recorder.rest.PermissionResponse;

import java.util.List;

public interface IPermissionService {

    @PreAuthorize("@guard.checkUserOwnsRecording(authentication, #recording.id)")
    void grantPermission(Recording recording, Long userId) throws UserNotFoundException, PermissionAlreadyGrantedException;

    @PreAuthorize("@guard.checkUserOwnsRecording(authentication, #recording.id)")
    void revokePermission(Recording recording, Long userId) throws UserNotFoundException, PermissionDoesNotExistException;

    @PreAuthorize("@guard.checkUserOwnsRecording(authentication, #recordingId)")
    void revokePermissionsRelatedToRecording(Long recordingId);

    @PreAuthorize("@guard.checkUserOwnsRecording(authentication, #recordingId)")
    List<PermissionResponse> getPermissionsToRecording(Long recordingId);
}
