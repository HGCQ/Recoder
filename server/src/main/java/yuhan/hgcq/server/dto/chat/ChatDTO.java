package yuhan.hgcq.server.dto.chat;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChatDTO implements Serializable {
    private Long id;
    private Long eventId;
    private Long writerId;
    private LocalDateTime time;
}
