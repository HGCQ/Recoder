package yuhan.hgcq.server.dto.member;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UpdateForm {
    private String name;
    private String password;
}
