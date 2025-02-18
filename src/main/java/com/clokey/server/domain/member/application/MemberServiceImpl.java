package com.clokey.server.domain.member.application;

import com.clokey.server.domain.member.converter.GetUserConverter;
import com.clokey.server.domain.member.converter.ProfileConverter;
import com.clokey.server.domain.member.domain.entity.Follow;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.member.domain.repository.FollowRepository;
import com.clokey.server.domain.member.dto.MemberDTO;
import com.clokey.server.domain.member.exception.MemberException;
import com.clokey.server.domain.model.entity.enums.RegisterStatus;
import com.clokey.server.domain.search.application.SearchRepositoryService;
import com.clokey.server.domain.search.exception.SearchException;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.infra.s3.S3ImageService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements  MemberService{

    private final MemberRepositoryService memberRepositoryService;
    private final FollowRepository followRepository;

    private final S3ImageService s3ImageService; // ✅ S3 업로드 서비스 추가
    private final SearchRepositoryService searchRepositoryService;

    @Override
    public MemberDTO.FollowRP followCheck(String clokeyId, Member currentUser) {
        Long yourUserId = currentUser.getId();
        Long myUserId = memberRepositoryService.findMemberByClokeyId(clokeyId).getId();

        boolean isFollow = followRepository.existsByFollowing_IdAndFollowed_Id(myUserId, yourUserId);

        return new MemberDTO.FollowRP(isFollow);
    }

    @Override
    @Transactional
    public void follow(String clokeyId, Member currentUser) {
        // myClokeyId로 사용자 조회
        Long yourUserId = currentUser.getId();
        Long myUserId = memberRepositoryService.findMemberByClokeyId(clokeyId).getId();

        if(myUserId.equals(yourUserId)){
            throw new MemberException(ErrorStatus.CANNOT_FOLLOW_MYSELF);
        }

        // 팔로우 관계가 존재하는지 확인
        boolean isFollow = followRepository.existsByFollowing_IdAndFollowed_Id(myUserId, yourUserId);

        if (isFollow) {
            // 팔로우가 이미 존재하면 언팔로우 처리
            Follow follow = followRepository.findByFollowing_IdAndFollowed_Id(myUserId, yourUserId)
                    .orElseThrow(() -> new IllegalStateException("팔로우 관계가 존재하지 않습니다."));

            // 팔로우 삭제 (언팔로우)
            followRepository.delete(follow);
        } else {
            // 팔로우가 존재하지 않으면 팔로우 처리
            Follow follow = Follow.builder()
                    .following(memberRepositoryService.findMemberById(myUserId))
                    .followed(memberRepositoryService.findMemberById(yourUserId))
                    .build();

            // 팔로우 저장
            followRepository.save(follow);
        }
    }




    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public MemberDTO.GetUserRP getUser(String clokeyId, Member currentUser) { // 현재 사용자 추가
        Member member = memberRepositoryService.findMemberByClokeyId(clokeyId);

        Long recordCount = countHistoryByMember(member);
        Long followerCount = countFollowersByMember(member);
        Long followingCount = countFollowingByMember(member);
        Boolean isFollowing = isFollowing(currentUser, member); // 팔로우 여부 체크 추가
        List<String> topClothImages=getTop3ClothImages(member);

        return GetUserConverter.toGetUserResponseDTO(
                member, recordCount, followerCount, followingCount, isFollowing,
                topClothImages.size() > 0 ? topClothImages.get(0) : null,
                topClothImages.size() > 1 ? topClothImages.get(1) : null,
                topClothImages.size() > 2 ? topClothImages.get(2) : null
        );
    }

    @Transactional(readOnly = true)
    public Boolean isFollowing(Member currentUser, Member targetUser) {
        String jpql = "SELECT COUNT(f) FROM Follow f WHERE f.following = :currentUser AND f.followed = :targetUser";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("currentUser", currentUser);
        query.setParameter("targetUser", targetUser);
        return query.getSingleResult() > 0;
    }


    @Transactional(readOnly = true) // 트랜잭션 읽기 전용으로 설정
    public Long countHistoryByMember(Member member) {
        String jpql = "SELECT COUNT(h) FROM History h WHERE h.member = :member";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("member", member);
        return query.getSingleResult();
    }

    @Transactional(readOnly = true) // 트랜잭션 읽기 전용으로 설정
    public Long countFollowersByMember(Member member) {
        String jpql = "SELECT COUNT(f) FROM Follow f WHERE f.followed = :member";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("member", member);
        return query.getSingleResult();
    }

    @Transactional(readOnly = true) // 트랜잭션 읽기 전용으로 설정
    public Long countFollowingByMember(Member member) {
        String jpql = "SELECT COUNT(f) FROM Follow f WHERE f.following = :member";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("member", member);
        return query.getSingleResult();
    }

    @Transactional(readOnly = true)
    public List<String> getTop3ClothImages(Member member) {
        String jpql = """
        SELECT c.image.imageUrl
        FROM Cloth c
        WHERE c.member = :member
        ORDER BY c.wearNum DESC
    """;

        TypedQuery<String> query = entityManager.createQuery(jpql, String.class);
        query.setParameter("member", member);
        query.setMaxResults(3); // 상위 3개만 가져오기

        return query.getResultList();
    }



    @Override
    @Transactional
    public MemberDTO.ProfileRP updateProfile(Long userId, MemberDTO.ProfileRQ request,
                                             MultipartFile profileImage, MultipartFile profileBackImage) {
        // 사용자 확인
        Member member = memberRepositoryService.findMemberById(userId);

        // ✅ S3 업로드 후 URL 저장
        String profileImageUrl = (profileImage != null && !profileImage.isEmpty()) ? s3ImageService.upload(profileImage) : member.getProfileImageUrl();
        String profileBackImageUrl = (profileBackImage != null && !profileBackImage.isEmpty()) ? s3ImageService.upload(profileBackImage) : member.getProfileBackImageUrl();

        member.profileUpdate(request, profileImageUrl, profileBackImageUrl);

        if (member.getRegisterStatus() != RegisterStatus.REGISTERED) {
            // 약관 동의가 완료되었으므로 회원의 등록 상태를 업데이트
            member.updateRegisterStatus(RegisterStatus.REGISTERED);
        }

        // 저장
        Member updatedMember = memberRepositoryService.saveMember(member);

        // ES 동기화
        try {
            searchRepositoryService.updateMemberDataToElasticsearch(updatedMember);
        } catch (IOException e) {
            throw new SearchException(ErrorStatus.ELASTIC_SEARCH_SYNC_FAULT);
        }

        // 응답 생성
        return ProfileConverter.toProfileRPDTO(updatedMember);
    }
}
