package yuhan.hgcq.server.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yuhan.hgcq.server.domain.Follow;
import yuhan.hgcq.server.domain.Member;
import yuhan.hgcq.server.domain.Team;
import yuhan.hgcq.server.dto.follow.FollowDTO;
import yuhan.hgcq.server.dto.follow.Follower;
import yuhan.hgcq.server.dto.member.MemberDTO;
import yuhan.hgcq.server.service.FollowService;
import yuhan.hgcq.server.service.MemberService;
import yuhan.hgcq.server.service.TeamMemberService;
import yuhan.hgcq.server.service.TeamService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/follow")
public class FollowController {

    private final FollowService fs;
    private final MemberService ms;
    private final TeamService ts;
    private final TeamMemberService tms;

    /**
     * 팔로우 추가
     *
     * @param dto     팔로우
     * @param request 요청
     * @return 성공 시 201 상태 코드, 실패 시 404 상태 코드, 세션 없을 시 401 상태 코드
     */
    @PostMapping("/add")
    public ResponseEntity<?> addFollow(@RequestBody FollowDTO dto, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.search(loginMember.getMemberId());

                    if (findMember != null) {
                        Long followId = dto.getFollowId();

                        try {
                            Member follow = ms.search(followId);

                            if (follow != null) {
                                Follow following = new Follow(findMember, follow);
                                try {
                                    fs.add(following);
                                    return ResponseEntity.status(HttpStatus.CREATED).body("Add Following Success");
                                } catch (IllegalArgumentException e) {
                                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Add Following Fail");
                                }
                            }
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Follow Member Fail");
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
     * 팔로우 삭제
     *
     * @param dto     팔로우
     * @param request 요청
     * @return 성공 시 200 상태 코드, 실패 시 404 상태 코드, 세션 없을 시 401 상태 코드
     */
    @PostMapping("/delete")
    public ResponseEntity<?> deleteFollow(@RequestBody FollowDTO dto, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.search(loginMember.getMemberId());

                    if (findMember != null) {
                        Long followId = dto.getFollowId();

                        try {
                            Member follow = ms.search(followId);

                            if (follow != null) {
                                Follow ff = fs.search(findMember, follow);

                                if (ff != null) {
                                    try {
                                        fs.remove(ff);
                                        return ResponseEntity.status(HttpStatus.OK).body("Delete Following Success");
                                    } catch (IllegalArgumentException e) {
                                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Delete Following Fail");
                                    }
                                }
                            }
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Follow Member Fail");
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
     * 팔로잉 리스트
     *
     * @param request 요청
     * @return 성공 시 200 상태 코드와 팔로잉 리스트, 실패 시 404 상태 코드, 세션 없을 시 401 상태 코드
     */
    @GetMapping("/followinglist")
    public ResponseEntity<?> followingList(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.search(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            List<Member> followingList = fs.searchFollowing(findMember);
                            List<MemberDTO> dtoList = new ArrayList<>();

                            for (Member following : followingList) {
                                MemberDTO dto = mapping(following);
                                dtoList.add(dto);
                            }

                            return ResponseEntity.status(HttpStatus.OK).body(dtoList);
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found followingList Fail");
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
     * 팔로잉 리스트 이름으로 검색
     *
     * @param name    이름
     * @param request 요청
     * @return 성공 시 200 상태 코드와 팔로잉 리스트, 실패 시 404 상태 코드, 세션 없을 시 401 상태 코드
     */
    @GetMapping("/followinglist/name")
    public ResponseEntity<?> searchFollowingListByName(@RequestParam("name") String name, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.search(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            List<Member> followingList = fs.searchFollowingByName(findMember, name);
                            List<MemberDTO> dtoList = new ArrayList<>();

                            for (Member following : followingList) {
                                MemberDTO dto = mapping(following);
                                dtoList.add(dto);
                            }

                            return ResponseEntity.status(HttpStatus.OK).body(dtoList);
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found followingList By Name Fail");
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
     * 초대할 팔로잉 리스트
     *
     * @param request 요청
     * @return 성공 시 200 상태 코드와 팔로잉 리스트, 실패 시 404 상태 코드, 세션 없을 시 401 상태 코드
     */
    @GetMapping("/followinglist/teamId")
    public ResponseEntity<?> inviteFollowingList(@RequestParam("teamId") Long teamId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.search(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            List<Member> followingList = fs.searchFollowing(findMember);
                            try {
                                Team ft = ts.search(teamId);
                                List<Member> memberList = tms.searchMemberList(ft);
                                List<MemberDTO> dtoList = new ArrayList<>();

                                for (Member following : followingList) {
                                    if (memberList.contains(following)) {
                                        continue;
                                    }
                                    MemberDTO dto = mapping(following);
                                    dtoList.add(dto);
                                }

                                return ResponseEntity.status(HttpStatus.OK).body(dtoList);
                            } catch (IllegalArgumentException e) {
                                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Team Fail");
                            }
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found followingList Fail");
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
     * 팔로워 리스트
     *
     * @param request 요청
     * @return 성공 시 200 상태 코드와 팔로워 리스트, 실패 시 404 상태 코드, 세션 없을 시 401 상태 코드
     */
    @GetMapping("/followerlist")
    public ResponseEntity<?> followerList(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.search(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            List<Member> followerList = fs.searchFollower(findMember);
                            List<Member> followingList = fs.searchFollowing(findMember);
                            List<MemberDTO> followerDtoList = new ArrayList<>();
                            List<MemberDTO> followingDtoList = new ArrayList<>();


                            for (Member follower : followerList) {
                                MemberDTO dto = mapping(follower);
                                followerDtoList.add(dto);
                            }

                            for (Member following : followingList) {
                                MemberDTO dto = mapping(following);
                                followingDtoList.add(dto);
                            }

                            Follower follower = new Follower();
                            follower.setFollowerList(followerDtoList);
                            follower.setFollowingList(followingDtoList);

                            return ResponseEntity.status(HttpStatus.OK).body(follower);
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found followerList Fail");
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
     * 팔로워 리스트 이름으로 검색
     *
     * @param name    이름
     * @param request 요청
     * @return 성공 시 200 상태 코드와 팔로워 리스트, 실패 시 404 상태 코드, 세션 없을 시 401 상태 코드
     */
    @GetMapping("/followerlist/name")
    public ResponseEntity<?> searchFollowerListByName(@RequestParam("name") String name, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.search(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            List<Member> followerList = fs.searchFollowerByName(findMember, name);
                            List<Member> followingList = fs.searchFollowing(findMember);
                            List<MemberDTO> followerDtoList = new ArrayList<>();
                            List<MemberDTO> followingDtoList = new ArrayList<>();


                            for (Member follower : followerList) {
                                MemberDTO dto = mapping(follower);
                                followerDtoList.add(dto);
                            }

                            for (Member following : followingList) {
                                MemberDTO dto = mapping(following);
                                followingDtoList.add(dto);
                            }

                            Follower follower = new Follower();
                            follower.setFollowerList(followerDtoList);
                            follower.setFollowingList(followingDtoList);

                            return ResponseEntity.status(HttpStatus.OK).body(follower);
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.OK).body("Found followerList By Name Fail");
                        }
                    }
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.OK).body("Found Member Fail");
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    /**
     * Member -> MemberDTO
     *
     * @param member 회원
     * @return MemberDTO
     */
    private MemberDTO mapping(Member member) {
        MemberDTO dto = new MemberDTO();
        dto.setMemberId(member.getId());
        dto.setName(member.getName());
        dto.setEmail(member.getEmail());
        return dto;
    }
}
