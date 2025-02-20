package com.clokey.server.domain.search.application;

import java.io.IOException;

import com.clokey.server.domain.cloth.domain.entity.Cloth;
import com.clokey.server.domain.history.domain.entity.History;
import com.clokey.server.domain.member.domain.entity.Member;

public interface SearchRepositoryService {

    // Cloth Sync

    void updateClothDataToElasticsearch(Cloth cloth) throws IOException;

    void deleteClothesByMemberIdFromElasticsearch(Long memberId) throws IOException;

    void deleteClothByIdFromElasticsearch(Long clothId) throws IOException;

    void syncAllClothesDataToElasticsearch() throws IOException;

    // History Sync

    void updateHistoryDataToElasticsearch(History history) throws IOException;

    void deleteHistoriesByMemberIdFromElasticsearch(Long memberId) throws IOException;

    void deleteHistoryByIdFromElasticsearch(Long historyId) throws IOException;

    void syncAllHistoriesDataToElasticsearch() throws IOException;

    // Member Sync

    void updateMemberDataToElasticsearch(Member member) throws IOException;

    void deleteMemberByMemberIdFromElasticsearch(Long memberId) throws IOException;

    void syncAllMembersDataToElasticsearch() throws IOException;

}
