package com.clokey.server.domain.member.application;

import com.clokey.server.domain.member.dto.GetUserResponseDTO;

public interface GetUserQueryService {

    GetUserResponseDTO.GetUserRP getUser(String clokeyId);

}
