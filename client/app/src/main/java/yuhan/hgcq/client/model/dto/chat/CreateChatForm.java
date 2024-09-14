package yuhan.hgcq.client.model.dto.chat;

import java.io.Serializable;

public class CreateChatForm implements Serializable {

    private Long albumId;
    private String message;

    public CreateChatForm() {
    }

    public CreateChatForm(Long albumId, String message) {
        this.albumId = albumId;
        this.message = message;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "CreateChatForm{" +
                "albumId=" + albumId +
                ", message='" + message + '\'' +
                '}';
    }
}
