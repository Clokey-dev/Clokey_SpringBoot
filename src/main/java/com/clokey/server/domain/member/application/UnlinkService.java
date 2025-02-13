package com.clokey.server.domain.member.application;

import jakarta.servlet.http.HttpServletRequest;

public interface UnlinkService {
    void unlink(Long userId);
}
