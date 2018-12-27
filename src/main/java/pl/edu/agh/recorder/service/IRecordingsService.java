package pl.edu.agh.recorder.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.agh.recorder.entity.Recording;
import pl.edu.agh.recorder.exception.application.InvalidFileFormatException;
import pl.edu.agh.recorder.exception.application.RecordingDoesNotExistException;
import ws.schild.jave.EncoderException;

import java.io.IOException;
import java.util.List;

public interface IRecordingsService {
    List<Recording> getUserRecordings();

    @PreAuthorize("@guard.checkUserCanAccessRecording(authentication, #id)")
    Recording getRecordingById(Long id) throws RecordingDoesNotExistException;

    Recording uploadRecording(MultipartFile file) throws IOException, InvalidFileFormatException, EncoderException;

    @PreAuthorize("@guard.checkUserOwnsRecording(authentication, #recording.id)")
    Recording updateRecording(Recording recording);

    @PreAuthorize("@guard.checkUserOwnsRecording(authentication, #recording.id)")
    void deleteRecording(Recording recording);
}
