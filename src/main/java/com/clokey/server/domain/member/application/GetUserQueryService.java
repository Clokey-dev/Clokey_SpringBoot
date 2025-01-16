package com.clokey.server.domain.member.application;

import com.clokey.server.domain.member.dto.MemberResponseDTO;

public interface GetUserQueryService {

    MemberResponseDTO.GetUserRP getUser(String clokeyId);

}
