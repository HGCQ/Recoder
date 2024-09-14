package yuhan.hgcq.client.localDatabase.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PUploadPhotoForm {
    private Long albumId;
    private List<String> paths;

    private List<LocalDateTime> times;
    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }

    public List<LocalDateTime> getTimes() {
        return times;
    }

    public void setTimes(List<LocalDateTime> times) {
        this.times = times;
    }

    @Override
    public String toString() {
        return "PUploadPhotoForm{" +
                "albumId=" + albumId +
                ", paths=" + paths +
                ", times=" + times +
                '}';
    }
}
