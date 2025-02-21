package com.clokey.server.domain.history.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class HashtagDeserializer extends JsonDeserializer<List<String>> {
    @Override
    public List<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        List<String> tags = p.readValueAs(List.class);
        return tags.stream()
                .map(tag -> tag.startsWith("#") ? tag : "#" + tag) // #이 없으면 추가
                .collect(Collectors.toList());
    }
}

