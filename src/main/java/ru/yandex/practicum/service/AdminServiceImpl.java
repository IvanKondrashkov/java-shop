package ru.yandex.practicum.service;

import java.util.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import java.util.concurrent.ConcurrentHashMap;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.yandex.practicum.dto.ItemCsv;
import ru.yandex.practicum.model.Image;
import ru.yandex.practicum.mapper.ItemMapper;
import ru.yandex.practicum.mapper.ImageMapper;
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
    private final Map<String, Image> imageCache = new ConcurrentHashMap<>();

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
                .map(ItemCsv::getImageBase64)
                .map(Base64.getDecoder()::decode)
                .flatMap(bytes -> {
                    String fileName = UUID.nameUUIDFromBytes(bytes).toString();
                    return Mono.fromCallable(() -> s3Service.uploadImage(fileName, bytes))
                            .subscribeOn(Schedulers.boundedElastic())
                            .map(ImageMapper::imageInfoToImage);
                })
                .doOnNext(image -> imageCache.put(image.getFileName(), image))
                .collectList()
                .flatMap(images -> imageRepository.saveAll(images).then(Mono.just(itemCsvs)));
    }

    private Mono<Void> saveItems(List<ItemCsv> itemCsvs) {
        return Flux.fromIterable(itemCsvs)
                .map(it -> ItemMapper.itemCsvToItem(it, imageCache.get(getFileNameS3(it.getImageBase64()))))
                .collectList()
                .flatMap(items -> itemRepository.saveAll(items).then());
    }

    private String getFileNameS3(String imageBase64) {
        byte[] bytea = Base64.getDecoder().decode(imageBase64);
        return UUID.nameUUIDFromBytes(bytea).toString();
    }
}