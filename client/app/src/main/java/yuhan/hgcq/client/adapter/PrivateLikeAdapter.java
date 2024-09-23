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

public class PrivateLikeAdapter extends RecyclerView.Adapter<PrivateLikeAdapter.PrivateLikeViewHolder> {

    private List<PhotoDTO> likeList;
    private OnItemClickListener listener;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public PrivateLikeAdapter(List<PhotoDTO> likeList, Context context) {
        this.likeList = likeList;
        this.context = context;
    }

    public static class PrivateLikeViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;

        public PrivateLikeViewHolder(@NonNull View view, OnItemClickListener listener) {
            super(view);

            image = view.findViewById(R.id.photo);

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
    public PrivateLikeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View imageView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_image, parent, false);
        return new PrivateLikeViewHolder(imageView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull PrivateLikeViewHolder holder, int position) {
        String path = likeList.get(position).getPath();
        Glide.with(context)
                .load(Uri.parse(path))
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return likeList.size();
    }
}