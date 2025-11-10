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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.ItemCsv;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.model.Image;
import ru.yandex.practicum.mapper.ItemMapper;
import ru.yandex.practicum.mapper.ImageMapper;
import ru.yandex.practicum.repository.ItemRepository;
import ru.yandex.practicum.repository.ImageRepository;
import org.springframework.web.multipart.MultipartFile;
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
    public void importCsvFile(MultipartFile file) {
        List<ItemCsv> itemCsvs;

        try (var reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            HeaderColumnNameMappingStrategy<ItemCsv> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(ItemCsv.class);

            CsvToBean<ItemCsv> csvToBean = new CsvToBeanBuilder<ItemCsv>(reader)
                    .withMappingStrategy(strategy)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreEmptyLine(true)
                    .build();

            itemCsvs = csvToBean.parse();
            log.info("Parsed {} items from CSV file", itemCsvs.size());

        } catch (Exception e) {
            log.error("Error parsing CSV file: {}", e.getMessage());
            throw new ImportCsvException(e.getMessage(), e);
        }

        if (!itemCsvs.isEmpty()) {
            saveImages(itemCsvs);
            saveItems(itemCsvs);
        }
    }

    private void saveImages(List<ItemCsv> itemCsvs) {
        List<Image> images = itemCsvs.stream()
                .map(ItemCsv::getImageBase64)
                .map(Base64.getDecoder()::decode)
                .map(it -> s3Service.uploadImage(UUID.nameUUIDFromBytes(it).toString(), it))
                .map(ImageMapper::imageInfoToImage)
                .peek(it -> imageCache.put(it.getFileName(), it))
                .toList();

        imageRepository.saveAll(images);
    }

    private void saveItems(List<ItemCsv> itemCsvs) {
        List<Item> items = itemCsvs.stream()
                .map(it -> ItemMapper.itemCsvToItem(it, imageCache.get(getFileNameS3(it.getImageBase64()))))
                .toList();

        itemRepository.saveAll(items);
    }

    private String getFileNameS3(String imageBase64) {
        byte[] bytea = Base64.getDecoder().decode(imageBase64);
        return UUID.nameUUIDFromBytes(bytea).toString();
    }
}