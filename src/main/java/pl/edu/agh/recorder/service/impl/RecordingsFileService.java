package pl.edu.agh.recorder.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.agh.recorder.exception.application.InvalidFileFormatException;
import pl.edu.agh.recorder.security.SecurityConstants;
import pl.edu.agh.recorder.service.IRecordingsFileService;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@Service
public class RecordingsFileService implements IRecordingsFileService {

    @Autowired
    private AmazonS3 s3Client;

    @Autowired
    private SecurityConstants securityConstants;

    private final static String fileNameFormat = "recordings/%s";
    private final static byte[] mp4MagicBytes = new byte[] {0x66, 0x74, 0x79, 0x70, 0x6d, 0x70};
    private final static int[] magicBytesRange = new int[] {4, 10};

    @Override
    public void downloadRecording(String fileName, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
        String filePath = String.format(fileNameFormat, fileName);
        MultipartAWSFileSender fileSender = new MultipartAWSFileSender(filePath, s3Client, securityConstants.getAwsBucketName(), fileName)
                .with(httpRequest)
                .with(httpResponse);

        fileSender.serveResource();
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
    public long getMultimediaObjectDuration(MultipartFile file) throws IOException, EncoderException {
        String tmpPath = "./tmp/" + RandomStringUtils.randomAlphanumeric(32);
        File tmpFile = new File(tmpPath);
        FileUtils.writeByteArrayToFile(tmpFile, file.getBytes());
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
    public void checkIfFileIsMp4(MultipartFile file) throws IOException, InvalidFileFormatException {
        byte[] bytes = file.getBytes();
        byte[] mediaInfoHeader = Arrays.copyOfRange(bytes, magicBytesRange[0], magicBytesRange[1]);

        boolean isMp4 = Arrays.equals(mediaInfoHeader, mp4MagicBytes);

        if (!isMp4) throw new InvalidFileFormatException();
    }

    @Override
    public void deleteRecording(String fileName) {
        s3Client.deleteObject(new DeleteObjectRequest(
                securityConstants.getAwsBucketName(),
                String.format(fileNameFormat, fileName)));
    }
}