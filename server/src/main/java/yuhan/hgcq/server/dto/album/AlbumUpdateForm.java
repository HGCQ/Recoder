package yuhan.hgcq.server.dto.album;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AlbumUpdateForm implements Serializable {
    private Long albumId;
    private LocalDate date;
    private String name;
    private String region;
    private String content;
}
