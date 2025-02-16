package com.clokey.server.domain.search.application;

import com.clokey.server.domain.cloth.domain.entity.Cloth;
import com.clokey.server.domain.history.domain.entity.History;
import com.clokey.server.domain.member.domain.entity.Member;

import java.io.IOException;

public interface SearchRepositoryService {

    // Cloth Sync

    void updateClothDataToElasticsearch(Cloth cloth) throws IOException;

    void deleteClothesByClokeyIdFromElasticsearch(String clokeyId) throws IOException;

    void deleteClothByIdFromElasticsearch(Long clothId) throws IOException;

    void syncAllClothesDataToElasticsearch() throws IOException;

    // History Sync

    void updateHistoryDataToElasticsearch(History history) throws IOException;

    void deleteHistoriesByClokeyIdFromElasticsearch(String clokeyId) throws IOException;

    void deleteHistoryByIdFromElasticsearch(Long historyId) throws IOException;

    void syncAllHistoriesDataToElasticsearch() throws IOException;

    // Member Sync

    void updateMemberDataToElasticsearch(Member member) throws IOException;

    void deleteMemberByClokeyIdFromElasticsearch(String clokeyId) throws IOException;

    void syncAllMembersDataToElasticsearch() throws IOException;

}
