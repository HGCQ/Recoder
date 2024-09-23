package yuhan.hgcq.client.model.dto.photo;

import java.io.Serializable;
import java.util.List;

public class DeleteCancelPhotoForm implements Serializable {
    private List<Long> photoIds;

    public DeleteCancelPhotoForm() {
    }

    public DeleteCancelPhotoForm(List<Long> photoIds) {
        this.photoIds = photoIds;
    }

    public List<Long> getPhotoIds() {
        return photoIds;
    }

    public void setPhotoIds(List<Long> photoIds) {
        this.photoIds = photoIds;
    }
}
