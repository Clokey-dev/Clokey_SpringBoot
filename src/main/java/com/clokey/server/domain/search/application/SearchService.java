package com.clokey.server.domain.search.application;

import com.clokey.server.domain.cloth.dto.ClothResponseDTO;
import com.clokey.server.domain.history.dto.HistoryResponseDTO;
import com.clokey.server.domain.member.domain.document.MemberDocument;
import com.clokey.server.domain.member.dto.MemberDTO;
import org.springframework.data.domain.Page;

import java.io.IOException;

public interface SearchService {

    void syncClothesDataToElasticsearch() throws IOException;

    ClothResponseDTO.ClothPreviewListResult searchClothesByNameOrBrand(String keyword, int page, int size) throws IOException;

    void syncMembersDataToElasticsearch() throws IOException;

    MemberDTO.ProfilePreviewListRP searchMembersByClokeyIdOrNickname(String keyword, int page, int size) throws IOException;

    void syncHistoriesDataToElasticsearch() throws IOException;

    HistoryResponseDTO.HistoryPreviewListResult searchHistoriesByHashtagAndCategory(String keyword, int page, int size) throws IOException;

}
