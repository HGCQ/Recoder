package yuhan.hgcq.client.model.dto.chat;

import java.io.Serializable;

public class ChatDTO implements Serializable {

    private Long chatId;
    private Long albumId;
    private Long writerId;
    private String writerName;
    private String image;
    private String message;
    private String time;

    public ChatDTO() {
    }

    public ChatDTO(Long chatId, Long albumId, Long writerId, String writerName, String image, String message, String time) {
        this.chatId = chatId;
        this.albumId = albumId;
        this.writerId = writerId;
        this.writerName = writerName;
        this.image = image;
        this.message = message;
        this.time = time;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    public Long getWriterId() {
        return writerId;
    }

    public void setWriterId(Long writerId) {
        this.writerId = writerId;
    }

    public String getWriterName() {
        return writerName;
    }

    public void setWriterName(String writerName) {
        this.writerName = writerName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "ChatDTO{" +
                "chatId=" + chatId +
                ", albumId=" + albumId +
                ", writerId=" + writerId +
                ", writerName='" + writerName + '\'' +
                ", message='" + message + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
