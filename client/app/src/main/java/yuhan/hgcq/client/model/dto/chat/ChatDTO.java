package yuhan.hgcq.client.model.dto.chat;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ChatDTO implements Serializable {

    private Long chatId;
    private Long writerId;
    private String writerName;
    private String message;
    private LocalDateTime time;

    public ChatDTO() {
    }

    public ChatDTO(Long chatId, Long writerId, String writerName, String message, LocalDateTime time) {
        this.chatId = chatId;
        this.writerId = writerId;
        this.writerName = writerName;
        this.message = message;
        this.time = time;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
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

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "ChatDTO{" +
                "chatId=" + chatId +
                ", writerId=" + writerId +
                ", writerName='" + writerName + '\'' +
                ", message='" + message + '\'' +
                ", time=" + time +
                '}';
    }
}
