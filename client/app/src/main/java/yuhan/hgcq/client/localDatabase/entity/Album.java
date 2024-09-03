package yuhan.hgcq.client.localDatabase.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

@Entity(tableName = "Album")
public class Album {

    @PrimaryKey(autoGenerate = true)
    private Long albumId;
    @NonNull
    private LocalDate date;
    @NonNull
    private String name;
    @NonNull
    private String content;
    @NonNull
    private String region;
    @NonNull
    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return "Album{" +
                "album_id=" + albumId +
                ", date=" + date +
                ", outline='" + name + '\'' +
                ", comment='" + content + '\'' +
                ", region='" + region + '\'' +
                '}';
    }
}
