package yuhan.hgcq.client.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import yuhan.hgcq.client.R;
import yuhan.hgcq.client.config.NetworkClient;
import yuhan.hgcq.client.model.dto.photo.PhotoDTO;

public class GalleryPhotoAdapter extends RecyclerView.Adapter<GalleryPhotoAdapter.GalleryPhotoViewHolder> {

    private List<PhotoDTO> photoList;
    private OnItemClickListener listener;
    private Context context;
    private String serverIp;
    private boolean isPrivate;

    public GalleryPhotoAdapter(List<PhotoDTO> photoList, Context context, boolean isPrivate) {
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

    public static class GalleryPhotoViewHolder extends RecyclerView.ViewHolder {
        public ImageView photo;

        public GalleryPhotoViewHolder(@NonNull View view, OnItemClickListener listener) {
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
    public GalleryPhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View photoView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_image, parent, false);
        return new GalleryPhotoViewHolder(photoView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryPhotoViewHolder holder, int position) {
        if(isPrivate){
            String path = photoList.get(position).getPath();
            Glide.with(context)
                    .load(Uri.parse(path))
                    .into(holder.photo);
        }else {
            Glide.with(context)
                    .load(serverIp + photoList.get(position).getPath())
                    .into(holder.photo);
        }
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }
}
