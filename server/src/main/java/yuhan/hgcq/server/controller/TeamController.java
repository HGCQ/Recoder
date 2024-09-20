package yuhan.hgcq.server.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.AccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yuhan.hgcq.server.domain.Member;
import yuhan.hgcq.server.domain.Team;
import yuhan.hgcq.server.domain.TeamMember;
import yuhan.hgcq.server.dto.member.MemberDTO;
import yuhan.hgcq.server.dto.team.*;
import yuhan.hgcq.server.service.MemberService;
import yuhan.hgcq.server.service.TeamMemberService;
import yuhan.hgcq.server.service.TeamService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {

    private final MemberService ms;
    private final TeamService ts;
    private final TeamMemberService tms;

    /**
     * 그룹 생성
     *
     * @param createForm 그룹 생성 폼
     * @param request    요청
     * @return 성공 시 201 상태 코드, 실패 시 400 상태 코드 또는 404 상태 코드, 권한 없을 시 401 상태 코드, 세션 없을 시 401 상태 코드
     */
    @PostMapping("/create")
    public ResponseEntity<?> createTeam(@RequestBody TeamCreateForm createForm, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.search(loginMember.getMemberId());

                    if (findMember != null) {
                        Team newTeam = new Team(findMember, createForm.getName());
                        try {
                            ts.create(newTeam);

                            List<Long> memberIdList = createForm.getMembers();
                            List<Member> memberList = new ArrayList<>();

                            for (Long memberId : memberIdList) {
                                try {
                                    Member fm = ms.search(memberId);

                                    if (fm != null) {
                                        memberList.add(fm);
                                    }

                                } catch (IllegalArgumentException e) {
                                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Member Fail");
                                }
                            }

                            for (Member member : memberList) {
                                TeamMember newTeamMember = new TeamMember(newTeam, member);
                                try {
                                    tms.invite(findMember, newTeamMember);
                                } catch (AccessException e) {
                                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Admin");
                                } catch (IllegalArgumentException e) {
                                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invite Member Fail");
                                }
                            }

                            return ResponseEntity.status(HttpStatus.CREATED).body("Create Team Success");
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Create Team Fail");
                        }
                    }
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Member Fail");
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    /**
     * 그룹에 회원 초대
     *
     * @param inviteForm 회원 초대 폼
     * @param request    요청
     * @return 성공 시 200 상태 코드, 실패 시 404 상태 코드, 권한 없을 시 401 상태 코드, 세션 없을 시 401 상태 코드
     */
    @PostMapping("/invite")
    public ResponseEntity<?> inviteMember(@RequestBody TeamInviteForm inviteForm, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.search(loginMember.getMemberId());

                    if (findMember != null) {
                        Long teamId = inviteForm.getTeamId();
                        try {
                            Team ft = ts.search(teamId);

                            if (ft != null) {
                                List<Long> memberIdList = inviteForm.getMembers();
                                List<Member> memberList = new ArrayList<>();

                                for (Long memberId : memberIdList) {
                                    try {
                                        Member fm = ms.search(memberId);

                                        if (fm != null) {
                                            memberList.add(fm);
                                        }
                                    } catch (IllegalArgumentException e) {
                                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Member Fail");
                                    }
                                }

                                for (Member member : memberList) {
                                    TeamMember newTeamMember = new TeamMember(ft, member);
                                    try {
                                        tms.invite(findMember, newTeamMember);
                                    } catch (AccessException e) {
                                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Admin");
                                    } catch (IllegalArgumentException e) {
                                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invite Member In Team Fail");
                                    }
                                }

                                return ResponseEntity.status(HttpStatus.OK).body("Invite Member In Team Success");
                            }
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Team Fail");
                        }
                    }
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Member Fail");
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    /**
     * 그룹에 회원 추방
     *
     * @param teamMemberDTO 그룹 회원 DTO
     * @param request       요청
     * @return 성공 시 200 상태 코드, 실패 시 404 상태 코드, 권한 없을 시 401 상태 코드, 세션 없을 시 401 상태 코드
     */
    @PostMapping("/expel")
    public ResponseEntity<?> expelMember(@RequestBody TeamMemberDTO teamMemberDTO, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.search(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            Member fm = ms.search(teamMemberDTO.getMemberId());

                            try {
                                Team ft = ts.search(teamMemberDTO.getTeamId());

                                if (fm != null && ft != null) {
                                    try {
                                        TeamMember ftm = tms.search(ft, fm);

                                        if (ftm != null) {
                                            try {
                                                tms.expel(findMember, ftm);
                                                return ResponseEntity.status(HttpStatus.OK).body("Expel Member Success");
                                            } catch (AccessException e) {
                                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Admin");
                                            } catch (IllegalArgumentException e) {
                                                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Expel Member Fail");
                                            }
                                        }
                                    } catch (IllegalArgumentException e) {
                                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Member In Team Fail");
                                    }
                                }
                            } catch (IllegalArgumentException e) {
                                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Team Fail");
                            }
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Member Fail");
                        }
                    }
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Member Fail");
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    /**
     * 그룹 수정
     *
     * @param updateForm 그룹 수정 폼
     * @param request    요청
     * @return 성공 시 200 상태 코드, 실패 시 404 상태 코드, 권한 없을 시 401 상태 코드, 세션 없을 시 401 상태 코드
     */
    @PostMapping("/update")
    public ResponseEntity<?> updateTeam(@RequestBody TeamUpdateForm updateForm, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.search(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            Team ft = ts.search(updateForm.getTeamId());

                            if (ft != null) {
                                ft.changeName(updateForm.getName());

                                try {
                                    ts.update(findMember, ft);
                                    return ResponseEntity.status(HttpStatus.OK).body("Update Team Success");
                                } catch (AccessException e) {
                                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Admin");
                                } catch (IllegalArgumentException e) {
                                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Update Team Success");
                                }
                            }
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Team Fail");
                        }
                    }
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Member Fail");
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    /**
     * 그룹 삭제(나가기)
     *
     * @param teamDTO 그룹 DTO
     * @param request 요청
     * @return 성공 시 200 상태 코드, 실패 시 404 상태 코드, 세션 없을 시 401 상태 코드
     */
    @PostMapping("/delete")
    public ResponseEntity<?> deleteTeam(@RequestBody TeamDTO teamDTO, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.search(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            Team ft = ts.search(teamDTO.getTeamId());

                            if (ft != null) {
                                try {
                                    ts.delete(findMember, ft);
                                    return ResponseEntity.status(HttpStatus.OK).body("Delete Team Success");
                                } catch (IllegalArgumentException e) {
                                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Delete Team Fail");
                                }
                            }
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Team Fail");
                        }
                    }
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Member Fail");
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    /**
     * 관리자 권한 부여
     *
     * @param teamMemberDTO 그룹 회원 DTO
     * @param request       요청
     * @return 성공 시 200 상태 코드, 실패 시 404 상태 코드, 권한 없을 시 401 상태 코드, 세션 없을 시 401 상태 코드
     */
    @PostMapping("/authorize")
    public ResponseEntity<?> authorizeAdmin(@RequestBody TeamMemberDTO teamMemberDTO, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.search(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            Member fm = ms.search(teamMemberDTO.getMemberId());

                            try {
                                Team ft = ts.search(teamMemberDTO.getTeamId());

                                if (fm != null && ft != null) {
                                    try {
                                        TeamMember ftm = tms.search(ft, fm);

                                        if (ftm != null) {
                                            try {
                                                tms.authorizeAdmin(findMember, ftm);
                                                return ResponseEntity.status(HttpStatus.OK).body("Authorize Admin Success");
                                            } catch (AccessException e) {
                                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Admin");
                                            } catch (IllegalArgumentException e) {
                                                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Authorize Admin Fail");
                                            }
                                        }
                                    } catch (IllegalArgumentException e) {
                                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Member In Team Fail");
                                    }
                                }
                            } catch (IllegalArgumentException e) {
                                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Team Fail");
                            }
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Member Fail");
                        }
                    }
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Member Fail");
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    /**
     * 관리자 권한 박탈
     *
     * @param teamMemberDTO 그룹 회원 DTO
     * @param request       요청
     * @return 성공 시 200 상태 코드, 실패 시 404 상태 코드, 권한 없을 시 401 상태 코드, 세션 없을 시 401 상태 코드
     */
    @PostMapping("/revoke")
    public ResponseEntity<?> revokeAdmin(@RequestBody TeamMemberDTO teamMemberDTO, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.search(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            Member fm = ms.search(teamMemberDTO.getMemberId());
                            try {
                                Team ft = ts.search(teamMemberDTO.getTeamId());

                                if (fm != null && ft != null) {
                                     try {
                                         TeamMember ftm = tms.search(ft, fm);

                                         if (ftm != null) {
                                             try {
                                                 tms.revokeAdmin(findMember, ftm);
                                                 return ResponseEntity.status(HttpStatus.OK).body("Revoke Admin Success");
                                             } catch (AccessException e) {
                                                 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Admin");
                                             } catch (IllegalArgumentException e) {
                                                 return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Revoke Admin Fail");
                                             }
                                         }
                                     } catch (IllegalArgumentException e) {
                                         return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Member In Team Fail");
                                     }
                                }
                            } catch (IllegalArgumentException e) {
                                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Team Fail");
                            }
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Member Fail");
                        }
                    }
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Member Fail");
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    /**
     * 회원이 가진 그룹 리스트
     *
     * @param request 요청
     * @return 성공 시 200 상태 코드와 그룹 리스트, 실패 시 404 상태 코드, 세션 없을 시 401 상태 코드
     */
    @GetMapping("/list")
    public ResponseEntity<?> teamListByMember(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.search(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            List<Team> teamList = tms.searchTeamList(findMember);
                            List<TeamDTO> teamDTOList = new ArrayList<>();

                            for (Team team : teamList) {
                                TeamDTO teamDTO = mapping(team);
                                teamDTOList.add(teamDTO);
                            }

                            return ResponseEntity.status(HttpStatus.OK).body(teamDTOList);
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found TeamList Fail");
                        }
                    }
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Member Fail");
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    /**
     * 그룹 이름으로 검색
     *
     * @param name    그룹 이름
     * @param request 요청
     * @return 성공 시 200 상태 코드와 그룹 리스트, 실패 시 404 상태 코드, 세션 없을 시 401 상태 코드
     */
    @GetMapping("/list/name")
    public ResponseEntity<?> searchTeamByTeamName(@RequestParam("name") String name, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.search(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            List<Team> teamList = tms.searchTeamList(findMember, name);
                            List<TeamDTO> teamDTOList = new ArrayList<>();

                            for (Team team : teamList) {
                                TeamDTO teamDTO = mapping(team);
                                teamDTOList.add(teamDTO);
                            }

                            return ResponseEntity.status(HttpStatus.OK).body(teamDTOList);
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found TeamList Fail");
                        }
                    }
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Member Fail");
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    /**
     * 그룹에 속한 회원 리스트
     *
     * @param teamId  그룹 id
     * @param request 요청
     * @return 성공 시 200 상태 코드와 회원 리스트, 실패 시 404 상태 코드, 세션 없을 시 401 상태 코드
     */
    @GetMapping("/memberlist/teamId")
    public ResponseEntity<?> memberListByTeam(@RequestParam("teamId") Long teamId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.search(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            Team ft = ts.search(teamId);

                            if (ft != null) {
                                Member owner = ft.getOwner();
                                try {
                                    List<Team> teamList = tms.searchTeamList(findMember);

                                    if (teamList.contains(ft)) {
                                        try {
                                            List<Member> memberList = tms.searchMemberList(ft);
                                            try {
                                                List<Member> adminList = tms.searchAdminList(ft);
                                                List<MemberInTeamDTO> memberDTOList = new ArrayList<>();

                                                for (Member member : memberList) {
                                                    MemberInTeamDTO memberDTO = mapping(member);

                                                    if (owner.equals(member)) {
                                                        memberDTO.setIsOwner(true);
                                                    } else {
                                                        memberDTO.setIsOwner(false);
                                                    }

                                                    if (adminList.contains(member)) {
                                                        memberDTO.setIsAdmin(true);
                                                    } else {
                                                        memberDTO.setIsAdmin(false);
                                                    }

                                                    memberDTOList.add(memberDTO);
                                                }

                                                return ResponseEntity.status(HttpStatus.OK).body(memberDTOList);
                                            } catch (IllegalArgumentException e) {
                                                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found AdminList Fail");
                                            }
                                        } catch (IllegalArgumentException e) {
                                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found MemberList Fail");
                                        }
                                    }
                                } catch (IllegalArgumentException e) {
                                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found TeamList Fail");
                                }
                            }
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Team Fail");
                        }
                    }
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Member Fail");
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    /**
     * 그룹 관리자 리스트
     *
     * @param teamId  그룹 id
     * @param request 요청
     * @return 성공 시 200 상태 코드와 관리자 리스트, 실패 시 404 상태 코드, 세션 없을 시 401 상태 코드
     */
    @GetMapping("/adminlist/teamId")
    public ResponseEntity<?> adminListByTeam(@RequestParam("teamId") Long teamId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.search(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            Team ft = ts.search(teamId);

                            if (ft != null) {
                                Member owner = ft.getOwner();

                                if (owner.equals(findMember)) {
                                    try {
                                        List<Member> adminList = tms.searchAdminList(ft);
                                        List<MemberInTeamDTO> memberDTOList = new ArrayList<>();

                                        for (Member member : adminList) {
                                            MemberInTeamDTO memberDTO = mapping(member);
                                            memberDTO.setIsAdmin(true);
                                            memberDTOList.add(memberDTO);
                                        }

                                        return ResponseEntity.status(HttpStatus.OK).body(memberDTOList);
                                    } catch (IllegalArgumentException e) {
                                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found AdminList Fail");
                                    }
                                } else {
                                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Owner");
                                }
                            }
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Team Fail");
                        }
                    }
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Member Fail");
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    /**
     * Team -> TeamDTO
     *
     * @param team 그룹
     * @return 데이터 전송 객체
     */
    private TeamDTO mapping(Team team) {
        TeamDTO teamDTO = new TeamDTO();
        teamDTO.setTeamId(team.getId());
        teamDTO.setName(team.getName());
        teamDTO.setOwner(team.getOwner().getName());
        return teamDTO;
    }

    /**
     * Member -> MemberInTeamDTO
     *
     * @param member 회원
     * @return 데이터 전송 객체
     */
    private MemberInTeamDTO mapping(Member member) {
        MemberInTeamDTO memberDTO = new MemberInTeamDTO();
        memberDTO.setMemberId(member.getId());
        memberDTO.setName(member.getName());
        return memberDTO;
    }
}
