package yuhan.hgcq.client.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import yuhan.hgcq.client.R;
import yuhan.hgcq.client.controller.AlbumController;
import yuhan.hgcq.client.localDatabase.Repository.AlbumRepository;
import yuhan.hgcq.client.localDatabase.callback.Callback;
import yuhan.hgcq.client.model.dto.album.AlbumDTO;

public class AlbumTrashAdapter extends RecyclerView.Adapter<AlbumTrashAdapter.AlbumTrashViewHolder> {
    private List<AlbumDTO> albumList;
    private List<Integer> selectedItems = new ArrayList<>();
    private OnItemClickListener listener;
    private Context context;
    private boolean isPrivate;

    public AlbumTrashAdapter(List<AlbumDTO> albumList, Context context, boolean isPrivate) {
        this.albumList = albumList;
        this.context = context;
        this.isPrivate = isPrivate;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class AlbumTrashViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageButton albumDelete;

        public AlbumTrashViewHolder(@NonNull View view, OnItemClickListener listener) {
            super(view);

            title = view.findViewById(R.id.title);
            albumDelete = view.findViewById(R.id.albumdelete);

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
    public AlbumTrashViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View albumView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
        return new AlbumTrashViewHolder(albumView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumTrashViewHolder holder, int position) {
        AlbumDTO albumDTO = albumList.get(position);
        holder.title.setText(albumDTO.getName());
        holder.albumDelete.setVisibility(View.INVISIBLE);

        if (selectedItems.contains(position)) {
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (selectedItems.contains(position)) {
                selectedItems.remove(Integer.valueOf(position));
            } else {
                selectedItems.add(position);
            }
            notifyItemChanged(Integer.valueOf(position));
        });
    }

    public List<Long> getSelectedItems() {
        List<Long> newList = new ArrayList<>(selectedItems.size());

        for (int position : selectedItems) {
            newList.add(albumList.get(position).getAlbumId());
        }

        return newList;
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public void updateList(List<AlbumDTO> newList) {
        albumList.clear();
        albumList.addAll(newList);
        notifyDataSetChanged();
    }
}