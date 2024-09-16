package yuhan.hgcq.client.localDatabase.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.time.LocalDateTime;

import yuhan.hgcq.client.localDatabase.Converters;

@Entity(tableName = "Album")
@TypeConverters({Converters.class})
public class Album {
    @PrimaryKey(autoGenerate = true)
    private Long albumId;
    @NonNull
    private LocalDateTime startDate;

    @NonNull
    private LocalDateTime endDate;
    @NonNull
    private String name;

    @NonNull
    private Boolean isDeleted;

    private LocalDateTime deletedTime;

    public Album() {

    }

    // 정적 팩토리 메서드
    public static Album create(@NonNull LocalDateTime startDate, @NonNull LocalDateTime endDate,  @NonNull String name) {
        Album album = new Album();
        album.setStartDate(startDate);
        album.setEndDate(endDate);
        album.setName(name);
        album.isDeleted = false;
        return album;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    @NonNull
    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(@NonNull LocalDateTime startDate) {
        this.startDate = startDate;
    }

    @NonNull
    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(@NonNull LocalDateTime endDate) {
        this.endDate = endDate;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(@NonNull Boolean deleted) {
        isDeleted = deleted;
    }

    public LocalDateTime getDeletedTime() {
        return deletedTime;
    }

    public void setDeletedTime(LocalDateTime deletedTime) {
        this.deletedTime = deletedTime;
    }

    public void delete() {
        this.isDeleted = true;
        this.deletedTime = LocalDateTime.now();
    }

    public void deleteCancel() {
        this.isDeleted = false;
        this.deletedTime = null;
    }

    @Override
    public String toString() {
        return "Album{" +
                "albumId=" + albumId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", name='" + name + '\'' +
                ", isDeleted=" + isDeleted +
                ", deleted=" + deletedTime +
                '}';
    }
}
