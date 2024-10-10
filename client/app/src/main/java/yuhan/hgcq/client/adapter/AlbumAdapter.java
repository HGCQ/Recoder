package yuhan.hgcq.client.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
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
import yuhan.hgcq.client.controller.MemberController;
import yuhan.hgcq.client.controller.PhotoController;
import yuhan.hgcq.client.localDatabase.Repository.AlbumRepository;
import yuhan.hgcq.client.localDatabase.Repository.PhotoRepository;
import yuhan.hgcq.client.localDatabase.callback.Callback;
import yuhan.hgcq.client.model.dto.album.AlbumDTO;
import yuhan.hgcq.client.model.dto.member.MemberDTO;
import yuhan.hgcq.client.model.dto.photo.PhotoDTO;
import yuhan.hgcq.client.view.AlbumMain;
import yuhan.hgcq.client.view.AlbumTrash;
import yuhan.hgcq.client.view.Photo;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {
    private List<AlbumDTO> albumList;
    private List<AlbumDTO> trashAlbumList = new ArrayList<>();  // 삭제된 앨범을 저장할 리스트
    private OnItemClickListener listener;
    private Context context;
    private AlbumController ac;
    private AlbumRepository ar;
    private boolean isPrivate;
    private Handler handler = new Handler(Looper.getMainLooper());
    private PhotoController pc;
    private PhotoRepository pr;
    private String serverIp;
    private boolean isPhoto = false;


    public AlbumAdapter(List<AlbumDTO> albumList, Context context, boolean isPrivate) {
        this.albumList = albumList;
        this.context = context;
        this.ac = new AlbumController(context);
        this.ar = new AlbumRepository(context);
        this.pc = new PhotoController(context);
        this.pr = new PhotoRepository(context);
        this.isPrivate = isPrivate;
        this.serverIp = NetworkClient.getInstance(context).getServerIp();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setPhoto() {
        this.isPhoto = true;
    }

    public static class AlbumViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageButton albumDelete;

        public ImageView photo;

        public AlbumViewHolder(@NonNull View view, OnItemClickListener listener) {
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
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View albumView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
        return new AlbumViewHolder(albumView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        AlbumDTO albumDTO = albumList.get(position);
        holder.title.setText(albumDTO.getName());

        if (isPrivate) {
            pr.searchByAlbum(albumDTO.getAlbumId(), new Callback<List<PhotoDTO>>() {
                @Override
                public void onSuccess(List<PhotoDTO> result) {
                    handler.post(() -> { // UI 스레드에서 실행
                        if (result != null && !result.isEmpty()) {
                            int random = (int) (Math.random() * result.size());
                            String path = result.get(random).getPath();
                            Uri uri = Uri.parse(path);
                            Bitmap thumbNail = getThumbNail(uri);
                            Glide.with(context)
                                    .load(thumbNail)
                                    .into(holder.photo);
                        } else {
                            holder.photo.setImageResource(R.drawable.basic); // 기본 이미지 설정
                        }
                        Log.i("앨범 대표 사진", "Success");
                    });
                }

                @Override
                public void onError(Exception e) {
                    handler.post(() -> { // UI 스레드에서 실행
                        holder.photo.setImageResource(R.drawable.basic); // 기본 이미지 설정
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
                                String path = serverIp + photoDTO.get(random).getPath();
                                Glide.with(context)
                                        .load(path)
                                        .into(holder.photo);
                            } else {
                                holder.photo.setImageResource(R.drawable.basic); // 기본 이미지 설정
                            }
                            Log.i("앨범 대표 사진", "Success");
                        } else {
                            holder.photo.setImageResource(R.drawable.basic); // 기본 이미지 설정
                        }
                    });
                }

                @Override
                public void onFailure(Call<List<PhotoDTO>> call, Throwable t) {
                    handler.post(() -> { // UI 스레드에서 실행
                        holder.photo.setImageResource(R.drawable.basic); // 기본 이미지 설정
                        Log.e("앨범 대표 사진", t.getMessage());
                    });
                }
            });
        }

        if (isPhoto) {
            holder.albumDelete.setVisibility(View.INVISIBLE);
        } else {
            holder.albumDelete.setOnClickListener(v -> {
                int currentPositon = holder.getAdapterPosition();
                if (isPrivate) {
                    ar.delete(albumDTO.getAlbumId(), new Callback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean result) {
                            if (result != null) {
                                if (result) {
                                    albumList.remove(currentPositon);
                                    handler.post(() -> {
                                        notifyItemRemoved(currentPositon);
                                        notifyItemRangeChanged(currentPositon, albumList.size());
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
                                albumList.remove(currentPositon);
                                handler.post(() -> {
                                    notifyItemRemoved(currentPositon);
                                    notifyItemRangeChanged(currentPositon, albumList.size());
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

    private Bitmap getThumbNail(Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.TITLE/*, MediaStore.Images.Media.ORIENTATION*/};

        ContentResolver cor = context.getContentResolver();
        Cursor cursor = cor.query(uri, filePathColumn, null, null, null);

        Bitmap thumbnail = null;
        if(cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            long ImageId = cursor.getLong(columnIndex);
            if(ImageId != 0) {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                thumbnail = MediaStore.Images.Thumbnails.getThumbnail(
                        context.getContentResolver(), ImageId,
                        MediaStore.Images.Thumbnails.MINI_KIND,
                        bmOptions);
            } else {
                Toast.makeText(context, "불러올수 없는 이미지 입니다.", Toast.LENGTH_LONG).show();
            }
            cursor.close();
        }
        return thumbnail;
    }
}