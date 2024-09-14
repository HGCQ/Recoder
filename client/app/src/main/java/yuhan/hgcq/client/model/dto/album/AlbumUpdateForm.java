package yuhan.hgcq.client.model.dto.album;

import java.io.Serializable;
import java.time.LocalDateTime;

public class AlbumUpdateForm implements Serializable {

    private Long albumId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String name;

    public AlbumUpdateForm() {
    }

    public AlbumUpdateForm(Long albumId, LocalDateTime startDate, LocalDateTime endDate, String name) {
        this.albumId = albumId;
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
        return "AlbumUpdateForm{" +
                "albumId=" + albumId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", name='" + name + '\'' +
                '}';
    }
}
