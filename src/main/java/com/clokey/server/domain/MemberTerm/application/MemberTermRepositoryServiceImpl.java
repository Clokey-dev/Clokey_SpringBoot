package com.clokey.server.domain.MemberTerm.application;

import com.clokey.server.domain.model.repository.MemberTermRepository;
import com.clokey.server.domain.model.entity.mapping.MemberTerm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberTermRepositoryServiceImpl implements MemberTermRepositoryService {

    private final MemberTermRepository memberTermRepository;

    @Override
    public void saveMemberTerm(MemberTerm memberTerm) {
        memberTermRepository.save(memberTerm);
    }
}
