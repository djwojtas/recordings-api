package pl.edu.agh.recorder.security;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("recordings.security")
public class SecurityConstants {
    @Getter
    @Setter
    private String secret = "d3Rm";
    @Getter
    @Setter
    private String tokenPrefix = "Bearer ";
    @Getter
    @Setter
    private String headerString = "Authorization";
    @Getter
    @Setter
    private long expirationTime = 864000000L;
    @Getter
    @Setter
    private String awsAccessKeyId;
    @Getter
    @Setter
    private String awsSecretKey;
    @Getter
    @Setter
    private String awsBucketName = "meeting-recordings-storage";
    @Setter
    private AWSCredentials awsCredentials;

    public AWSCredentials getAwsCredentials() {
        if (awsCredentials == null) {
            awsCredentials = new BasicAWSCredentials(awsAccessKeyId, awsSecretKey);
        }
        return awsCredentials;
    }
}
