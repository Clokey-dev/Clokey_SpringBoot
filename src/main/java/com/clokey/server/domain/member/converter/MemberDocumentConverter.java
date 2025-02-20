package com.clokey.server.domain.member.converter;

import com.clokey.server.domain.member.domain.document.MemberDocument;
import com.clokey.server.domain.member.dto.MemberDTO;
import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MemberDocumentConverter {

    public static <T> List<MemberDTO.ProfilePreview> toProfilePreviewList(Page<? extends T> page) {
        return Optional.ofNullable(page.getContent()).orElse(Collections.emptyList())
                .stream()
                .map(item -> {
                            MemberDocument doc = (MemberDocument) item;
                            return MemberDTO.ProfilePreview.builder()
                                    .nickname(doc.getNickname())
                                    .clokeyId(doc.getClokeyId())
                                    .profileImage(doc.getProfileUrl())
                                    .build();
                        }
                )
                .collect(Collectors.toList());
    }

    public static MemberDTO.ProfilePreviewListRP toProfilePreviewListRP(Page<?> page,
                                                                        List<MemberDTO.ProfilePreview> memberPreviews) {
        return MemberDTO.ProfilePreviewListRP.builder()
                .profilePreviews(memberPreviews)
                .totalPage(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .build();
    }
}
