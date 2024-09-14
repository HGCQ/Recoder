package yuhan.hgcq.client.localDatabase.dto;

import java.time.LocalDateTime;

public class PPhotoDTO {
    private Long photoId;
    private String path;
    private Long albumId;
    private Boolean is_liked;
    private Boolean is_deleted;
    private LocalDateTime created;
    private LocalDateTime deletedTime;

    public Long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Long photoId) {
        this.photoId = photoId;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    public Boolean getIs_liked() {
        return is_liked;
    }

    public void setIs_liked(Boolean is_liked) {
        this.is_liked = is_liked;
    }

    public Boolean getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(Boolean is_deleted) {
        this.is_deleted = is_deleted;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getDeletedTime() {
        return deletedTime;
    }

    public void setDeletedTime(LocalDateTime deletedTime) {
        this.deletedTime = deletedTime;
    }

    @Override
    public String toString() {
        return "PhotoDTO{" +
                "photoId=" + photoId +
                ", path='" + path + '\'' +
                ", albumId=" + albumId +
                ", is_liked=" + is_liked +
                ", is_deleted=" + is_deleted +
                ", created=" + created +
                ", deletedTime=" + deletedTime +
                '}';
    }
}
