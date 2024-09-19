package yuhan.hgcq.client.model.dto.album;

import java.io.Serializable;

public class AlbumCreateForm implements Serializable {

    private Long teamId;
    private String name;
    private String startTime;
    private String endTime;

    public AlbumCreateForm() {
    }

    public AlbumCreateForm(Long teamId, String name, String startTime, String endTime) {
        this.teamId = teamId;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "AlbumCreateForm{" +
                "teamId=" + teamId +
                ", name='" + name + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
