package yuhan.hgcq.server.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
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
     * @return 성공 시 201 상태 코드, 실패 시 401 상태 코드
     */
    @PostMapping("/create")
    public ResponseEntity<?> createTeam(@RequestBody TeamCreateForm createForm, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                Member findMember = ms.search(loginMember.getMemberId());

                if (findMember != null) {
                    Team newTeam = new Team(findMember, createForm.getName());
                    ts.create(newTeam);

                    List<Long> memberIdList = createForm.getMembers();
                    List<Member> memberList = new ArrayList<>();

                    for (Long memberId : memberIdList) {
                        Member fm = ms.search(memberId);
                        if (fm != null) {
                            memberList.add(fm);
                        }
                    }

                    for (Member member : memberList) {
                        TeamMember newTeamMember = new TeamMember(newTeam, member);
                        try {
                            tms.invite(findMember, newTeamMember);
                        } catch (AccessException e) {
                            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Admin");
                        }
                    }

                    return ResponseEntity.status(HttpStatus.CREATED).build();
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
     * @return 성공 시 200 상태 코드, 실패 시 401 상태 코드
     */
    @PostMapping("/invite")
    public ResponseEntity<?> inviteMember(@RequestBody TeamInviteForm inviteForm, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                Member findMember = ms.search(loginMember.getMemberId());

                if (findMember != null) {
                    Long teamId = inviteForm.getTeamId();
                    Team ft = ts.search(teamId);

                    if (ft != null) {
                        List<Long> memberIdList = inviteForm.getMembers();
                        List<Member> memberList = new ArrayList<>();

                        for (Long memberId : memberIdList) {
                            Member fm = ms.search(memberId);
                            if (fm != null) {
                                memberList.add(fm);
                            }
                        }

                        for (Member member : memberList) {
                            TeamMember newTeamMember = new TeamMember(ft, member);
                            try {
                                tms.invite(findMember, newTeamMember);
                            } catch (AccessException e) {
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Admin");
                            }
                        }

                        return ResponseEntity.status(HttpStatus.OK).build();
                    }
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
     * @return 성공 시 200 상태 코드, 실패 시 401 상태 코드
     */
    @PostMapping("/expel")
    public ResponseEntity<?> expelMember(@RequestBody TeamMemberDTO teamMemberDTO, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                Member findMember = ms.search(loginMember.getMemberId());

                if (findMember != null) {
                    Member fm = ms.search(teamMemberDTO.getMemberId());
                    Team ft = ts.search(teamMemberDTO.getTeamId());

                    if (fm != null && ft != null) {
                        TeamMember ftm = tms.search(ft, fm);

                        if (ftm != null) {
                            try {
                                tms.expel(findMember, ftm);
                            } catch (AccessException e) {
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Admin");
                            }

                            return ResponseEntity.status(HttpStatus.OK).build();
                        }
                    }
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
     * @return 성공 시 200 상태 코드, 실패 시 401 상태 코드
     */
    @PostMapping("/update")
    public ResponseEntity<?> updateTeam(@RequestBody TeamUpdateForm updateForm, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                Member findMember = ms.search(loginMember.getMemberId());

                if (findMember != null) {
                    Team ft = ts.search(updateForm.getTeamId());

                    if (ft != null) {
                        ft.changeName(updateForm.getName());

                        try {
                            ts.update(findMember, ft);
                        } catch (AccessException e) {
                            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Admin");
                        }

                        return ResponseEntity.status(HttpStatus.OK).build();
                    }
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
     * @return 성공 시 200 상태 코드, 실패 시 401 상태 코드
     */
    @PostMapping("/delete")
    public ResponseEntity<?> deleteTeam(@RequestBody TeamDTO teamDTO, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                Member findMember = ms.search(loginMember.getMemberId());

                if (findMember != null) {
                    Team ft = ts.search(teamDTO.getTeamId());

                    if (ft != null) {
                        ts.delete(findMember, ft);

                        return ResponseEntity.status(HttpStatus.OK).build();
                    }
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
     * @return 성공 시 200 상태 코드, 실패 시 401 상태 코드
     */
    @PostMapping("/authorize")
    public ResponseEntity<?> authorizeAdmin(@RequestBody TeamMemberDTO teamMemberDTO, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                Member findMember = ms.search(loginMember.getMemberId());

                if (findMember != null) {
                    Member fm = ms.search(teamMemberDTO.getMemberId());
                    Team ft = ts.search(teamMemberDTO.getTeamId());

                    if (fm != null && ft != null) {
                        TeamMember ftm = tms.search(ft, fm);

                        if (ftm != null) {
                            try {
                                tms.authorizeAdmin(findMember, ftm);
                            } catch (AccessException e) {
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Admin");
                            }

                            return ResponseEntity.status(HttpStatus.OK).build();
                        }
                    }
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
     * @return 성공 시 200 상태 코드, 실패 시 401 상태 코드
     */
    @PostMapping("/revoke")
    public ResponseEntity<?> revokeAdmin(@RequestBody TeamMemberDTO teamMemberDTO, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                Member findMember = ms.search(loginMember.getMemberId());

                if (findMember != null) {
                    Member fm = ms.search(teamMemberDTO.getMemberId());
                    Team ft = ts.search(teamMemberDTO.getTeamId());

                    if (fm != null && ft != null) {
                        TeamMember ftm = tms.search(ft, fm);

                        if (ftm != null) {
                            try {
                                tms.revokeAdmin(findMember, ftm);
                            } catch (AccessException e) {
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Admin");
                            }

                            return ResponseEntity.status(HttpStatus.OK).build();
                        }
                    }
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    /**
     * 회원이 가진 그룹 리스트
     *
     * @param request 요청
     * @return 성공 시 200 상태 코드와 그룹 리스트, 실패 시 401 상태 코드
     */
    @GetMapping("/list")
    public ResponseEntity<?> teamListByMember(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                Member findMember = ms.search(loginMember.getMemberId());

                if (findMember != null) {
                    List<Team> teamList = tms.searchTeamList(findMember);
                    List<TeamDTO> teamDTOList = new ArrayList<>();

                    for (Team team : teamList) {
                        TeamDTO teamDTO = mapping(team);
                        teamDTOList.add(teamDTO);
                    }

                    return ResponseEntity.status(HttpStatus.OK).body(teamDTOList);
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
     * @return 성공 시 200 상태 코드와 그룹 리스트, 실패 시 401 상태 코드
     */
    @GetMapping("/list/name")
    public ResponseEntity<?> searchTeamByTeamName(@RequestParam("name") String name, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                Member findMember = ms.search(loginMember.getMemberId());

                if (findMember != null) {
                    List<Team> teamList = tms.searchTeamList(findMember, name);
                    List<TeamDTO> teamDTOList = new ArrayList<>();

                    for (Team team : teamList) {
                        TeamDTO teamDTO = mapping(team);
                        teamDTOList.add(teamDTO);
                    }

                    return ResponseEntity.status(HttpStatus.OK).body(teamDTOList);
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
     * @return 성공 시 200 상태 코드와 회원 리스트, 실패 시 401 상태 코드
     */
    @GetMapping("/memberlist/teamId")
    public ResponseEntity<?> memberListByTeam(@RequestParam("teamId") Long teamId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                Member findMember = ms.search(loginMember.getMemberId());

                if (findMember != null) {
                    Team ft = ts.search(teamId);

                    if (ft != null) {
                        Member owner = ft.getOwner();
                        List<Team> teamList = tms.searchTeamList(findMember);

                        if (teamList.contains(ft)) {
                            List<Member> memberList = tms.searchMemberList(ft);
                            List<Member> adminList = tms.searchAdminList(ft);
                            List<MemberInTeamDTO> memberDTOList = new ArrayList<>();

                            for (Member member : memberList) {
                                MemberInTeamDTO memberDTO = mapping(member);

                                if (owner.equals(member)) {
                                    memberDTO.setIsOwner(true);
                                }

                                if (adminList.contains(member)) {
                                    memberDTO.setIsAdmin(true);
                                }

                                memberDTOList.add(memberDTO);
                            }

                            return ResponseEntity.status(HttpStatus.OK).body(memberDTOList);
                        }
                    }
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
     * @return 성공 시 200 상태 코드와 관리자 리스트, 실패 시 401 상태 코드
     */
    @GetMapping("/adminlist/teamId")
    public ResponseEntity<?> adminListByTeam(@RequestParam("teamId") Long teamId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                Member findMember = ms.search(loginMember.getMemberId());

                if (findMember != null) {
                    Team ft = ts.search(teamId);

                    if (ft != null) {
                        Member owner = ft.getOwner();

                        if (owner.equals(findMember)) {
                            List<Member> adminList = tms.searchAdminList(ft);
                            List<MemberInTeamDTO> memberDTOList = new ArrayList<>();

                            for (Member member : adminList) {
                                MemberInTeamDTO memberDTO = mapping(member);
                                memberDTO.setIsAdmin(true);
                                memberDTOList.add(memberDTO);
                            }

                            return ResponseEntity.status(HttpStatus.OK).body(memberDTOList);
                        } else {
                            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Owner");
                        }
                    }
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
