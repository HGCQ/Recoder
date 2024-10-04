package yuhan.hgcq.client.model.dto.album;

import java.io.Serializable;

public class AlbumCreateForm implements Serializable {

    private Long teamId;
    private String name;

    public AlbumCreateForm() {
    }

    public AlbumCreateForm(Long teamId, String name) {
        this.teamId = teamId;
        this.name = name;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "AlbumCreateForm{" +
                "teamId=" + teamId +
                ", name='" + name + '\'' +
                '}';
    }
}
