package yuhan.hgcq.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import yuhan.hgcq.client.R;
import yuhan.hgcq.client.localDatabase.dto.PAlbumDTO;
import yuhan.hgcq.client.model.dto.album.AlbumDTO;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    private Context context;
    private List<PAlbumDTO> pAlbumList;

    private List<AlbumDTO>  albumList;
    private boolean isPrivate;

    /*개별항목(xml로 만들 레이아웃)에 연결되는 데이터(개별항목에 들어가는 리소스) 리스트 구현*/
    public AlbumAdapter(Context context, List<PAlbumDTO> albumList, boolean isPrivate) {
        this.context = context;
        this.pAlbumList = albumList;
        this.isPrivate = isPrivate;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_album, parent, false);
        return new AlbumViewHolder(view);
    }
    public AlbumAdapter(Context context, boolean isPrivate,List<AlbumDTO> albumList) {
        this.context = context;
        this.albumList = albumList;
        this.isPrivate = false;  // server albums are public
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        if (isPrivate && pAlbumList != null) {
            PAlbumDTO paAlbum = (PAlbumDTO) pAlbumList.get(position);
            holder.albumTitle.setText(paAlbum.getName());
            holder.albumDate.setText(paAlbum.getStartDate() + "~" + paAlbum.getEndDate());
        } else if (albumList != null) {
            AlbumDTO album = (AlbumDTO) albumList.get(position);
            holder.albumDate.setText(album.getName());
            holder.albumTitle.setText(album.getStartDate() + "~" + album.getEndDate());
        }
    }

    @Override
    public int getItemCount() {
        if (isPrivate && pAlbumList != null) {
            return pAlbumList.size();
        } else if (albumList != null) {
            return albumList.size();
        } else {
            return 0;
        }
    }

    /*로컬 앨범*/
    public void updatePAlbumList(List<PAlbumDTO> updatedPAlbumList) {
        this.pAlbumList = updatedPAlbumList;
        notifyDataSetChanged();
    }
    /*서버 앨범*/
    public void updateAlbumList(List<AlbumDTO> albumList) {
        this.albumList = albumList;
        notifyDataSetChanged();
    }

    /*아이템 뷰를 저장하는 클래스 : xml로 지정된 뷰의 id 참조*/
    public static class AlbumViewHolder extends RecyclerView.ViewHolder {
        TextView albumTitle, albumDate;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            albumDate = itemView.findViewById(R.id.date);
            albumTitle = itemView.findViewById(R.id.title);

        }
    }
}
