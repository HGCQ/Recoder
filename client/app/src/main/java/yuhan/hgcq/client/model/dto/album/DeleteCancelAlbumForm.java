package yuhan.hgcq.client.model.dto.album;

import java.io.Serializable;
import java.util.List;

public class DeleteCancelAlbumForm implements Serializable {
    private List<Long> albumIds;

    public DeleteCancelAlbumForm() {
    }

    public DeleteCancelAlbumForm(List<Long> albumIds) {
        this.albumIds = albumIds;
    }

    public List<Long> getAlbumIds() {
        return albumIds;
    }

    public void setAlbumIds(List<Long> albumIds) {
        this.albumIds = albumIds;
    }
}
