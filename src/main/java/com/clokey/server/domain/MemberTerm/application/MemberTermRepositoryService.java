package com.clokey.server.domain.MemberTerm.application;

import com.clokey.server.domain.model.mapping.MemberTerm;

public interface MemberTermRepositoryService {
    void saveMemberTerm(MemberTerm memberTerm); // MemberTerm 저장
}
