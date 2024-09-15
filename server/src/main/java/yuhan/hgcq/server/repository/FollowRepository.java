package yuhan.hgcq.server.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
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
        try {
            return em.createQuery("select f from Follow f where f.member = :member and f.follow = :follow", Follow.class)
                    .setParameter("member", member)
                    .setParameter("follow", follow)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * 이름으로 팔로잉 리스트 조회
     *
     * @param member 회원
     * @param name   이름
     * @return 팔로우 리스트
     */
    public List<Member> findFollowingListByName(Member member, String name) {
        return em.createQuery("select f.follow from Follow f where f.member = :member and f.follow.name LIKE :name order by f.follow.name", Member.class)
                .setParameter("member", member)
                .setParameter("name", "%" + name + "%")
                .getResultList();
    }

    /**
     * 회원의 팔로잉 리스트 조회
     *
     * @param member 회원
     * @return 팔로우 리스트
     */
    public List<Member> findFollowingList(Member member) {
        return em.createQuery("select f.follow from Follow f where f.member = :member order by f.follow.name", Member.class)
                .setParameter("member", member)
                .getResultList();
    }

    /**
     * 회원의 팔로워 리스트 조회
     *
     * @param member 회원
     * @return 팔로워 리스트
     */
    public List<Member> findFollowerList(Member member) {
        return em.createQuery("select f.member from Follow f where f.follow = :member order by f.member.name", Member.class)
                .setParameter("member", member)
                .getResultList();
    }

    /**
     * 회원의 팔로워 리스트 이름으로 조회
     *
     * @param member 회원
     * @param name   이름
     * @return 팔로워 리스트
     */
    public List<Member> findFollowerListByName(Member member, String name) {
        return em.createQuery("select f.member from Follow f where f.follow = :member and f.member.name like :name order by f.member.name", Member.class)
                .setParameter("member", member)
                .setParameter("name", "%" + name + "%")
                .getResultList();
    }
}
