package yuhan.hgcq.server.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.AccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import yuhan.hgcq.server.domain.Member;
import yuhan.hgcq.server.domain.Team;
import yuhan.hgcq.server.domain.TeamMember;
import yuhan.hgcq.server.dto.photo.UploadTeamForm;
import yuhan.hgcq.server.repository.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * 그룹 기능 요구사항 분석
 * 1. 그룹을 생성한 회원은 그룹 소유자(owner)가 된다.
 * 2. 그룹에 속한 회원 중에서, 관리자 권한이 있는 회원만 그룹을 수정할 수 있다.
 * 3. 그룹 소유자만 그룹을 삭제할 수 있다.
 */

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TeamService {
    private static final Logger log = LoggerFactory.getLogger(TeamService.class);

    private final TeamRepository tr;
    private final TeamMemberRepository tmr;
    private final AlbumRepository ar;
    private final LikedRepository lr;
    private final ChatRepository cr;
    private final PhotoRepository pr;

    private final static String DIRECTORY_PATH = "D:" + File.separator
            + "app" + File.separator
            + "images" + File.separator
            + "team" + File.separator;

    /**
     * 그룹 생성(테스트 완료)
     *
     * @param team 그룹
     */
    @Transactional
    public Long create(Team team) throws IllegalArgumentException {
        ensureNotNull(team, "Team");

        Long saveId = tr.save(team);

        TeamMember tm = new TeamMember(team, team.getOwner());
        tm.authorizeAdmin();

        tmr.save(tm);
        log.info("Team created: {}", team);
        return saveId;
    }

    /**
     * 그룹 수정(테스트 완료)
     *
     * @param member 회원
     * @param team   그룹
     * @throws AccessException 관리자가 아닐 시
     */
    @Transactional
    public void update(Member member, Team team) throws AccessException, IllegalArgumentException {
        ensureNotNull(member, "Member");
        ensureNotNull(team, "Team");

        List<Member> adminList = tmr.findAdminByTeam(team);

        if (adminList.contains(member)) {
            tr.save(team);
            log.info("Team updated: {}", team);
        } else {
            throw new AccessException("Don't have Permission");
        }
    }

    /**
     * 그룹 삭제(테스트 완료)
     *
     * @param member 회원
     * @param team   그룹
     */
    @Transactional
    public void delete(Member member, Team team) throws IllegalArgumentException {
        ensureNotNull(member, "Member");
        ensureNotNull(team, "Team");

        boolean isOwner = isOwner(member, team);

        if (isOwner) {
            lr.deleteByTeam(team);
            pr.deleteByTeam(team);
            cr.deleteByTeam(team);
            ar.deleteByTeam(team);
            tmr.deleteAll(team);
            tr.delete(team.getId());
            log.info("Team deleted: {}", team);
        } else {
            TeamMember find = tmr.findOne(member, team);
            tmr.delete(find);
            log.info("Team exit : {}", find);
        }
    }

    /**
     * 그룹 검색(테스트 완료)
     *
     * @param id 그룹 id
     * @return 그룹
     */
    public Team search(Long id) throws IllegalArgumentException {
        Team findTeam = tr.findOne(id);

        if (findTeam == null) {
            throw new IllegalArgumentException("team not found");
        }

        return findTeam;
    }

    @Transactional
    public void upload(Member member, UploadTeamForm form) throws IOException, AccessException, IllegalArgumentException {
        ensureNotNull(member, "Member");

        Long teamId = form.getTeamId();
        MultipartFile file = form.getFile();
        Team ft = tr.findOne(teamId);

        ensureNotNull(ft, "Team");
        List<Member> adminList = tmr.findAdminByTeam(ft);

        if (adminList.contains(member)) {
            try {
                String newPath = DIRECTORY_PATH + teamId + File.separator;
                File directory = new File(newPath);

                if (!directory.exists()) {
                    directory.mkdirs();
                }

                String name = file.getOriginalFilename();

                Path path = Paths.get(newPath + name);
                file.transferTo(path);
                String imagePath = "/images/team/" + teamId + "/" + name;

                ft.changeImage(imagePath);
                tr.save(ft);

                log.info("Upload Team : {}", member);
            } catch (IOException e) {
                log.error("IO Error");
                throw new IOException();
            }
        } else {
            throw new AccessException("Not admin");
        }
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
}
