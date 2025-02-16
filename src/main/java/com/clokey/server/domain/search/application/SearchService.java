package com.clokey.server.domain.search.application;

import com.clokey.server.domain.cloth.domain.entity.Cloth;
import com.clokey.server.domain.cloth.dto.ClothResponseDTO;
import com.clokey.server.domain.history.domain.entity.History;
import com.clokey.server.domain.history.dto.HistoryResponseDTO;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.member.dto.MemberDTO;

import java.io.IOException;

public interface SearchService {

    ClothResponseDTO.ClothPreviewListResult searchClothesByNameOrBrand(Long requestedMemberId, String clokeyId, String keyword, int page, int size) throws IOException;

    HistoryResponseDTO.HistoryPreviewListResult searchHistoriesByHashtagAndCategory(String keyword, int page, int size) throws IOException;

    MemberDTO.ProfilePreviewListRP searchMembersByClokeyIdOrNickname(String keyword, int page, int size) throws IOException;
}
