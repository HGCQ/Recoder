package yuhan.hgcq.client.localDatabase.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class AlbumWithPhotos {
    @Embedded
    public Album album;

    @Relation(
            parentColumn = "albumId",
            entityColumn = "albumId"
    )
    public List<Photo> photos;
}
