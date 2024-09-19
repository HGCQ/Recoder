package yuhan.hgcq.client.model.dto.album;

import java.io.Serializable;

public class AlbumDTO implements Serializable {

    private Long albumId;
    private Long teamId;
    private String startDate;
    private String endDate;
    private String name;

    public AlbumDTO() {
    }

    public AlbumDTO(Long albumId, Long teamId, String startDate, String endDate, String name) {
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
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
