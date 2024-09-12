package yuhan.hgcq.server.dto.album;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AlbumCreateForm implements Serializable {
    private Long teamId;
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
