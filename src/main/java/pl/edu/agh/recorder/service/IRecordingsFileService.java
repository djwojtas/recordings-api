package pl.edu.agh.recorder.service;

import org.springframework.web.multipart.MultipartFile;
import pl.edu.agh.recorder.exception.application.InvalidFileFormatException;
import ws.schild.jave.EncoderException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface IRecordingsFileService {

    void downloadRecording(String fileName, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException;

    String uploadRecording(MultipartFile file) throws IOException, InvalidFileFormatException, EncoderException;

    long getMultimediaObjectDuration(MultipartFile file) throws IOException, EncoderException;

    String getUniqueFileName();

    void checkIfFileIsMp4(MultipartFile file) throws IOException, InvalidFileFormatException;

    void deleteRecording(String fileName);
}
