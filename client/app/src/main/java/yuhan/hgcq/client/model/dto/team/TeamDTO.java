package yuhan.hgcq.client.model.dto.team;

import java.io.Serializable;

public class TeamDTO implements Serializable {
    private Long id;
    private Long ownerId;
    private String name;

    public TeamDTO() {
    }

    public TeamDTO(Long id, Long ownerId, String name) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "TeamDTO{" +
                "id=" + id +
                ", ownerId=" + ownerId +
                ", name='" + name + '\'' +
                '}';
    }
}
