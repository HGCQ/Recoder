package yuhan.hgcq.client.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import yuhan.hgcq.client.R;
import yuhan.hgcq.client.config.NetworkClient;
import yuhan.hgcq.client.controller.FollowController;
import yuhan.hgcq.client.model.dto.follow.FollowDTO;
import yuhan.hgcq.client.model.dto.member.MemberDTO;

public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.FollowingViewHolder> {

    private List<MemberDTO> followingList;
    private OnItemClickListener listener;
    private Context context;
    private String serverIp;
    private FollowController fc;

    public FollowingAdapter(Context context, List<MemberDTO> followingList) {
        this.context = context;
        this.serverIp = NetworkClient.getInstance(context).getServerIp();
        this.fc = new FollowController(context);
        this.followingList = followingList;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class FollowingViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public Button unFollow;
        public ImageView profile;

        public FollowingViewHolder(@NonNull View view, OnItemClickListener listener) {
            super(view);

            name = view.findViewById(R.id.name);
            unFollow = view.findViewById(R.id.unfollowButton);
            profile = view.findViewById(R.id.profile);

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
    public FollowingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View followView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_following, parent, false);
        return new FollowingViewHolder(followView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowingViewHolder holder, int position) {
        MemberDTO memberDTO = followingList.get(position);
        holder.name.setText(memberDTO.getName());
        if (memberDTO.getImage() != null) {
            Glide.with(context)
                    .load(serverIp + memberDTO.getImage())
                    .into(holder.profile);
        }
        holder.unFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fc.deleteFollow(new FollowDTO(memberDTO.getMemberId()), new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(context, memberDTO.getName() + " 님을 팔로잉 취소합니다.", Toast.LENGTH_SHORT).show();
                            followingList.remove(memberDTO);
                            notifyDataSetChanged();
                            Log.i("Delete Following", "Success");
                        } else {
                            Log.i("Delete Following", "Fail");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("Delete Following Error", t.getMessage());
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return followingList.size();
    }

    public void updateList(List<MemberDTO> newList) {
        followingList.clear();
        followingList.addAll(newList);
        notifyDataSetChanged();
    }
}
