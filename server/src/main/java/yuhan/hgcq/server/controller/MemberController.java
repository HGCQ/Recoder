package yuhan.hgcq.server.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.web.bind.annotation.*;
import yuhan.hgcq.server.domain.Member;
import yuhan.hgcq.server.dto.member.*;
import yuhan.hgcq.server.service.FollowService;
import yuhan.hgcq.server.service.MemberService;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private static final Logger log = LoggerFactory.getLogger(MemberController.class);

    private final MemberService ms;
    private final FollowService fs;
    private final SessionRepository<? extends Session> sessionRepository;

    /**
     * 회원 가입
     *
     * @param form 회원 가입 폼
     * @return 성공시 201 상태 코드, 실패 시 400 상태 코드
     */
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody SignupForm form) {
        try {
            ms.join(form);
            return ResponseEntity.status(HttpStatus.CREATED).body("Join Member Success");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Join Member Fail");
        }
    }

    /**
     * 로그인
     *
     * @param form    로그인 폼
     * @param request 요청
     * @return 성공 시 200 상태 코드와 memberDTO, 실패 시 404 상태 코드
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginForm form, HttpServletRequest request) {
        try {
            Member loginMember = ms.login(form);

            if (loginMember != null) {
                MemberDTO memberDTO = mapping(loginMember);

                HttpSession session = request.getSession();
                session.setAttribute("member", memberDTO);

                log.info("Session Create : {}", memberDTO);
                return ResponseEntity.status(HttpStatus.OK).body(memberDTO);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Login Fail");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Login Fail");
        }
    }

    /**
     * 로그아웃
     *
     * @param request 요청
     * @return 성공 시 200 상태 코드, 실패 시 400 상태 코드
     */
    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();

            log.info("Session Invalidate : {}", session);
            return ResponseEntity.status(HttpStatus.OK).body("Logout Success");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Logout Fail");
        }
    }

    /**
     * 정보 수정
     *
     * @param form    정보 수정 폼
     * @param request 요청
     * @return 성공 시 200 상태 코드, 실패 시 404 상태 코드, 세션 없을 시 401 상태 코드
     */
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody MemberUpdateForm form, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.search(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            ms.update(findMember, form);
                            return ResponseEntity.status(HttpStatus.OK).body("Member Update Success");
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Member Update Fail");
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
     * 이메일 중복 확인
     *
     * @param email   이메일
     * @param request 요청
     * @return 성공 시 200 상태 코드와 중복 유무
     */
    @GetMapping("/duplicate/email")
    public ResponseEntity<?> duplicateEmail(@RequestParam("email") String email, HttpServletRequest request) {
        boolean isDuplicateEmail = ms.duplicateEmail(email);

        if (isDuplicateEmail) {
            return ResponseEntity.status(HttpStatus.OK).body(true);
        }

        return ResponseEntity.status(HttpStatus.OK).body(false);
    }

    /**
     * 닉네임 중복 확인
     *
     * @param name    닉네임
     * @param request 요청
     * @return 성공 시 200 상태 코드와 중복 유무
     */
    @GetMapping("/duplicate/name")
    public ResponseEntity<?> duplicateName(@RequestParam("name") String name, HttpServletRequest request) {
        boolean isDuplicateName = ms.duplicateName(name);

        if (isDuplicateName) {
            return ResponseEntity.status(HttpStatus.OK).body(true);
        }

        return ResponseEntity.status(HttpStatus.OK).body(false);
    }

    /**
     * 회원 리스트
     *
     * @param request 요청
     * @return 성공 시 200 상태 코드와 회원 리스트, 실패 시 404 상태 코드, 세션 없을 시 401 상태 코드
     */
    @GetMapping("/list")
    public ResponseEntity<?> memberList(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.search(loginMember.getMemberId());

                    if (findMember != null) {
                        List<Member> memberList = ms.searchAll();
                        List<Member> followingList = fs.searchFollowing(findMember);

                        List<MemberDTO> memberDtoList = new ArrayList<>();
                        List<MemberDTO> followingDtoList = new ArrayList<>();

                        for (Member member : memberList) {
                            MemberDTO dto = mapping(member);
                            memberDtoList.add(dto);
                        }

                        for (Member following : followingList) {
                            MemberDTO dto = mapping(following);
                            followingDtoList.add(dto);
                        }

                        Members members = new Members();
                        members.setMemberList(memberDtoList);
                        members.setFollowingList(followingDtoList);

                        return ResponseEntity.status(HttpStatus.OK).body(members);
                    }
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Member Fail");
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    /**
     * 로그인 유무 확인
     *
     * @param request 요청
     * @return 성공 시 200 상태 코드와 회원 정보, 실패 시 401 상태 코드
     */
    @GetMapping("/islogin")
    public ResponseEntity<?> isLogin(HttpServletRequest request) {
        String sessionId = null;
        String decodedString = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("SESSION".equals(cookie.getName())) {
                    sessionId = cookie.getValue();
                    byte[] decodedBytes = Base64.getDecoder().decode(sessionId);
                    decodedString = new String(decodedBytes);
                    log.debug(sessionId);
                    log.debug(decodedString);
                    break;
                }
            }
        }

        if (sessionId != null) {
            Session session = sessionRepository.findById(decodedString);

            if (session != null) {
                MemberDTO loginMember = (MemberDTO) session.getAttribute("member");
                return ResponseEntity.status(HttpStatus.OK).body(loginMember);
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
