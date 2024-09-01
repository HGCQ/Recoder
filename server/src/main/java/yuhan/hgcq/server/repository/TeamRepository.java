package yuhan.hgcq.server.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import yuhan.hgcq.server.domain.Team;

@Repository
@RequiredArgsConstructor
public class TeamRepository {
    @PersistenceContext
    private final EntityManager em;

    /**
     * 그룹 저장
     *
     * @param team 그룹
     */
    public Long save(Team team) {
        if (team.getId() == null) {
            em.persist(team);
        } else {
            em.merge(team);
        }
        return team.getId();
    }

    /**
     * 그룹 삭제
     *
     * @param id 그룹 id
     */
    public void delete(Long id) {
        Team find = findOne(id);
        em.remove(find);
    }

    /**
     * 그룹 조회
     *
     * @param id 그룹 id
     * @return 그룹
     */
    public Team findOne(Long id) {
        return em.find(Team.class, id);
    }
}
