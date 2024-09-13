package yuhan.hgcq.client.model.dto.photo;

import java.io.Serializable;
import java.util.List;

public class MovePhotoForm implements Serializable {

    private Long newAlbumId;
    private List<PhotoDTO> photos;

    public MovePhotoForm() {
    }

    public MovePhotoForm(Long newAlbumId, List<PhotoDTO> photos) {
        this.newAlbumId = newAlbumId;
        this.photos = photos;
    }

    public Long getNewAlbumId() {
        return newAlbumId;
    }

    public void setNewAlbumId(Long newAlbumId) {
        this.newAlbumId = newAlbumId;
    }

    public List<PhotoDTO> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PhotoDTO> photos) {
        this.photos = photos;
    }

    @Override
    public String toString() {
        return "MovePhotoForm{" +
                "newAlbumId=" + newAlbumId +
                ", photos=" + photos +
                '}';
    }
}
