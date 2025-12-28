package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.BytesWrapper;
import ru.yandex.practicum.dto.response.ImageInfo;
import ru.yandex.practicum.config.properties.AwsProperties;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import ru.yandex.practicum.exception.S3ConnectionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {
    private final S3AsyncClient s3Client;
    private final AwsProperties awsProperties;

    @Override
    public Mono<ImageInfo> uploadImage(String fileName, byte[] image) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(awsProperties.getBucketName())
                .key(fileName)
                .contentLength((long) image.length)
                .build();

        return Mono.fromFuture(s3Client.putObject(putObjectRequest, AsyncRequestBody.fromBytes(image)))
                .then(buildImageInfo(fileName))
                .doOnSubscribe(it -> log.debug("Uploading image: {}", fileName))
                .doOnSuccess(it -> log.debug("Successfully uploaded image: {}", fileName))
                .onErrorMap(e -> new S3ConnectionException("Upload failed: " + e.getMessage(), e));
    }

    @Override
    public Mono<byte[]> downloadImage(String fileName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(awsProperties.getBucketName())
                .key(fileName)
                .build();

        return Mono.fromFuture(s3Client.getObject(getObjectRequest, AsyncResponseTransformer.toBytes()))
                .map(BytesWrapper::asByteArray)
                .doOnSubscribe(it -> log.debug("Downloading image: {}", fileName))
                .doOnSuccess(it -> log.debug("Successfully downloaded image: {}", fileName))
                .onErrorMap(e -> new S3ConnectionException("Download failed: " + e.getMessage(), e));
    }

    @Override
    public Mono<Void> deleteImage(String fileName) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(awsProperties.getBucketName())
                .key(fileName)
                .build();

        return Mono.fromFuture(s3Client.deleteObject(deleteObjectRequest))
                .doOnSubscribe(it -> log.debug("Deleting image: {}", fileName))
                .doOnSuccess(it -> log.debug("Successfully deleted image: {}", fileName))
                .doOnError(e -> log.error("Failed to delete image: {}", fileName, e))
                .then()
                .onErrorMap(e -> new S3ConnectionException(e.getMessage(), e));
    }

    private Mono<ImageInfo> buildImageInfo(String fileName) {
        return Mono.fromCallable(() -> {
            String imageUrl = String.format("%s/%s/%s",
                    awsProperties.getServiceEndpoint(),
                    awsProperties.getBucketName(),
                    fileName);

            return ImageInfo.builder()
                    .fileName(fileName)
                    .imageUrl(imageUrl)
                    .build();
        });
    }
}