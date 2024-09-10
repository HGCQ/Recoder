package yuhan.hgcq.server.service;


import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
