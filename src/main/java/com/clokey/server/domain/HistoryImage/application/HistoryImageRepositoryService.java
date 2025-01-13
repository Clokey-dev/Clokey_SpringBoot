package com.clokey.server.domain.HistoryImage.application;

import java.util.List;

public interface HistoryImageRepositoryService {

    boolean historyImageExist(Long historyId);

    List<String> getHistoryImageUrls(Long historyId);

}
