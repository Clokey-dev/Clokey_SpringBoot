package com.clokey.server.domain.member.application;

import jakarta.servlet.http.HttpServletRequest;

public interface LogoutService {
    void logout(Long userId, HttpServletRequest request);
    void unlink(Long userId);
}
