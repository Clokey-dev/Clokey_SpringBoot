package com.clokey.server.domain.member.application;

import com.clokey.server.domain.member.dto.MemberDTO;

public interface GetUserQueryService {

    MemberDTO.GetUserRP getUser(String clokeyId);

}
