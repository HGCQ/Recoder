package yuhan.hgcq.server.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import yuhan.hgcq.server.domain.Follow;
import yuhan.hgcq.server.domain.Member;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FollowRepository {
    @PersistenceContext
    private final EntityManager em;

    /**
     * 팔로우 저장
     *
     * @param follow 팔로우
     */
    public void save(Follow follow) {
        em.persist(follow);
    }

    /**
     * 팔로우 삭제
     *
     * @param follow 팔로우
     */
    public void delete(Follow follow) {
        em.remove(follow);
    }

    /**
     * 팔로우 조회
     *
     * @param member 회원
     * @param follow 팔로우한 회원
     * @return 팔로우
     */
    public Follow findOne(Member member, Member follow) {
        return em.createQuery("select f from Follow f where f.member = :member and f.follow = :follow", Follow.class)
                .setParameter("member", member)
                .setParameter("follow", follow)
                .getSingleResult();
    }

    /**
     * 이름으로 팔로우 리스트 조회
     *
     * @param member 회원
     * @param name   이름
     * @return 팔로우 리스트
     */
    public List<Member> findByName(Member member, String name) {
        return em.createQuery("select f.follow from Follow f where f.member = :member and f.follow.name LIKE :name order by f.follow.name", Member.class)
                .setParameter("member", member)
                .setParameter("name", "%" + name + "%")
                .getResultList();
    }

    /**
     * 회원의 팔로우 리스트 조회
     *
     * @param member 회원
     * @return 팔로우 리스트
     */
    public List<Member> findFollowList(Member member) {
        return em.createQuery("select f.follow from Follow f where f.member = :member order by f.follow.name", Member.class)
                .setParameter("member", member)
                .getResultList();
    }
}
