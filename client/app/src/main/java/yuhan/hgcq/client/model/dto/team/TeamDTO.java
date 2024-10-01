package yuhan.hgcq.client.model.dto.team;

import java.io.Serializable;

public class TeamDTO implements Serializable {

    private Long teamId;
    private String owner;
    private String name;
    private String image;

    public TeamDTO() {
    }

    public TeamDTO(Long teamId, String owner, String name) {
        this.teamId = teamId;
        this.owner = owner;
        this.name = name;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "TeamDTO{" +
                "teamId=" + teamId +
                ", owner='" + owner + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
