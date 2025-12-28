package ru.yandex.practicum.service;

import java.util.*;
import java.time.Duration;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.ItemCsv;
import ru.yandex.practicum.mapper.ItemMapper;
import ru.yandex.practicum.mapper.ImageMapper;
import ru.yandex.practicum.model.Image;
import ru.yandex.practicum.repository.ItemRepository;
import ru.yandex.practicum.repository.ImageRepository;
import ru.yandex.practicum.exception.ImportCsvException;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final S3Service s3Service;
    private final ItemRepository itemRepository;
    private final ImageRepository imageRepository;
    private final CacheService cacheService;

    @Override
    public Mono<Void> importCsvFile(FilePart file) {
        return parseCsvFile(file)
                .flatMap(this::saveImages)
                .flatMap(this::saveItems);
    }

    private Mono<List<ItemCsv>> parseCsvFile(FilePart file) {
        return DataBufferUtils.join(file.content())
                .map(dataBuffer -> {
                    try (var reader = new BufferedReader(new InputStreamReader(dataBuffer.asInputStream()))) {
                        HeaderColumnNameMappingStrategy<ItemCsv> strategy = new HeaderColumnNameMappingStrategy<>();
                        strategy.setType(ItemCsv.class);

                        CsvToBean<ItemCsv> csvToBean = new CsvToBeanBuilder<ItemCsv>(reader)
                                .withMappingStrategy(strategy)
                                .withIgnoreLeadingWhiteSpace(true)
                                .withIgnoreEmptyLine(true)
                                .build();

                        List<ItemCsv> itemCsvs = csvToBean.parse();
                        log.info("Parsed {} items from CSV file", itemCsvs.size());
                        return itemCsvs;
                    } catch (Exception e) {
                        log.error("Error parsing CSV file: {}", e.getMessage());
                        throw new ImportCsvException(e.getMessage(), e);
                    }
                });
    }

    private Mono<List<ItemCsv>> saveImages(List<ItemCsv> itemCsvs) {
        return Flux.fromIterable(itemCsvs)
                .flatMap(itemCsv -> {
                    String fileName = getFileNameS3(itemCsv.getImageBase64());
                    byte[] bytes = Base64.getDecoder().decode(itemCsv.getImageBase64());

                    return s3Service.uploadImage(fileName, bytes)
                            .map(ImageMapper::imageInfoToImage)
                            .flatMap(imageRepository::save)
                            .flatMap(image -> cacheService.save("image", image.getFileName(), image, Duration.ofMinutes(3)))
                            .thenReturn(itemCsv);
                })
                .collectList();
    }

    private Mono<Void> saveItems(List<ItemCsv> itemCsvs) {
        return Flux.fromIterable(itemCsvs)
                .flatMap(itemCsv -> {
                    String fileName = getFileNameS3(itemCsv.getImageBase64());
                    return cacheService.get("image", fileName, Image.class)
                            .map(image -> ItemMapper.itemCsvToItem(itemCsv, image));
                })
                .collectList()
                .flatMap(items -> itemRepository.saveAll(items).then());
    }

    private String getFileNameS3(String imageBase64) {
        byte[] bytea = Base64.getDecoder().decode(imageBase64);
        return UUID.nameUUIDFromBytes(bytea).toString();
    }
}