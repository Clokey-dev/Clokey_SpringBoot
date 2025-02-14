package com.clokey.server.domain.search.application;

import com.clokey.server.domain.cloth.dto.ClothResponseDTO;

import java.io.IOException;

public interface SearchService {

    void syncClothesDataToElasticsearch() throws IOException;

    ClothResponseDTO.ClothPreviewListResult searchClothesByNameOrBrand(String keyword, int page, int size) throws IOException;
}
