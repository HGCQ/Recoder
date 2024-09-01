package yuhan.hgcq.server.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import yuhan.hgcq.server.domain.Member;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {
    @PersistenceContext
    private final EntityManager em;

    /**
     * 회원 저장
     *
     * @param member 회원
     * @return 회원 id
     */
    public Long save(Member member) {
        if (member.getId() == null) {
            em.persist(member);
        } else {
            em.merge(member);
        }
        return member.getId();
    }

    /**
     * 회원 삭제
     *
     * @param memberId 회원 id
     */
    public void delete(Long memberId) {
        Member findMember = findOne(memberId);
        em.remove(findMember);
    }

    /**
     * 회원 조회
     *
     * @param id 회원 id
     * @return 회원
     */
    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    /**
     * 회원 조회
     *
     * @param email 이메일
     * @return 회원
     */
    public Member findOne(String email) {
        return em.createQuery("select m from Member m where m.email = :email", Member.class)
                .setParameter("email", email)
                .getSingleResult();
    }

    /**
     * 회원 리스트 조회
     *
     * @param name 이름
     * @return 회원 리스트
     */
    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }

    /**
     * 회원 리스트 조회
     *
     * @return 회원 리스트
     */
    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    /**
     * 이메일 리스트 조회
     *
     * @return 이메일 리스트
     */
    public List<String> findAllEmails() {
        return em.createQuery("select m.email from Member m order by m.email", String.class)
                .getResultList();
    }
}
