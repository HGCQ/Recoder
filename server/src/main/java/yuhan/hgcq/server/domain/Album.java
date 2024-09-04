package yuhan.hgcq.server.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Album {
    @Id @GeneratedValue
    @Column(name = "album_id")
    private Long id;

    private LocalDate date;
    private String name;
    private String region;
    private String content;
    private Boolean isDeleted;
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL)
    private List<Chat> chats = new ArrayList<>();

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL)
    private List<Photo> photos = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @PrePersist
    @PreUpdate
    private void validate() {
        if (team == null) {
            throw new IllegalStateException("group is null");
        }
    }

    public Album(Team team, LocalDate date, String name, String region, String content) {
        if (team == null) {
            throw new IllegalStateException("Team cannot be null");
        }
        this.team = team;
        this.date = date;
        this.name = name;
        this.region = region;
        this.content = content;
        this.isDeleted = false;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void changeRegion(String region) {
        this.region = region;
    }

    public void changeContent(String content) {
        this.content = content;
    }

    public void deleteAlbum() {
        isDeleted = true;

        deletedAt = LocalDateTime.now();
    }

    public void cancelDeleteAlbum() {
        isDeleted = false;

        deletedAt = null;
    }

    /* 테스트용(추후 삭제) */
    public void test(LocalDateTime date) {
        isDeleted = true;
        deletedAt = date;
    }
}
