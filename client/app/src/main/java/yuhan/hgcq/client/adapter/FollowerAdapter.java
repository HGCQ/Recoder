package yuhan.hgcq.client.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import yuhan.hgcq.client.R;
import yuhan.hgcq.client.controller.FollowController;
import yuhan.hgcq.client.model.dto.follow.FollowDTO;
import yuhan.hgcq.client.model.dto.member.MemberDTO;

public class FollowerAdapter extends RecyclerView.Adapter<FollowerAdapter.FollowerViewHolder> {

    private List<MemberDTO> followerList;
    private List<MemberDTO> followingList;
    private List<String> followingNameList = new ArrayList<>();
    private OnItemClickListener listener;
    private Context context;
    private FollowController fc;

    public FollowerAdapter(Context context, List<MemberDTO> followerList, List<MemberDTO> followingList) {
        this.context = context;
        this.fc = new FollowController(context);
        this.followerList = followerList;
        this.followingList = followingList;

        for (MemberDTO dto : followingList) {
            followingNameList.add(dto.getName());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class FollowerViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public Button follow, unFollow;

        public FollowerViewHolder(@NonNull View view, OnItemClickListener listener, int viewType) {
            super(view);

            name = view.findViewById(R.id.name);
            if (viewType == 1) {
                unFollow = view.findViewById(R.id.unfollowButton);
            } else {
                follow = view.findViewById(R.id.followButton);
            }

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

    @Override
    public int getItemViewType(int position) {
        MemberDTO dto = followerList.get(position);
        return followingNameList.contains(dto.getName()) ? 1 : 0;
    }

    @NonNull
    @Override
    public FollowerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View followView;
        if (viewType == 1) {
            followView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_following, parent, false);
        } else {
            followView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_follwer, parent, false);
        }
        return new FollowerViewHolder(followView, listener, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowerViewHolder holder, int position) {
        MemberDTO memberDTO = followerList.get(position);
        int viewType = getItemViewType(position);

        holder.name.setText(memberDTO.getName());
        if (viewType == 1) {
            holder.unFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fc.deleteFollow(new FollowDTO(memberDTO.getMemberId()), new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(context, memberDTO.getName() + "을 팔로잉 취소합니다.", Toast.LENGTH_SHORT).show();
                                followingList.remove(memberDTO);
                                followingNameList.remove(memberDTO.getName());
                                notifyDataSetChanged();
                                Log.i("Delete Following", "Success");
                                for(MemberDTO dto : followingList) {
                                    Log.i("followingList", dto.toString());
                                }
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
        } else {
            holder.follow.setOnClickListener(v -> {
                fc.addFollow(new FollowDTO(memberDTO.getMemberId()), new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(context, memberDTO.getName() + "을 팔로잉 합니다.", Toast.LENGTH_SHORT).show();
                            followingList.add(memberDTO);
                            followingNameList.add(memberDTO.getName());
                            notifyDataSetChanged();
                            Log.i("Add Following", "Success");
                            for(MemberDTO dto : followingList) {
                                Log.i("followingList", dto.toString());
                            }
                        } else {
                            Log.i("Add Following", "Fail");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("Add Following Error", t.getMessage());
                    }
                });
            });
        }
    }

    @Override
    public int getItemCount() {
        return followerList.size();
    }
}
