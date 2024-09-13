package yuhan.hgcq.client.localDatabase.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.time.LocalDate;
import java.time.LocalDateTime;

import yuhan.hgcq.client.localDatabase.Converters;

//import yuhan.hgcq.client.localDatabase.Converters;

@Entity(tableName = "photo",
        foreignKeys = @ForeignKey(
                entity = Album.class,
                parentColumns = "albumId",
                childColumns = "albumId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = {"albumId"})})
@TypeConverters({Converters.class})
public class Photo {
    public Photo() {
    }

    @PrimaryKey(autoGenerate = true)
    private Long photoId;
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

    public Long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Long photoId) {
        this.photoId = photoId;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getPath() {
        return path;
    }

    public void setPath(@NonNull String path) {
        this.path = path;
    }

    @NonNull
    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(@NonNull Long albumId) {
        this.albumId = albumId;
    }

    @NonNull
    public Boolean getIs_liked() {
        return is_liked;
    }

    public void setIs_liked(@NonNull Boolean is_liked) {
        this.is_liked = is_liked;
    }

    @NonNull
    public Boolean getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(@NonNull Boolean is_deleted) {
        this.is_deleted = is_deleted;
    }

    @NonNull
    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(@NonNull LocalDateTime created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "photoId=" + photoId +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", albumId=" + albumId +
                ", is_liked=" + is_liked +
                ", is_deleted=" + is_deleted +
                ", created=" + created +
                '}';
    }
}