package yuhan.hgcq.server.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Photo {
    @Id @GeneratedValue
    @Column(name = "photo_id")
    private Long id;

    private String name;
    private String path;
    private LocalDateTime created;
    private Boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    private Album album;

    @PrePersist
    @PreUpdate
    private void validate() {
        if (album == null) {
            throw new NullPointerException("Event is null");
        }
    }

    public Photo(Album album, String name, String path, LocalDateTime created) {
        if (album == null) {
            throw new NullPointerException("Event cannot be null");
        }
        this.album = album;
        this.name = name;
        this.path = path;
        this.created = created;
        this.isDeleted = false;
    }

    public void toggleIsDeleted() {
        this.isDeleted = !isDeleted;
    }
}
