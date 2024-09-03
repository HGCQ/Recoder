package yuhan.hgcq.client.localDatabase.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity(tableName = "photo",
        foreignKeys = @ForeignKey(
                entity = Album.class,
                parentColumns = "albumId",
                childColumns = "albumId",
                onDelete = ForeignKey.CASCADE
        ))
public class Photo {

    @PrimaryKey(autoGenerate = true)
    private int photoId;
    @NonNull
    private String name;
    @NonNull
    private String path;
    @NonNull
    private Long albumId;
    @NonNull
    private Boolean is_liked;
    @NonNull
    private Boolean is_deleted;
    @NonNull
    private LocalDateTime created;

    // Getters and Setters
    public int getPhoto_id() {
        return photoId;
    }

    public void setPhoto_id(int photoId) {
        this.photoId = photoId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    public Boolean getIs_liked() {
        return is_liked;
    }

    public void setIs_liked(Boolean is_liked) {
        this.is_liked = is_liked;
    }

    public Boolean getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(Boolean is_deleted) {
        this.is_deleted = is_deleted;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "photo_id=" + photoId +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", album_id=" + albumId +
                ", is_liked=" + is_liked +
                ", is_deleted=" + is_deleted +
                ", created=" + created +
                '}';
    }
}