package yuhan.hgcq.client.localDatabase.dto;

import java.time.LocalDateTime;

public class PAlbumDTO {
    private Long albumId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String name;
    private Boolean isDeleted;
    private LocalDateTime deletedTime;

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
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

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public LocalDateTime getDeletedTime() {
        return deletedTime;
    }

    public void setDeletedTime(LocalDateTime deletedTime) {
        this.deletedTime = deletedTime;
    }

    @Override
    public String toString() {
        return "PAlbumDTO{" +
                "albumId=" + albumId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", name='" + name +
                ", isDeleted=" + isDeleted +
                ", deletedTime=" + deletedTime +
                '}';
    }
}