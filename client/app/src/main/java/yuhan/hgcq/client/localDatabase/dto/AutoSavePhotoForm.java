package yuhan.hgcq.client.localDatabase.dto;

import java.time.LocalDateTime;
import java.util.List;

public class AutoSavePhotoForm {

    private List<String> paths;
    private List<LocalDateTime> creates;

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