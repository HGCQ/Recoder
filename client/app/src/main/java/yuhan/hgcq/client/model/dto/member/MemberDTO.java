package yuhan.hgcq.client.model.dto.member;

import java.io.Serializable;

public class MemberDTO implements Serializable {

    private Long memberId;
    private String name;
    private String email;

    public MemberDTO() {
    }

    public MemberDTO(Long memberId, String name, String email) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "MemberDTO{" +
                "memberId=" + memberId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
