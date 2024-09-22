package yuhan.hgcq.client.model.dto.photo;

import java.io.Serializable;

public class LikedDTO implements Serializable {
    private Long photoId;

    public LikedDTO() {
    }

    public LikedDTO(Long photoId) {
        this.photoId = photoId;
    }

    public Long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Long photoId) {
        this.photoId = photoId;
    }

    @Override
    public String toString() {
        return "LikedDTO{" +
                "photoId=" + photoId +
                '}';
    }
}
