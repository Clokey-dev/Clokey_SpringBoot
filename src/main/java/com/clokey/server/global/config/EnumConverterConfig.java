package com.clokey.server.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import com.clokey.server.global.converter.StringToEnumConverter;

@Configuration
public class EnumConverterConfig implements WebMvcConfigurer {

    private static final List<Class<? extends Enum<?>>> ENUM_CLASSES = List.of(
            com.clokey.server.domain.model.entity.enums.ClothSort.class,
            com.clokey.server.domain.model.entity.enums.Season.class,
            com.clokey.server.domain.model.entity.enums.SummaryFrequency.class
            );

    @Override
    public void addFormatters(FormatterRegistry registry) {
        for (Class<? extends Enum<?>> enumClass : ENUM_CLASSES) {
            registry.addConverter(createConverter(enumClass));
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Enum<T>> StringToEnumConverter<T> createConverter(Class<? extends Enum<?>> enumClass) {
        return new StringToEnumConverter<>((Class<T>) enumClass);
    }
}
