package yuhan.hgcq.client.model.dto.photo;

import java.io.Serializable;

public class PhotoDTO implements Serializable {

    private Long photoId;
    private Long albumId;
    private String name;
    private String path;
    private String created;

    public PhotoDTO() {
    }

    public PhotoDTO(Long photoId, Long albumId, String name, String path, String created) {
        this.photoId = photoId;
        this.albumId = albumId;
        this.name = name;
        this.path = path;
        this.created = created;
    }

    public Long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Long photoId) {
        this.photoId = photoId;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "PhotoDTO{" +
                "photoId=" + photoId +
                ", albumId=" + albumId +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", created=" + created +
                '}';
    }
}
