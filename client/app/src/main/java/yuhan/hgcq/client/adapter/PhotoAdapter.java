package yuhan.hgcq.client.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import yuhan.hgcq.client.R;
import yuhan.hgcq.client.config.NetworkClient;
import yuhan.hgcq.client.model.dto.photo.PhotoDTO;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private List<PhotoDTO> photoList;
    private List<Integer> selectedItems = new ArrayList<>();
    private OnItemClickListener listener;
    private Context context;
    private String serverIp;
    private boolean isPrivate;

    public PhotoAdapter(List<PhotoDTO> photoList, Context context, boolean isPrivate) {
        this.photoList = photoList;
        this.context = context;
        this.serverIp = NetworkClient.getInstance(context).getServerIp();
        this.isPrivate = isPrivate;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        public ImageView photo;

        public PhotoViewHolder(@NonNull View view, OnItemClickListener listener) {
            super(view);

            photo = view.findViewById(R.id.photo);

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
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View photoView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_image, parent, false);
        return new PhotoViewHolder(photoView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        if (isPrivate) {
            String path = photoList.get(position).getPath();
            Glide.with(context)
                    .load(Uri.parse(path))
                    .into(holder.photo);
        } else {
            Glide.with(context)
                    .load(serverIp + photoList.get(position).getPath())
                    .into(holder.photo);
        }


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
            newList.add(photoList.get(position).getPhotoId());
        }

        return newList;
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public List<PhotoDTO> getPhotoList() {
        return photoList;
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }
}
