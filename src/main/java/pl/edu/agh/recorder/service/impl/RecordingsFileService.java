package pl.edu.agh.recorder.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.agh.recorder.exception.application.InvalidFileFormatException;
import pl.edu.agh.recorder.repository.RecordingRepository;
import pl.edu.agh.recorder.security.SecurityConstants;
import pl.edu.agh.recorder.service.IRecordingsFileService;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;

import java.io.File;
import java.io.IOException;

@Service
public class RecordingsFileService implements IRecordingsFileService {

    @Autowired
    private AmazonS3 s3Client;

    @Autowired
    private SecurityConstants securityConstants;

    private final static String fileNameFormat = "recordings/%s";

    @Override
    public Resource downloadRecording(String fileName) {
        String filePath = String.format(fileNameFormat, fileName);

        S3Object object = s3Client.getObject(new GetObjectRequest(securityConstants.getAwsBucketName(), filePath));
        return new InputStreamResource(object.getObjectContent());
    }

    @Override
    public String uploadRecording(MultipartFile file) throws IOException {
        String fileName = getUniqueFileName();
        String filePath = String.format(fileNameFormat, fileName);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());

        s3Client.putObject(
                securityConstants.getAwsBucketName(),
                filePath,
                file.getInputStream(),
                objectMetadata
        );

        return fileName;
    }

    @Override
    public long getMp3Duration(MultipartFile mp3File) throws IOException, EncoderException {
        String tmpPath = "./tmp/" + RandomStringUtils.randomAlphanumeric(32);
        File tmpFile = new File(tmpPath);
        FileUtils.writeByteArrayToFile(tmpFile, mp3File.getBytes());
        return new MultimediaObject(tmpFile).getInfo().getDuration();
    }

    @Override
    public String getUniqueFileName() {
        String filePath;
        do {
            filePath = RandomStringUtils.randomAlphanumeric(32);
        } while (s3Client.doesObjectExist(securityConstants.getAwsBucketName(), filePath));

        return filePath;
    }

    @Override
    public void checkIfFileIsMp3(MultipartFile file) throws IOException, InvalidFileFormatException {
        byte[] bytes = file.getBytes();
        boolean isMp3v1 = bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0xFB;
        boolean isMp3v2 = bytes[0] == (byte) 0x49 && bytes[1] == (byte) 0x44 && bytes[2] == (byte) 0x33;
        boolean isMp3 = isMp3v1 || isMp3v2;
        if (!isMp3) throw new InvalidFileFormatException();
    }

    @Override
    public void deleteRecording(String fileName) {
        s3Client.deleteObject(new DeleteObjectRequest(
                securityConstants.getAwsBucketName(),
                String.format(fileNameFormat, fileName)));
    }
}