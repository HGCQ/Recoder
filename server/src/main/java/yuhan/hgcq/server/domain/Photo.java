package yuhan.hgcq.server.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Photo {
    @Id @GeneratedValue
    @Column(name = "photo_id")
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String path;

    private LocalDateTime created;
    private Boolean isDeleted;
    private LocalDateTime deleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    private Album album;

    @PrePersist
    @PreUpdate
    private void validate() {
        if (album == null || path == null) {
            throw new NullPointerException("Album or Path is null");
        }
    }

    public Photo(Album album, String name, String path, LocalDateTime created) {
        if (album == null || path == null) {
            throw new NullPointerException("Album or Path cannot be null");
        }
        this.album = album;
        this.name = name;
        this.path = path;
        this.created = created;
        this.isDeleted = false;
    }

    public void changeIsDeleted() {
        this.isDeleted = !isDeleted;

        if (isDeleted) {
            deleted = LocalDateTime.now();
        } else {
            deleted = null;
        }
    }
}
