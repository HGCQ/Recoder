package yuhan.hgcq.client.localDatabase.dto;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

public class AutoSavePhotoForm {

    private List<Long> albumIds;

    private List<String> paths;
    private List<LocalDateTime> creates;

    public List<Long> getAlbumIds() {
        return albumIds;
    }

    public void setAlbumIds(List<Long> albumIds) {
        this.albumIds = albumIds;
    }

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }

    public List<LocalDateTime> getCreates() {
        return creates;
    }

    public void setCreates(List<LocalDateTime> creates) {
        this.creates = creates;
    }
}