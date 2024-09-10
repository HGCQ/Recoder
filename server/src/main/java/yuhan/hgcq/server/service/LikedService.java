package yuhan.hgcq.server.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuhan.hgcq.server.domain.Liked;
import yuhan.hgcq.server.domain.Member;
import yuhan.hgcq.server.domain.Photo;
import yuhan.hgcq.server.repository.LikedRepository;

import java.util.List;

/**
 * 좋아요 기능 요구사항 분석
 * 1. 회원마다 사진에 좋아요를 할 수 있다.
 * 2. 좋아요를 한 사진 리스트를 볼 수 있다.
 */

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LikedService {
    private static final Logger log = LoggerFactory.getLogger(LikedService.class);

    private final LikedRepository lr;

    /**
     * 좋아요 추가
     *
     * @param liked 좋아요
     */
    @Transactional
    public void add(Liked liked) {
        ensureNotNull(liked, "Liked");

        Member member = liked.getMember();
        Photo photo = liked.getPhoto();

        Liked find = lr.findOne(member, photo);

        if (find == null) {
            lr.save(liked);
        } else {
            liked.addLiked();
            lr.update(liked);
        }
        log.info("Add Like : {}", liked);
    }

    /**
     * 좋아요 삭제
     *
     * @param liked 좋아요
     */
    @Transactional
    public void remove(Liked liked) {
        ensureNotNull(liked, "Liked");

        liked.cancelLiked();

        lr.update(liked);
        log.info("Cancel Like : {}", liked);
    }

    /**
     * 좋아요 검색
     *
     * @param member 회원
     * @param photo  사진
     * @return 좋아요
     */
    public Liked search(Member member, Photo photo) {
        ensureNotNull(member, "Member");
        ensureNotNull(photo, "Photo");

        Liked find = lr.findOne(member, photo);

        if (find == null) {
            throw new IllegalStateException("Liked is null");
        }

        return find;
    }

    /**
     * 좋아요한 사진 리스트 검색
     *
     * @param member 회원
     * @return 사진 리스트
     */
    public List<Photo> searchAll(Member member) {
        ensureNotNull(member, "Member");

        return lr.findAll(member);
    }

    /* 매개변수가 null 값인지 확인 */
    private void ensureNotNull(Object obj, String name) {
        if (obj == null) {
            throw new IllegalArgumentException(name + " is null");
        }
    }
}
