package pl.edu.agh.recorder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.agh.recorder.entity.ApplicationUser;
import pl.edu.agh.recorder.entity.Mark;
import pl.edu.agh.recorder.entity.Recording;
import pl.edu.agh.recorder.exception.application.*;
import pl.edu.agh.recorder.mapper.MarkMapper;
import pl.edu.agh.recorder.mapper.RecordingMapper;
import pl.edu.agh.recorder.mapper.TagMapper;
import pl.edu.agh.recorder.message.DefaultResponse;
import pl.edu.agh.recorder.rest.*;
import pl.edu.agh.recorder.security.UserPrincipal;
import pl.edu.agh.recorder.service.*;
import pl.edu.agh.recorder.service.impl.AuthenticationService;
import pl.edu.agh.recorder.service.impl.CustomUserDetailsService;
import ws.schild.jave.EncoderException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/recordings")
public class RecordingsController {

    @Autowired
    private IRecordingsService recordingsService;

    @Autowired
    private IRecordingsFileService recordingsFileService;

    @Autowired
    private RecordingMapper recordingMapper;

    @Autowired
    private IPermissionService permissionService;

    @Autowired
    private IMarksService marksService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private MarkMapper markMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private ITagService tagService;

    @PostMapping("/upload")
    public RecordingResponse recordingUpload(@RequestParam("file") MultipartFile file) throws IOException, InvalidFileFormatException, EncoderException {
        return recordingMapper.toResponse(recordingsService.uploadRecording(file));
    }

    @GetMapping("/download/{id}")
    public void recordingUpload(@PathVariable("id") Long id, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws RecordingDoesNotExistException, IOException {
        Recording recording = recordingsService.getRecordingById(id);
        recordingsFileService.downloadRecording(recording.getFileName(), httpRequest, httpResponse);
    }

    @GetMapping
    public List<RecordingResponse> getRecordings() {
        return recordingMapper.toResponseList(recordingsService.getUserRecordings());
    }

    @GetMapping("/{id}")
    public RecordingResponse getRecording(@PathVariable("id") Long id) throws RecordingDoesNotExistException {
        return recordingMapper.toResponse(recordingsService.getRecordingById(id));
    }


    @PutMapping
    public RecordingResponse updateRecording(@RequestBody RecordingUpdateRequest recordingUpdateRequest) throws RecordingDoesNotExistException {
        Recording recording = recordingsService.getRecordingById(recordingUpdateRequest.getId());
        recording.setTitle(recordingUpdateRequest.getTitle());
        return recordingMapper.toResponse(recordingsService.updateRecording(recording));
    }

    @DeleteMapping("/{id}")
    public DefaultResponse deleteRecording(@PathVariable("id") Long id) throws RecordingDoesNotExistException {
        Recording recording = recordingsService.getRecordingById(id);
        recordingsService.deleteRecording(recording);
        return new DefaultResponse("success");
    }

    @PostMapping("/{recordingId}/permissions/{username}")
    public DefaultResponse grantPermission(@PathVariable("recordingId") Long recordingId, @PathVariable("username") String username) throws RecordingDoesNotExistException, UserNotFoundException, PermissionAlreadyGrantedException {
        ApplicationUser user = userDetailsService.loadUserByUsername(username).getApplicationUser();
        Recording recording = recordingsService.getRecordingById(recordingId);
        permissionService.grantPermission(recording, user.getId());
        return new DefaultResponse("success");
    }

    @DeleteMapping("/{recordingId}/permissions/{username}")
    public DefaultResponse revokePermission(@PathVariable("recordingId") Long recordingId, @PathVariable("username") String username) throws RecordingDoesNotExistException, UserNotFoundException, PermissionDoesNotExistException {
        ApplicationUser user = userDetailsService.loadUserByUsername(username).getApplicationUser();
        Recording recording = recordingsService.getRecordingById(recordingId);
        permissionService.revokePermission(recording, user.getId());
        return new DefaultResponse("success");
    }

    @GetMapping("/{recordingId}/permissions")
    public List<PermissionResponse> getPermissionsToRecording(@PathVariable("recordingId") Long recordingId) {
        return permissionService.getPermissionsToRecording(recordingId);
    }

    @PostMapping("/{recordingId}/marks")
    public List<MarkResponse> postMarks(@RequestBody List<MarkPostRequest> markPostRequests, @PathVariable("recordingId") Long recordingId) throws RecordingDoesNotExistException {
        Recording recording = recordingsService.getRecordingById(recordingId);

        return markPostRequests.stream()
                .map(request -> marksService.createMark(Mark.builder()
                        .name(request.getName())
                        .markTime(request.getMarkTime())
                        .recording(recording)
                        .user(authenticationService.getAuthenticatedUser())
                        .build()))
                .map(mark -> markMapper.toResponse(mark))
                .collect(Collectors.toList());
    }

    @GetMapping("/{recordingId}/marks")
    public List<MarkResponse> getMarks(@PathVariable("recordingId") Long recordingId) {
        return markMapper.toResponseList(marksService.getRecordingMarks(recordingId));
    }

    @PostMapping("/{recordingId}/tags")
    public List<TagResponse> addTagsToRecording(@PathVariable("recordingId") Long recordingId, @RequestBody List<TagPostRequest> tagPostRequests) throws RecordingDoesNotExistException {
        Recording recording = recordingsService.getRecordingById(recordingId);
        List<String> stringTags = tagPostRequests.stream().map(TagPostRequest::getTag).collect(Collectors.toList());

        return tagMapper.toRecordingTagResponseList(tagService.addTagsToRecording(recording, stringTags));
    }

    @GetMapping("/{recordingId}/tags")
    public List<TagResponse> getRecordingTags(@PathVariable("recordingId") Long recordingId) {
        return tagMapper.toRecordingTagResponseList(tagService.getRecordingTags(recordingId));
    }

    @DeleteMapping("/{recordingId}/tags/{tagId}")
    public DefaultResponse deleteTagFromRecording(@PathVariable("recordingId") Long recordingId, @PathVariable("tagId") Long tagId) {
        tagService.deleteRecordingTag(recordingId, tagId);
        return new DefaultResponse("success");
    }
}
