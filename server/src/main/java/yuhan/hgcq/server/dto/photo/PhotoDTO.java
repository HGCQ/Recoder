package yuhan.hgcq.server.dto.photo;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PhotoDTO implements Serializable {
    private Long id;
    private Long albumId;
    private String name;
    private String path;
    private LocalDateTime created;
}
