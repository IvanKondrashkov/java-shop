package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import ru.yandex.practicum.dto.response.ImageInfo;
import org.apache.http.entity.ContentType;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import ru.yandex.practicum.config.properties.AwsProperties;
import ru.yandex.practicum.exception.S3ConnectionException;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {
    private final AmazonS3 s3Client;
    private final AwsProperties awsProperties;

    @Override
    public ImageInfo uploadImage(String fileName, byte[] image) {
        try (var is = new ByteArrayInputStream(image)) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(image.length);
            metadata.setContentType(ContentType.IMAGE_PNG.getMimeType());

            s3Client.putObject(awsProperties.getBucketName(), fileName, is, metadata);
        } catch (IOException e) {
            throw new S3ConnectionException(e.getMessage(), e);
        }
        return ImageInfo.builder()
                .fileName(fileName)
                .imageUrl(s3Client.getUrl(awsProperties.getBucketName(), fileName).toExternalForm())
                .build();
    }

    @Override
    public byte[] downloadImage(String fileName) {
        try {
            return s3Client.getObject(awsProperties.getBucketName(), fileName).getObjectContent().readAllBytes();
        } catch (IOException e) {
            throw new S3ConnectionException(e.getMessage(), e);
        }
    }

    @Override
    public void deleteImage(String fileName) {
        s3Client.deleteObject(awsProperties.getBucketName(), fileName);
    }
}