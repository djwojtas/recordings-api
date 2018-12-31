package pl.edu.agh.recorder.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.agh.recorder.exception.application.InvalidFileFormatException;
import pl.edu.agh.recorder.exception.application.RecordingDoesNotExistException;
import ws.schild.jave.EncoderException;

import java.io.IOException;

public interface IRecordingsFileService {

    Resource downloadRecording(String fileName) throws RecordingDoesNotExistException;

    String uploadRecording(MultipartFile file) throws IOException, InvalidFileFormatException, EncoderException;

    long getMultimediaObjectDuration(MultipartFile file) throws IOException, EncoderException;

    String getUniqueFileName();

    void checkIfFileIsMp4(MultipartFile file) throws IOException, InvalidFileFormatException;

    void deleteRecording(String fileName);
}
