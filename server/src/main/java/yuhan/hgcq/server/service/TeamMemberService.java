package yuhan.hgcq.server.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuhan.hgcq.server.domain.Member;
import yuhan.hgcq.server.domain.Team;
import yuhan.hgcq.server.domain.TeamMember;
import yuhan.hgcq.server.repository.TeamMemberRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TeamMemberService {
    private static final Logger log = LoggerFactory.getLogger(TeamMemberService.class);

    private final TeamMemberRepository tmr;

    /**
     * 그룹에 회원 초대
     *
     * @param teamMember 그룹 회원
     */
    @Transactional
    public void invite(TeamMember teamMember) {
        ensureNotNull(teamMember, "TeamMember");

        tmr.save(teamMember);
        log.info("invite {}", teamMember);
    }

    /**
     * 그룹에 회원 추방
     *
     * @param teamMember 그룹 회원
     */
    @Transactional
    public void expel(TeamMember teamMember) {
        ensureNotNull(teamMember, "TeamMember");

        tmr.delete(teamMember);
        log.info("expel {}", teamMember);
    }

    /**
     * 회원이 가진 그룹 리스트 조회
     *
     * @param member 회원
     * @return 그룹 리스트
     */
    public List<Team> searchTeamList(Member member) {
        ensureNotNull(member, "Member");

        List<Team> findTeamList = tmr.findAll(member);
        log.info("searchTeamList {}", member);
        return findTeamList;
    }

    /**
     * 회원이 가진 그룹 리스트 이름으로 조회
     *
     * @param member 회원
     * @param name   이름
     * @return 그룹 리스트
     */
    public List<Team> searchTeamList(Member member, String name) {
        ensureNotNull(member, "Member");
        ensureNotNull(name, "Name");

        List<Team> findTeamList = tmr.findByName(member, name);
        log.info("searchTeamList {} by name : {}", member, name);
        return findTeamList;
    }

    /**
     * 그룹에 속한 회원 리스트 조회
     *
     * @param team 그룹
     * @return 회원 리스트
     */
    public List<Member> searchMemberList(Team team) {
        ensureNotNull(team, "Team");

        List<Member> findMemberList = tmr.findByTeam(team);
        log.info("searchMemberList {}", team);
        return findMemberList;
    }

    /* 매개변수가 null 값인지 확인 */
    private void ensureNotNull(Object obj, String name) {
        if (obj == null) {
            throw new IllegalArgumentException(name + " is null");
        }
    }
}
