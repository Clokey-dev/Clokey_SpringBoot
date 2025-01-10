package com.clokey.server.domain.cloth.application;

import com.clokey.server.domain.model.enums.Visibility;

public interface ClothRepositoryService {

    boolean clothExist(Long clothId);

    boolean canEdit(Long clothId, Long memberId);

    boolean isPublic(Long clothId);
}
