package yuhan.hgcq.client.adapter;

import android.content.Context;
import android.content.Intent;
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
import yuhan.hgcq.client.controller.MemberController;
import yuhan.hgcq.client.localDatabase.Repository.AlbumRepository;
import yuhan.hgcq.client.localDatabase.callback.Callback;
import yuhan.hgcq.client.model.dto.album.AlbumDTO;
import yuhan.hgcq.client.model.dto.member.MemberDTO;
import yuhan.hgcq.client.view.AlbumMain;
import yuhan.hgcq.client.view.AlbumTrash;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {
    private List<AlbumDTO> albumList;
    private List<AlbumDTO> trashAlbumList = new ArrayList<>();  // 삭제된 앨범을 저장할 리스트
    private OnItemClickListener listener;
    private Context context;
    private AlbumController ac;
    private AlbumRepository ar;
    private boolean isPrivate;
    private Handler handler = new Handler(Looper.getMainLooper());



    public AlbumAdapter(List<AlbumDTO> albumList, Context context, boolean isPrivate) {
        this.albumList = albumList;
        this.context = context;
        this.ac = new AlbumController(context);
        this.ar = new AlbumRepository(context);
        this.isPrivate = isPrivate;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class AlbumViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageButton albumDelete;

        public AlbumViewHolder(@NonNull View view, OnItemClickListener listener) {
            super(view);

            title = view.findViewById(R.id.title);
            albumDelete = view.findViewById(R.id.albumDelete);
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
        holder.title.setText(albumDTO.getName());

        holder.albumDelete.setOnClickListener(v -> {
            if (isPrivate) {
                ar.delete(albumDTO.getAlbumId(), new Callback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        if (result != null) {
                            if (result) {
                                albumList.remove(position);
                                handler.post(() -> {
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, albumList.size());
                                    Toast.makeText(context, "앨범을 삭제했습니다.", Toast.LENGTH_SHORT).show();
                                });
                                Log.i("Delete Private Album", "Success");
                            } else {
                                Log.i("Delete Private Album", "Fail");
                            }
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("Delete Private Album Error", e.getMessage());
                    }
                });
            } else {
                ac.deleteAlbum(albumDTO, new retrofit2.Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            trashAlbumList.add(albumDTO);
                            albumList.remove(position);
                            handler.post(() -> {
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, albumList.size());
                                Toast.makeText(context, "앨범을 삭제했습니다.", Toast.LENGTH_SHORT).show();
                            });
                            Log.i("Delete Shared Album", "Success");
                        } else {
                            Log.i("Delete Shared Album", "Fail");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("Delete Shared Album Error", t.getMessage());
                    }
                });
            }
        });
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