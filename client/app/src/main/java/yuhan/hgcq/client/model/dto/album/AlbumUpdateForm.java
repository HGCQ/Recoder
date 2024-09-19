package yuhan.hgcq.client.model.dto.album;

import java.io.Serializable;

public class AlbumUpdateForm implements Serializable {

    private Long albumId;
    private String startDate;
    private String endDate;
    private String name;

    public AlbumUpdateForm() {
    }

    public AlbumUpdateForm(Long albumId, String startDate, String endDate, String name) {
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
        return "AlbumUpdateForm{" +
                "albumId=" + albumId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", name='" + name + '\'' +
                '}';
    }
}
