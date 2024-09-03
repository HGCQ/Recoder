package yuhan.hgcq.server.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuhan.hgcq.server.domain.Team;
import yuhan.hgcq.server.repository.TeamRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TeamService {
    private static final Logger log = LoggerFactory.getLogger(TeamService.class);

    private final TeamRepository tr;

    /**
     * 그룹 생성
     *
     * @param team 그룹
     */
    @Transactional
    public void teamCreate(Team team) {
        ensureNotNull(team, "Team");

        tr.save(team);
        log.info("Team created: {}", team);
    }

    /**
     * 그룹 수정
     *
     * @param team 그룹
     */
    @Transactional
    public void teamUpdate(Team team) {
        ensureNotNull(team, "Team");

        tr.save(team);
        log.info("Team updated: {}", team);
    }

    /**
     * 그룹 삭제
     *
     * @param team 그룹
     */
    @Transactional
    public void teamDelete(Team team) {
        ensureNotNull(team, "Team");

        tr.delete(team.getId());
        log.info("Team deleted: {}", team);
    }

    /**
     * 그룹 검색
     *
     * @param id 그룹 id
     * @return 그룹
     */
    public Team teamSearch(Long id) {
        Team findTeam = tr.findOne(id);

        if (findTeam == null) {
            throw new IllegalArgumentException("team not found");
        }

        return findTeam;
    }

    /* 매개변수가 null 값인지 확인 */
    private void ensureNotNull(Object obj, String name) {
        if (obj == null) {
            throw new IllegalArgumentException(name + " is null");
        }
    }
}
