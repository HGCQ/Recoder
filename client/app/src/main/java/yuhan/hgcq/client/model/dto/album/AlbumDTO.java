package yuhan.hgcq.client.model.dto.album;

import java.io.Serializable;
import java.time.LocalDate;

public class AlbumDTO implements Serializable {

    private Long id;
    private Long teamId;
    private LocalDate date;
    private String name;
    private String region;
    private String content;

    public AlbumDTO() {
    }

    public AlbumDTO(Long id, Long teamId, LocalDate date, String name, String region, String content) {
        this.id = id;
        this.teamId = teamId;
        this.date = date;
        this.name = name;
        this.region = region;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "AlbumDTO{" +
                "id=" + id +
                ", teamId=" + teamId +
                ", date=" + date +
                ", name='" + name + '\'' +
                ", region='" + region + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
