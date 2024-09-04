package yuhan.hgcq.server.dto.team;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TeamUpdateForm implements Serializable {
    private Long id;
    private String name;
}
