package pl.edu.agh.recorder.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import pl.edu.agh.recorder.entity.ApplicationUser;
import pl.edu.agh.recorder.entity.Permission;
import pl.edu.agh.recorder.entity.Recording;
import pl.edu.agh.recorder.exception.application.PermissionAlreadyGrantedException;
import pl.edu.agh.recorder.exception.application.PermissionDoesNotExistException;
import pl.edu.agh.recorder.exception.application.UserNotFoundException;
import pl.edu.agh.recorder.mapper.PermissionMapper;
import pl.edu.agh.recorder.repository.ApplicationUserRepository;
import pl.edu.agh.recorder.repository.PermissionRepository;
import pl.edu.agh.recorder.rest.PermissionResponse;
import pl.edu.agh.recorder.service.IPermissionService;

import java.util.List;
import java.util.Optional;

@Service
public class PermissionService implements IPermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    @PreAuthorize("@guard.checkUserOwnsRecording(authentication, #recording.id)")
    public void grantPermission(Recording recording, Long userId) throws UserNotFoundException, PermissionAlreadyGrantedException {
        Optional<ApplicationUser> targetUser = applicationUserRepository.findById(userId);
        if (!targetUser.isPresent()) throw new UserNotFoundException();

        Optional<Permission> existingPermission = permissionRepository.findByUserAndRecording_Id(targetUser.get(), recording.getId());
        if (existingPermission.isPresent()) throw new PermissionAlreadyGrantedException();

        permissionRepository.save(Permission.builder()
                .user(targetUser.get())
                .recording(recording)
                .build());
    }

    @Override
    @PreAuthorize("@guard.checkUserOwnsRecording(authentication, #recording.id)")
    public void revokePermission(Recording recording, Long userId) throws UserNotFoundException, PermissionDoesNotExistException {
        Optional<ApplicationUser> targetUser = applicationUserRepository.findById(userId);
        if (!targetUser.isPresent()) throw new UserNotFoundException();

        Optional<Permission> existingPermission = permissionRepository.findByUserAndRecording_Id(targetUser.get(), recording.getId());
        if (existingPermission.isPresent()) throw new PermissionDoesNotExistException();

        permissionRepository.delete(existingPermission.get());
    }


    @Override
    @PreAuthorize("@guard.checkUserOwnsRecording(authentication, #recordingId)")
    public void revokePermissionsRelatedToRecording(Long recordingId) {
        permissionRepository.deleteAllByRecording_Id(recordingId);
    }

    @Override
    @PreAuthorize("@guard.checkUserOwnsRecording(authentication, #recordingId)")
    public List<PermissionResponse> getPermissionsToRecording(Long recordingId) {
        return permissionMapper.toResponseList(permissionRepository.findAllByRecording_Id(recordingId));
    }
}
