/*
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
                parentColumns = "album_id",
                childColumns = "album_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = {"albumId"})})
@TypeConverters({Converters.class})
public class Photo {
    public Photo() {
    }

    @PrimaryKey(autoGenerate = true)
    private int photo_id;

    private String name;
    private String path;
    private Long album_id;
    private Boolean is_liked;
    private  Boolean is_deleted;
    private LocalDateTime created;

    // Getters and Setters
    public int getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(int photo_id) {
        this.photo_id = photo_id;
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

    public Long getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(Long album_id) {
        this.album_id = album_id;
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
                "photo_id=" + photo_id +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", album_id=" + album_id +
                ", is_liked=" + is_liked +
                ", is_deleted=" + is_deleted +
                ", created=" + created +
                '}';
    }
}*/
