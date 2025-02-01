package com.clokey.server.domain.member.exception.validator;

import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.member.exception.MemberException;
import com.clokey.server.global.config.security.auth.PrincipalDetails;
import com.clokey.server.global.error.code.status.ErrorStatus;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;


import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(Member.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory)
            throws MemberException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null; // 로그인하지 않은 사용자
        }

        if (authentication.getPrincipal() instanceof PrincipalDetails principalDetails) {
            return principalDetails.getMember();
        }

        throw new MemberException(ErrorStatus.NO_SUCH_MEMBER);
    }
}

