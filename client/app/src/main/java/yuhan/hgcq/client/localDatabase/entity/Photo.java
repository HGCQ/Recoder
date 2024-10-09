package yuhan.hgcq.client.localDatabase.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.time.LocalDateTime;

import yuhan.hgcq.client.localDatabase.Converters;

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

    @PrimaryKey(autoGenerate = true)
    private Long photoId;
    @NonNull
    private String path;

    @NonNull
    private String region;

    @NonNull
    private Long albumId;
    @NonNull
    private Boolean is_liked;
    @NonNull
    private Boolean is_deleted;
    @NonNull
    private LocalDateTime created;

    private LocalDateTime deletedTime;

    public Photo() {
    }

    public static Photo create(@NonNull String path, @NonNull Long albumId, @NonNull LocalDateTime created, @NonNull String region) {
        Photo photo = new Photo();
        photo.setPath(path);
        photo.setAlbumId(albumId);
        photo.setIs_liked(false); //
        photo.setIs_deleted(false); // 삭제되지 않은 상태
        photo.setCreated(created); // 생성 시간 설정
        photo.setRegion(region);
        return photo;
    }

    public Long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Long photoId) {
        this.photoId = photoId;
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

    @NonNull
    public String getRegion() {
        return region;
    }

    public void setRegion(@NonNull String region) {
        this.region = region;
    }

    public LocalDateTime getDeletedTime() {
        return deletedTime;
    }

    public void setDeletedTime(LocalDateTime deletedTime) {
        this.deletedTime = deletedTime;
    }

    public void delete() {
        this.is_deleted = true;
        this.deletedTime = LocalDateTime.now();
    }

    public void deleteCancel() {
        this.is_deleted = false;
        this.deletedTime = null;
    }

    public void liked() {
        this.is_liked = !is_liked;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "photoId=" + photoId +
                ", path='" + path + '\'' +
                ", region='" + region + '\'' +
                ", albumId=" + albumId +
                ", is_liked=" + is_liked +
                ", is_deleted=" + is_deleted +
                ", created=" + created +
                ", deletedTime=" + deletedTime +
                '}';
    }
}
