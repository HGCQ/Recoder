package yuhan.hgcq.client.model.dto.chat;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ChatDTO implements Serializable {

    private Long id;
    private Long eventId;
    private Long writerId;
    private LocalDateTime time;

    public ChatDTO() {
    }

    public ChatDTO(Long id, Long eventId, Long writerId, LocalDateTime time) {
        this.id = id;
        this.eventId = eventId;
        this.writerId = writerId;
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getWriterId() {
        return writerId;
    }

    public void setWriterId(Long writerId) {
        this.writerId = writerId;
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
                "id=" + id +
                ", eventId=" + eventId +
                ", writerId=" + writerId +
                ", time=" + time +
                '}';
    }
}
