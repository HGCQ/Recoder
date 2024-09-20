package yuhan.hgcq.client.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import yuhan.hgcq.client.R;
import yuhan.hgcq.client.model.dto.album.AlbumDTO;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {
    private List<AlbumDTO> albumList;
    private OnItemClickListener listener;

    public AlbumAdapter(List<AlbumDTO> albumList) {
        this.albumList = albumList;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class AlbumViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView date;

        public AlbumViewHolder(@NonNull View view, OnItemClickListener listener) {
            super(view);

            title = view.findViewById(R.id.title);
            date = view.findViewById(R.id.date);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(v, position);
                        }
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View albumView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
        return new AlbumViewHolder(albumView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        AlbumDTO albumDTO = albumList.get(position);
        String date = albumDTO.getStartDate().toString() + " ~ " + albumDTO.getEndDate().toString();
        holder.title.setText(albumDTO.getName());
        holder.date.setText(date);
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public void updateList(List<AlbumDTO> newList) {
        albumList.clear();;
        albumList.addAll(newList);
        notifyDataSetChanged();
    }
}