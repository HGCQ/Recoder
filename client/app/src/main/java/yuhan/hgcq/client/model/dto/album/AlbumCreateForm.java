package yuhan.hgcq.client.model.dto.album;

import java.io.Serializable;
import java.time.LocalDateTime;

public class AlbumCreateForm implements Serializable {

    private Long teamId;
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public AlbumCreateForm() {
    }

    public AlbumCreateForm(Long teamId, String name, LocalDateTime startTime, LocalDateTime endTime) {
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
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
