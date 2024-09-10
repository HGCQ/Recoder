package yuhan.hgcq.server.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.AccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuhan.hgcq.server.domain.Member;
import yuhan.hgcq.server.domain.Team;
import yuhan.hgcq.server.domain.TeamMember;
import yuhan.hgcq.server.repository.TeamMemberRepository;

import java.util.List;

/**
 * 그룹 회원 기능 요구사항 분석
 * 1. 관리자만 그룹에 회원을 초대할 수 있다.
 * 2. 관리자만 그룹에 회원(관리자가 아닐 시)을 추방시킬 수 있다.
 * 3. 그룹 소유자만 관리자 권한을 부여할 수 있다.
 * 4. 그룹 소유자는 관리자 권한을 박탈할 수 있다.
 * 5. 소유자가 아닌 회원이 그룹을 삭제하면 그룹에서 탈퇴한다.
 * 6. 회원마다 가진 그룹 리스트를 추출할 수 있다.
 * 7. 그룹에 속한 회원 리스트를 추출할 수 있다.
 * 8. 그룹에 속한 관리자 리스트를 추출할 수 있다.
 */

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TeamMemberService {
    private static final Logger log = LoggerFactory.getLogger(TeamMemberService.class);

    private final TeamMemberRepository tmr;

    /**
     * 그룹에 회원 초대
     *
     * @param member     주체
     * @param teamMember 대상
     * @throws AccessException 관리자가 아닐 시
     */
    @Transactional
    public void invite(Member member, TeamMember teamMember) throws AccessException {
        ensureNotNull(member, "Member");
        ensureNotNull(teamMember, "TeamMember");

        Team team = teamMember.getTeam();
        boolean isAdmin = isAdmin(member, team);

        if (isAdmin) {
            tmr.save(teamMember);
            log.info("Team Member invite {}", teamMember);
        } else {
            throw new AccessException("Don't have Permission");
        }
    }

    /**
     * 그룹에 회원 추방
     *
     * @param member     주체
     * @param teamMember 대상
     * @throws AccessException 관리자가 아닐 시
     */
    @Transactional
    public void expel(Member member, TeamMember teamMember) throws AccessException {
        ensureNotNull(member, "Member");
        ensureNotNull(teamMember, "TeamMember");

        Team team = teamMember.getTeam();
        boolean isAdmin = isAdmin(member, team); // 주체
        boolean objIsAdmin = teamMember.getIsAdmin(); // 대상

        if (isAdmin && !objIsAdmin) {
            tmr.delete(teamMember);
            log.info("Team Member expel {}", teamMember);
        } else {
            throw new AccessException("Don't have Permission");
        }
    }

    /**
     * 관리자 권한 부여
     *
     * @param member     주체
     * @param teamMember 대상
     * @throws AccessException 소유자가 아닐 시
     */
    @Transactional
    public void authorizeAdmin(Member member, TeamMember teamMember) throws AccessException {
        ensureNotNull(member, "Member");
        ensureNotNull(teamMember, "TeamMember");

        Team team = teamMember.getTeam();
        boolean isOwner = isOwner(member, team);

        if (isOwner) {
            teamMember.authorizeAdmin();
            tmr.update(teamMember);
            log.info("Team Member authorize admin {}", teamMember);
        } else {
            throw new AccessException("Don't have Permission");
        }
    }

    /**
     * 관리자 권한 박탈
     *
     * @param member     주체
     * @param teamMember 대상
     * @throws AccessException 소유자가 아닐 시
     */
    @Transactional
    public void revokeAdmin(Member member, TeamMember teamMember) throws AccessException {
        ensureNotNull(member, "Member");
        ensureNotNull(teamMember, "TeamMember");

        Team team = teamMember.getTeam();
        boolean isOwner = isOwner(member, team);

        if (isOwner) {
            teamMember.revokeAdmin();
            tmr.update(teamMember);
            log.info("Team Member revoke admin {}", teamMember);
        } else {
            throw new AccessException("Don't have Permission");
        }
    }

    /**
     * 회원이 가진 그룹 리스트 검색
     *
     * @param member 회원
     * @return 그룹 리스트
     */
    public List<Team> searchTeamList(Member member) {
        ensureNotNull(member, "Member");

        return tmr.findAll(member);
    }

    /**
     * 회원이 가진 그룹 리스트 이름으로 검색
     *
     * @param member 회원
     * @param name   이름
     * @return 그룹 리스트
     */
    public List<Team> searchTeamList(Member member, String name) {
        ensureNotNull(member, "Member");
        ensureNotNull(name, "Name");

        return tmr.findByName(member, name);
    }

    /**
     * 그룹에 속한 회원 리스트 검색
     *
     * @param team 그룹
     * @return 회원 리스트
     */
    public List<Member> searchMemberList(Team team) {
        ensureNotNull(team, "Team");

        return tmr.findByTeam(team);
    }

    /**
     * 그룹에 속한 관리자 리스트 검색
     *
     * @param team 그룹
     * @return 관리자 리스트
     */
    public List<Member> searchAdminList(Team team) {
        ensureNotNull(team, "Team");

        return tmr.findAdminByTeam(team);
    }

    /* 매개변수가 null 값인지 확인 */
    private void ensureNotNull(Object obj, String name) {
        if (obj == null) {
            throw new IllegalArgumentException(name + " is null");
        }
    }

    /* 소유자인지 확인 */
    private boolean isOwner(Member member, Team team) {
        return team.getOwner().equals(member);
    }

    /* 관리자인지 확인 */
    private boolean isAdmin(Member member, Team team) {
        List<Member> adminList = tmr.findAdminByTeam(team);
        return adminList.contains(member);
    }
}
