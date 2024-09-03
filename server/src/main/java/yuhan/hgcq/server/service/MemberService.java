package yuhan.hgcq.server.service;

import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuhan.hgcq.server.domain.Member;
import yuhan.hgcq.server.dto.member.LoginForm;
import yuhan.hgcq.server.dto.member.SignupForm;
import yuhan.hgcq.server.repository.MemberRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private static final Logger log = LoggerFactory.getLogger(MemberService.class);

    private final MemberRepository memberRepository;

    @Transactional
    public synchronized Long join(Member member){

        String email = member.getEmail();
        String name = member.getName();
        if(!duplicateEmail(email)){
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        if(!duplicateName(name)){
            throw new IllegalArgumentException("이미 존재하는 이름입니다.");
        }

        memberRepository.save(member);
        log.debug("회원가입 성공");
        return  member.getId();
    }

    @Transactional
    public boolean login(LoginForm loginForm){
        List<String> emails = memberRepository.findAllEmails();

        String memberEmail = loginForm.getEmail();
        String memberpassword = loginForm.getPassword();

        if (!emails.contains(memberEmail)){
            log.error("로그인 실패: 존재하지 않는 아이디");
            throw  new IllegalArgumentException("존재하지 않는 아이디");
        }

        Member find = memberRepository.findOne(memberEmail);

        if(find.getPassword().equals(memberpassword)){
            log.debug("로그인 성공");
            return true;
        }else{
            log.error("로그인 실패: 비밀번호 틀림");
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }
    }


    public Member search(Long id){
        Member findMember = memberRepository.findOne(id);
        if(findMember == null){


            throw new IllegalArgumentException("존재하지 않는 아이디 입니다.")
        }
        return findMember;
    }


    public Member searchByEmail(String email){
        Member findMember = memberRepository.findOne(email);
        if(findMember == null){


            throw new IllegalArgumentException("존재하지 않는 이메일 입니다.");
        }


        return findMember;
    }

    public List<Member> searchByName(String name){
        List<Member> find = memberRepository.findByName(name);
        if(find.isEmpty()){

            throw new IllegalArgumentException("이름이 존재하지 않습니다.");
        }
        return find;
    }

    public List<Member> searchAll(){
        return memberRepository.findAll();
    }


    /**
     * 이메일 중복 검사
     *
     * @param email 이메일
     * @return 중복 여부
     */
    public boolean duplicateEmail(String email) {
        List<String> emails = memberRepository.findAllEmails();
        return !emails.contains(email);
    }

    /**
     * 닉네임 중복 검사
     *
     * @param name 닉네임
     * @return 중복 여부
     */
    public boolean duplicateName(String name) {
        List<String> names = memberRepository.findAllNames();
        return !names.contains(name);
    }


}
