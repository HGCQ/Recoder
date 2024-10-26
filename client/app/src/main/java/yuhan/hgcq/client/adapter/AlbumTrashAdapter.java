package yuhan.hgcq.client.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import yuhan.hgcq.client.R;
import yuhan.hgcq.client.config.NetworkClient;
import yuhan.hgcq.client.controller.AlbumController;
import yuhan.hgcq.client.controller.PhotoController;
import yuhan.hgcq.client.localDatabase.Repository.AlbumRepository;
import yuhan.hgcq.client.localDatabase.Repository.PhotoRepository;
import yuhan.hgcq.client.localDatabase.callback.Callback;
import yuhan.hgcq.client.model.dto.album.AlbumDTO;
import yuhan.hgcq.client.model.dto.photo.PhotoDTO;

public class AlbumTrashAdapter extends RecyclerView.Adapter<AlbumTrashAdapter.AlbumTrashViewHolder> {
    private List<AlbumDTO> albumList;
    private List<Integer> selectedItems = new ArrayList<>();
    private OnItemClickListener listener;
    private Context context;
    private boolean isPrivate;
    private PhotoRepository pr;
    private PhotoController pc;
    private Handler handler = new Handler(Looper.getMainLooper());
    private String serverIp;

    public AlbumTrashAdapter(List<AlbumDTO> albumList, Context context, boolean isPrivate) {
        this.albumList = albumList;
        this.context = context;
        this.isPrivate = isPrivate;
        this.pc = new PhotoController(context);
        this.pr = new PhotoRepository(context);
        this.serverIp = NetworkClient.getInstance(context).getServerIp();
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
        public ImageView photo;

        public AlbumTrashViewHolder(@NonNull View view, OnItemClickListener listener) {
            super(view);

            title = view.findViewById(R.id.title);
            albumDelete = view.findViewById(R.id.albumdelete);
            photo = view.findViewById(R.id.basicAlbumImage);

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

        if (isPrivate) {
            pr.searchByAlbum(albumDTO.getAlbumId(), new Callback<List<PhotoDTO>>() {
                @Override
                public void onSuccess(List<PhotoDTO> result) {
                    handler.post(() -> { // UI 스레드에서 실행
                        if (result != null && !result.isEmpty()) {
                            int random = (int) (Math.random() * result.size());
                            String path = result.get(random).getPath();
                            Glide.with(context)
                                    .load(Uri.parse(path))
                                    .into(holder.photo);
                        } else {
                            holder.photo.setImageResource(R.drawable.basic2); // 기본 이미지 설정
                        }
                        Log.i("앨범 대표 사진", "Success");
                    });
                }

                @Override
                public void onError(Exception e) {
                    handler.post(() -> { // UI 스레드에서 실행
                        holder.photo.setImageResource(R.drawable.basic2); // 기본 이미지 설정
                        Log.e("앨범 대표 사진", e.getMessage());
                    });
                }
            });
        } else {
            pc.photoList(albumDTO.getAlbumId(), new retrofit2.Callback<List<PhotoDTO>>() {
                @Override
                public void onResponse(Call<List<PhotoDTO>> call, Response<List<PhotoDTO>> response) {
                    handler.post(() -> { // UI 스레드에서 실행
                        if (response.isSuccessful()) {
                            List<PhotoDTO> photoDTO = response.body();
                            if (photoDTO != null && !photoDTO.isEmpty()) {
                                int random = (int) (Math.random() * photoDTO.size());
                                Glide.with(context)
                                        .load(serverIp + photoDTO.get(random).getPath())
                                        .into(holder.photo);
                            } else {
                                holder.photo.setImageResource(R.drawable.basic2); // 기본 이미지 설정
                            }
                            Log.i("앨범 대표 사진", "Success");
                        } else {
                            holder.photo.setImageResource(R.drawable.basic2); // 기본 이미지 설정
                        }
                    });
                }

                @Override
                public void onFailure(Call<List<PhotoDTO>> call, Throwable t) {
                    handler.post(() -> { // UI 스레드에서 실행
                        holder.photo.setImageResource(R.drawable.basic2); // 기본 이미지 설정
                        Log.e("앨범 대표 사진", t.getMessage());
                    });
                }
            });
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