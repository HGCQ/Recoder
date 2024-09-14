package yuhan.hgcq.client.model.dto.team;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TeamInviteForm implements Serializable {

    private Long teamId;
    private List<Long> members = new ArrayList<>();

    public TeamInviteForm() {
    }

    public TeamInviteForm(Long teamId, List<Long> members) {
        this.teamId = teamId;
        this.members = members;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public List<Long> getMembers() {
        return members;
    }

    public void setMembers(List<Long> members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return "TeamInviteForm{" +
                "teamId=" + teamId +
                ", members=" + members +
                '}';
    }
}
