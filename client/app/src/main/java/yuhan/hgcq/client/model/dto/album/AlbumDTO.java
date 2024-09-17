package yuhan.hgcq.client.model.dto.album;

import java.io.Serializable;
import java.time.LocalDateTime;

public class PAlbumDTO implements Serializable {

    private Long albumId;
    private Long teamId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String name;

    public PAlbumDTO() {
    }

    public PAlbumDTO(Long albumId, Long teamId, LocalDateTime startDate, LocalDateTime endDate, String name) {
        this.albumId = albumId;
        this.teamId = teamId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.name = name;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "AlbumDTO{" +
                "albumId=" + albumId +
                ", teamId=" + teamId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", name='" + name + '\'' +
                '}';
    }
}