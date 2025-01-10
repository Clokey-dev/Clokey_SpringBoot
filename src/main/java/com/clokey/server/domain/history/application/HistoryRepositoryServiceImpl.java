package com.clokey.server.domain.history.application;

import com.clokey.server.domain.HistoryImage.dao.HistoryImageRepository;
import com.clokey.server.domain.history.dao.HistoryRepository;
import com.clokey.server.domain.model.History;
import com.clokey.server.domain.model.HistoryImage;
import com.clokey.server.domain.model.enums.Visibility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoryRepositoryServiceImpl implements HistoryRepositoryService {

    private final HistoryRepository historyRepository;
    private final HistoryImageRepository historyImageRepository;

    @Override
    public boolean historyExist(Long historyId) {
        return historyRepository.existsById(historyId);
    }

    //validation 처리할 때 해결.
    @Override
    public History getHistoryById(Long historyId) {
        return historyRepository.findById(historyId).get();
    }

    @Override
    public boolean isPublic(Long historyId) {
        Visibility visibility = historyRepository.findById(historyId)
                .get()
                .getVisibility();
        return visibility.equals(Visibility.PUBLIC);
    }

    @Override
    public List<History> getMemberHistoryByYearMonth(Long memberId, String yearMonth) {
        return historyRepository.findHistoriesByMemberAndYearMonth(memberId, yearMonth);
    }

    @Override
    public List<String> getFirstImageUrlsOfHistory(List<History> histories) {
        return histories.stream()
                .map(history -> historyImageRepository.findByHistory_Id(history.getId())
                        .stream()
                        .sorted(Comparator.comparing(HistoryImage::getCreatedAt)) // createdAt 기준으로 정렬
                        .findFirst() // 첫 번째 이미지 가져오기
                        .map(HistoryImage::getImageUrl) // 이미지 URL을 추출
                        .orElse("")) // 없으면 빈 문자열 반환
                .toList();
    }


}
