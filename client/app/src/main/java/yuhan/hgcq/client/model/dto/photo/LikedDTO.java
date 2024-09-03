package yuhan.hgcq.client.model.dto.photo;

import java.io.Serializable;

public class LikedDTO implements Serializable {

    private Long memberId;
    private Long photoId;

    public LikedDTO() {
    }

    public LikedDTO(Long memberId, Long photoId) {
        this.memberId = memberId;
        this.photoId = photoId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
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
                "memberId=" + memberId +
                ", photoId=" + photoId +
                '}';
    }
}
