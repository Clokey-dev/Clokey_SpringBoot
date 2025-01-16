package com.clokey.server.domain.term.application;

import com.clokey.server.domain.model.entity.Term;

public interface TermRepositoryService {

    boolean termExist(Long termId);

    Term findById(Long termId);

}
