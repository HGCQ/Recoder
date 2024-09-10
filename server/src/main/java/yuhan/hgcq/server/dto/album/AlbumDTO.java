package yuhan.hgcq.server.dto.album;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AlbumDTO implements Serializable {
    private Long id;
    private Long teamId;
    private LocalDate date;
    private String name;
    private String region;
    private String content;
}
