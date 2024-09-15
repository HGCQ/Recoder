package yuhan.hgcq.server.service;


import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuhan.hgcq.server.domain.Follow;
import yuhan.hgcq.server.domain.Member;
import yuhan.hgcq.server.repository.FollowRepository;

import java.util.List;

/**
 * 팔로우 기능 요구사항 분석
 * 1. 팔로잉과 팔로워가 있다.
 * 2. 팔로잉은 회원 주체가 다른 회원을 팔로우한 것이다.
 * 3. 팔로워는 회원을 다른 회원이 팔로우한 것이다.
 * 4. 팔로워 리스트를 추출할 수 있다.
 * 5. 팔로잉 리스트를 추출할 수 있다.
 * 6. 팔로워, 팔로잉을 이름으로 검색할 수 있다.
 */

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FollowService {
    private static final Logger log = LoggerFactory.getLogger(FollowService.class);

    private final FollowRepository fr;

    /**
     * 팔로우 추가
     *
     * @param follow 팔로우
     */
    @Transactional
    public void add(Follow follow) throws IllegalArgumentException {
        ensureNotNull(follow, "Follow");

        fr.save(follow);
        log.info("Create Follow : {}", follow);
    }

    /**
     * 팔로우 삭제
     *
     * @param follow 팔로우
     */
    @Transactional
    public void remove(Follow follow) throws IllegalArgumentException {
        ensureNotNull(follow, "Follow");

        fr.delete(follow);
        log.info("Delete Follow : {}", follow);
    }

    /**
     * 팔로우 검색
     *
     * @param member 회원
     * @param follow 팔로우
     * @return 팔로우
     */
    public Follow search(Member member, Member follow) throws IllegalArgumentException {
        ensureNotNull(member, "Member");
        ensureNotNull(follow, "Follow");

        return fr.findOne(member, follow);
    }

    /**
     * 회원의 팔로잉 리스트
     *
     * @param member 회원
     * @return 팔로우 리스트
     */
    public List<Member> searchFollowing(Member member) throws IllegalArgumentException {
        ensureNotNull(member, "Member");

        return fr.findFollowingList(member);
    }

    /**
     * 회원의 팔로잉 리스트 이름으로 검색
     *
     * @param member 회원
     * @param name   이름
     * @return 팔로잉 리스트
     */
    public List<Member> searchFollowingByName(Member member, String name) throws IllegalArgumentException {
        ensureNotNull(member, "Member");

        return fr.findFollowingListByName(member, name);
    }

    /**
     * 회원의 팔로워 리스트
     *
     * @param member 회원
     * @return 팔로워 리스트
     */
    public List<Member> searchFollower(Member member) throws IllegalArgumentException {
        ensureNotNull(member, "Member");

        return fr.findFollowerList(member);
    }

    /**
     * 회원의 팔로워 리스트 이름으로 검색
     *
     * @param member 회원
     * @param name   이름
     * @return 팔로워 리스트
     */
    public List<Member> searchFollowerByName(Member member, String name) throws IllegalArgumentException {
        ensureNotNull(member, "Member");

        return fr.findFollowerListByName(member, name);
    }

    /* 매개변수가 null 값인지 확인 */
    private void ensureNotNull(Object obj, String name) {
        if (obj == null) {
            throw new IllegalArgumentException(name + " is null");
        }
    }
}
