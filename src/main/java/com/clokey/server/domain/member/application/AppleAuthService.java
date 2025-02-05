package com.clokey.server.domain.member.application;

import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.member.dto.AuthDTO;
import jakarta.servlet.http.HttpServletResponse;

public interface AppleAuthService {

    public AuthDTO.TokenResponse login(String code) throws Exception;
}
