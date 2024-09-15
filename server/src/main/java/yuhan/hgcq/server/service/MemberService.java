package yuhan.hgcq.server.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuhan.hgcq.server.domain.Member;
import yuhan.hgcq.server.dto.member.LoginForm;
import yuhan.hgcq.server.dto.member.MemberUpdateForm;
import yuhan.hgcq.server.dto.member.SignupForm;
import yuhan.hgcq.server.repository.MemberRepository;

import java.util.List;

/**
 * 회원 기능 요구사항 분석
 * 1. 회원가입을 할 때 이름과 이메일은 중복이 안되도록 한다.
 * 2. 로그인은 이메일과 패스워드로 한다.
 * 3. 회원 정보 수정은 이름과 패스워드만 가능하다.
 * 4. 전체 회원 리스트를 추출할 수 있다.
 * 5. 회원 리스트를 이름으로 검색할 수 있다.
 */

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private static final Logger log = LoggerFactory.getLogger(MemberService.class);

    private final MemberRepository mr;

    /**
     * 회원 가입
     *
     * @param form 회원 가입 폼
     * @return 회원 id
     */
    @Transactional
    public synchronized Long join(SignupForm form) throws IllegalArgumentException {
        String name = form.getName();
        String email = form.getEmail();
        String password = form.getPassword();

        if (!duplicateEmail(email)) {
            throw new IllegalArgumentException("Already Exist Email");
        }

        if (!duplicateName(name)) {
            throw new IllegalArgumentException("Already Exist Name");
        }

        Member member = new Member(name, email, password);
        mr.save(member);

        log.info("Join Member : {}", member);
        return member.getId();
    }

    /**
     * 로그인
     *
     * @param loginForm 로그인 폼
     * @return 로그인 유무
     */
    @Transactional
    public Member login(LoginForm loginForm) throws IllegalArgumentException {
        List<String> emailList = mr.findAllEmails();

        String memberEmail = loginForm.getEmail();
        String memberPassword = loginForm.getPassword();

        if (!emailList.contains(memberEmail)) {
            log.error("Login Error (Not exist Email) : {}", memberEmail);
            throw new IllegalArgumentException("Not exist Email");
        }

        Member fm = mr.findOne(memberEmail);

        if (fm.getPassword().equals(memberPassword)) {
            log.info("Login Success : {}", fm);
            return fm;
        } else {
            log.info("Login Error (Wrong Password) : {}", memberEmail);
            throw new IllegalArgumentException("Wrong Password");
        }
    }

    /**
     * 회원 정보 수정
     *
     * @param member 회원
     * @param form   회원 정보 수정 폼
     */
    @Transactional
    public void update(Member member, MemberUpdateForm form) throws IllegalArgumentException {
        ensureNotNull(member, "Member");

        String newName = form.getName();
        String newPassword = form.getPassword();

        if (newName != null && !duplicateName(newName)) {
            member.changeName(newName);
        }

        if (newPassword != null) {
            member.changePassword(newPassword);
        }

        mr.save(member);
        log.info("Update Member : {}", member);
    }

    /**
     * 회원 검색
     *
     * @param id 회원 id
     * @return 회원
     */
    public Member search(Long id) throws IllegalArgumentException {
        Member fm = mr.findOne(id);

        if (fm == null) {
            throw new IllegalArgumentException("Member Not Found");
        }

        return fm;
    }

    /**
     * 회원 이메일로 검색
     *
     * @param email 이메일
     * @return 회원
     */
    public Member searchByEmail(String email) throws IllegalArgumentException {
        Member fm = mr.findOne(email);

        if (fm == null) {
            throw new IllegalArgumentException("Member Not Found");
        }

        return fm;
    }

    /**
     * 회원 이름으로 검색
     *
     * @param name 이름
     * @return 회원 리스트
     */
    public List<Member> searchByName(String name) throws IllegalArgumentException {
        return mr.findByName(name);
    }

    /**
     * 회원 리스트
     *
     * @return 회원 리스트
     */
    public List<Member> searchAll() {
        return mr.findAll();
    }

    /**
     * 이메일 중복 검사
     *
     * @param email 이메일
     * @return 중복 유무
     */
    public boolean duplicateEmail(String email) {
        List<String> emails = mr.findAllEmails();
        return !emails.contains(email);
    }

    /**
     * 닉네임 중복 검사
     *
     * @param name 닉네임
     * @return 중복 유무
     */
    public boolean duplicateName(String name) {
        List<String> names = mr.findAllNames();
        return !names.contains(name);
    }

    /* 매개변수가 null 값인지 확인 */
    private void ensureNotNull(Object obj, String name) {
        if (obj == null) {
            throw new IllegalArgumentException(name + " is null");
        }
    }
}
