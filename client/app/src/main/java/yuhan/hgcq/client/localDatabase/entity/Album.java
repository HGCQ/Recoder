package yuhan.hgcq.client.localDatabase.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

@Entity(tableName = "Album")
public class Album {
    @PrimaryKey(autoGenerate = true)
    private Long album_id;
    private LocalDate date;
    private String outline;
    private String comment;
    private String region;

    public Long getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(Long albumId) {
        this.album_id = albumId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getOutline() {
        return outline;
    }

    public void setOutline(String outline) {
        this.outline = outline;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
                "album_id=" + album_id +
                ", date=" + date +
                ", outline='" + outline + '\'' +
                ", comment='" + comment + '\'' +
                ", region='" + region + '\'' +
                '}';
    }
}
