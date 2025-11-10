package ru.yandex.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.model.Image;
import ru.yandex.practicum.dto.ItemCsv;
import ru.yandex.practicum.dto.response.ItemInfo;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ItemMapper {
    public static Item itemCsvToItem(ItemCsv itemCsv, Image image) {
        return Item.builder()
                .title(itemCsv.getTitle())
                .description(itemCsv.getDescription())
                .price(itemCsv.getPrice())
                .imageId(image.getId())
                .build();
    }

    public static ItemInfo itemToItemInfo(Item item, Image image, Integer count) {
        return ItemInfo.builder()
                .id(item.getId())
                .title(item.getTitle())
                .description(item.getDescription())
                .price(item.getPrice())
                .image(image != null ? ImageMapper.imageToImageInfo(image) : null)
                .count(count != null ? count : 0)
                .build();
    }
}