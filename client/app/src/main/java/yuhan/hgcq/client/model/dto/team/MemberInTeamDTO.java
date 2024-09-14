package yuhan.hgcq.client.model.dto.team;

import java.io.Serializable;

public class MemberInTeamDTO implements Serializable {

    private Long memberId;
    private String name;
    private Boolean isAdmin;
    private Boolean isOwner;

    public MemberInTeamDTO() {
    }

    public MemberInTeamDTO(Long memberId, String name, Boolean isAdmin, Boolean isOwner) {
        this.memberId = memberId;
        this.name = name;
        this.isAdmin = isAdmin;
        this.isOwner = isOwner;
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

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Boolean getOwner() {
        return isOwner;
    }

    public void setOwner(Boolean owner) {
        isOwner = owner;
    }

    @Override
    public String toString() {
        return "MemberInTeamDTO{" +
                "memberId=" + memberId +
                ", name='" + name + '\'' +
                ", isAdmin=" + isAdmin +
                ", isOwner=" + isOwner +
                '}';
    }
}
