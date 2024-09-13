package yuhan.hgcq.client.localDatabase.dto;

import java.util.List;

public class PMovePhotoForm {
    private Long albumId;
    private List<PPhotoDTO> photos;

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    public List<PPhotoDTO> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PPhotoDTO> photos) {
        this.photos = photos;
    }

    @Override
    public String toString() {
        return "PMovePhotoForm{" +
                "albumId=" + albumId +
                ", photos=" + photos +
                '}';
    }
}
