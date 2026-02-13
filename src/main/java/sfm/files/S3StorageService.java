package sfm.files;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.*;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.*;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.net.URI;
import java.time.Duration;

@Service
public class S3StorageService {

    private final String bucket;
    private final S3Client s3;
    private final S3Presigner presigner;

    public S3StorageService(
            @Value("${sfm.s3.bucket}") String bucket,
            @Value("${sfm.s3.region}") String region,
            @Value("${sfm.s3.endpoint}") String endpoint,
            @Value("${sfm.s3.accessKey}") String accessKey,
            @Value("${sfm.s3.secretKey}") String secretKey
    ) {
        this.bucket = bucket;

        var creds = StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
        var r = Region.of(region);
        var ep = URI.create(endpoint);

        this.s3 = S3Client.builder()
                .credentialsProvider(creds)
                .region(r)
                .endpointOverride(ep)
                .forcePathStyle(true)   // important for LocalStack
                .build();

        this.presigner = S3Presigner.builder()
                .credentialsProvider(creds)
                .region(r)
                .endpointOverride(ep)
                .build();
    }

    public void putObject(String key, byte[] bytes, String contentType) {
        s3.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType(contentType)
                        .build(),
                RequestBody.fromBytes(bytes)
        );
    }

    public String presignedGetUrl(String key) {
        var getReq = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        var presignReq = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getReq)
                .build();

        return presigner.presignGetObject(presignReq).url().toString();
    }

    public void deleteObject(String key) {
        s3.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());
    }
}
