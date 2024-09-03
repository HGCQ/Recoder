package yuhan.hgcq.server.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import yuhan.hgcq.server.domain.Liked;
import yuhan.hgcq.server.domain.Member;
import yuhan.hgcq.server.domain.Photo;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LikedRepository {
    @PersistenceContext
    private final EntityManager em;

    /**
     * 좋아요 저장
     * @param liked 좋아요
     */
    public void save(Liked liked) {
        em.persist(liked);
    }

    /**
     * 좋아요 조회
     * @param member 회원
     * @param photo 사진
     * @return 좋아요
     */
    public Liked findOne(Member member, Photo photo) {
        return em.createQuery("select l from Liked l where l.member = :member and l.photo = :photo", Liked.class)
                .setParameter("member", member)
                .setParameter("photo", photo)
                .getSingleResult();
    }

    /**
     * 좋아요 수정
     * @param liked 좋아요
     */
    public void update(Liked liked) {
        em.merge(liked);
    }

    /**
     * 좋아요한 사진 리스트 조회
     * @param member 회원
     * @return 사진 리스트
     */
    public List<Photo> findAll(Member member) {
        return em.createQuery("select l.photo from Liked l where l.member = :member", Photo.class)
                .setParameter("member", member)
                .getResultList();
    }
}
