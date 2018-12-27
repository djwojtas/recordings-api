package pl.edu.agh.recorder.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import pl.edu.agh.recorder.security.SecurityConstants;

@SpringBootApplication(scanBasePackages = {"pl.edu.agh.recorder"})
@EnableJpaRepositories("pl.edu.agh.recorder.repository")
@EntityScan(basePackages = "pl.edu.agh.recorder.entity")
@EnableConfigurationProperties(SecurityConstants.class)
public class AppConfig {

    @Autowired
    SecurityConstants securityConstants;

    public static void main(String[] args) {
        SpringApplication.run(AppConfig.class, args);
    }

    @Bean
    AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder.standard()
                .withRegion(Regions.EU_CENTRAL_1)
                .withCredentials(new AWSStaticCredentialsProvider(securityConstants.getAwsCredentials()))
                .build();
    }
}
