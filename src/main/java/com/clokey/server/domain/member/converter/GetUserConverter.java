package com.clokey.server.domain.member.converter;

import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.member.dto.MemberDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class GetUserConverter {

    public static MemberDTO.GetUserRP toGetUserResponseDTO(Member member, Long recordCount, Long followerCount, Long followingCount, Boolean isFollowing
            , String clothImage1, String clothImage2, String clothImage3) {
        return MemberDTO.GetUserRP.builder()
                .clokeyId(member.getClokeyId())
                .profileImageUrl(member.getProfileImageUrl())
                .recordCount(recordCount)
                .followerCount(followerCount)
                .followingCount(followingCount)
                .nickname(member.getNickname())
                .bio(member.getBio())
                .profileBackImageUrl(member.getProfileBackImageUrl())
                .visibility(member.getVisibility().toString())
                .isFollowing(isFollowing) // 추가된 필드 반영
                .clothImage1(clothImage1)
                .clothImage2(clothImage2)
                .clothImage3(clothImage3)
                .build();
    }

    public static MemberDTO.GetFollowMemberResult toGetFollowPeopleResultDTO(
            List<Member> members, Pageable pageable, List<Boolean> isFollowings) {

        List<MemberDTO.FollowMemberResult> memberResults =  IntStream.range(0, members.size())
                .mapToObj(i -> convertToProfilePreviewResult(members.get(i), isFollowings.get(i)))
                .collect(Collectors.toList());

        return MemberDTO.GetFollowMemberResult.builder()
                .members(memberResults)
                .totalPage(pageable.getPageNumber() + 1)
                .totalElements(memberResults.size())
                .isFirst(pageable.getPageNumber() == 0)
                .isLast(memberResults.size() < pageable.getPageSize())
                .build();
    }

    private static MemberDTO.FollowMemberResult convertToProfilePreviewResult(Member member, Boolean isFollowing) {
        return MemberDTO.FollowMemberResult.builder()
                .profileImage(member.getProfileImageUrl())
                .clokeyId(member.getClokeyId())
                .nickname(member.getNickname())
                .isFollowed(isFollowing)
                .build();
    }
}



