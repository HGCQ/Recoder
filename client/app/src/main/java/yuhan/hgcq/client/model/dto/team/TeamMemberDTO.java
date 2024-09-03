package yuhan.hgcq.client.model.dto.team;

import java.io.Serializable;

public class TeamMemberDTO implements Serializable {
    private Long teamId;
    private Long memberId;

    public TeamMemberDTO() {
    }

    public TeamMemberDTO(Long teamId, Long memberId) {
        this.teamId = teamId;
        this.memberId = memberId;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    @Override
    public String toString() {
        return "TeamMemberDTO{" +
                "teamId=" + teamId +
                ", memberId=" + memberId +
                '}';
    }
}
