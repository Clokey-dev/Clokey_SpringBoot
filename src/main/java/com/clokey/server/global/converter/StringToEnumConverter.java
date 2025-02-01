package com.clokey.server.global.converter;

import org.springframework.core.convert.converter.Converter;

import java.util.Arrays;

public class StringToEnumConverter<T extends Enum<T>> implements Converter<String, T> {

    private final Class<T> enumType;

    @SuppressWarnings("unchecked")
    public StringToEnumConverter(Class<T> enumType) {
        this.enumType = enumType;
    }

    @Override
    public T convert(String source) {
        return Arrays.stream(enumType.getEnumConstants())
                .filter(e -> e.name().equalsIgnoreCase(source)) // 대소문자 구분 없이 변환
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid value '" + source + "' for enum " + enumType.getSimpleName() +
                                ". Allowed values: " + Arrays.toString(enumType.getEnumConstants())));
    }
}
