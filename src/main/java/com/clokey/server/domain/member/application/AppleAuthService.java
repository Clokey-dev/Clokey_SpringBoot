package com.clokey.server.domain.member.application;

import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.member.dto.AuthDTO;
import jakarta.servlet.http.HttpServletResponse;

import java.net.http.HttpRequest;
import java.util.Map;

public interface AppleAuthService {

    AuthDTO.TokenResponse login(String code, String deviceToken);
    HttpRequest.BodyPublisher getParamsUrlEncoded(Map<String, String> parameters);
    String createClientSecret();

}
