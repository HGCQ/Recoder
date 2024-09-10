package yuhan.hgcq.client.localDatabase.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.time.LocalDate;
import java.time.LocalDateTime;

import yuhan.hgcq.client.localDatabase.Converters;

//import yuhan.hgcq.client.localDatabase.Converters;

@Entity(tableName = "Album")
@TypeConverters({Converters.class})
public class Album {
<<<<<<< HEAD
    @PrimaryKey(autoGenerate = true)
    private Long album_id;
    private LocalDate date;
    private String outline;
    private String comment;
=======

    public Album() {

    }


    // 정적 팩토리 메서드
    public static Album create(@NonNull LocalDateTime date, @NonNull String name, @NonNull String content, @NonNull String region) {
        Album album = new Album();
        album.setDate(date);
        album.setName(name);
        album.setContent(content);
        album.setRegion(region);
        return album;
    }
    @PrimaryKey(autoGenerate = true)
    private Long albumId;
    @NonNull
    private LocalDateTime date;
    @NonNull
    private String name;
    @NonNull
    private String content;
    @NonNull
>>>>>>> main
    private String region;

    public Long getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(Long albumId) {
        this.album_id = albumId;
    }

    @NonNull
    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(@NonNull LocalDateTime date) {
        this.date = date;
    }

<<<<<<< HEAD
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
=======
    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getContent() {
        return content;
    }

    public void setContent(@NonNull String content) {
        this.content = content;
>>>>>>> main
    }

    @NonNull
    public String getRegion() {
        return region;
    }

    public void setRegion(@NonNull String region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return "Album{" +
<<<<<<< HEAD
                "album_id=" + album_id +
                ", date=" + date +
                ", outline='" + outline + '\'' +
                ", comment='" + comment + '\'' +
=======
                "albumId=" + albumId +
                ", date=" + date +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
>>>>>>> main
                ", region='" + region + '\'' +
                '}';
    }
}
