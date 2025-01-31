package com.clokey.server.domain.member.application;

import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.member.dto.MemberDTO;

public interface GetUserQueryService {

    MemberDTO.GetUserRP getUser(String clokeyId, Member currentUser); // 로그인한 사용자 정보 추가

}
