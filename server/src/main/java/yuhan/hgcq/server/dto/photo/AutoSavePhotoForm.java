package yuhan.hgcq.server.dto.photo;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AutoSavePhotoForm implements Serializable {
    private List<Long> albumIds;
    private List<MultipartFile> files;
    private List<LocalDateTime> creates;
}
