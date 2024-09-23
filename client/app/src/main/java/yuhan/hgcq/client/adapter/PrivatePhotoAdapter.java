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

public class PrivatePhotoAdapter extends RecyclerView.Adapter<PrivatePhotoAdapter.PrivatePhotoViewHolder> {

    private List<PhotoDTO> photoList;
    private OnItemClickListener listener;
    private Context context;

    public PrivatePhotoAdapter(List<PhotoDTO> photoList, Context context) {
        this.photoList = photoList;
        this.context = context;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class PrivatePhotoViewHolder extends RecyclerView.ViewHolder {
        public ImageView photo;

        public PrivatePhotoViewHolder(@NonNull View view, OnItemClickListener listener) {
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
    public PrivatePhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View photoView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new PrivatePhotoViewHolder(photoView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull PrivatePhotoViewHolder holder, int position) {
        String path = photoList.get(position).getPath();
        Glide.with(context)
                .load(Uri.parse(path))
                .into(holder.photo);
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public List<PhotoDTO> getPhotoList() {
        return photoList;
    }
}
