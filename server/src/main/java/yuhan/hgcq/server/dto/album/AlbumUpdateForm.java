package yuhan.hgcq.server.dto.album;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AlbumUpdateForm implements Serializable {
    private Long albumId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String name;
}
