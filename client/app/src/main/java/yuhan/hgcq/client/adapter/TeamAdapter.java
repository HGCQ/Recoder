package yuhan.hgcq.client.adapter;

import static android.widget.AdapterView.*;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.lang.reflect.Member;
import java.util.List;

import yuhan.hgcq.client.R;
import yuhan.hgcq.client.config.NetworkClient;
import yuhan.hgcq.client.model.dto.album.AlbumDTO;
import yuhan.hgcq.client.model.dto.member.MemberDTO;
import yuhan.hgcq.client.model.dto.team.TeamDTO;
import yuhan.hgcq.client.view.GroupMain;
import yuhan.hgcq.client.view.GroupSetting;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.TeamViewHolder> {

    private List<TeamDTO> groupList;
    private OnItemClickListener listener;
    private Context context;
    private MemberDTO loginMember;
    private String serverIp;

    public TeamAdapter(Context context, MemberDTO loginMember, List<TeamDTO> groupList) {
        this.context = context;
        this.loginMember = loginMember;
        this.groupList = groupList;
        this.serverIp = NetworkClient.getInstance(context).getServerIp();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class TeamViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageButton setting;
        public ImageView photo;

        public TeamViewHolder(@NonNull View view, OnItemClickListener listener) {
            super(view);

            name = view.findViewById(R.id.groupText);
            setting = view.findViewById(R.id.groupset);
            photo = view.findViewById(R.id.basicGroupImage);

            view.setOnClickListener(new OnClickListener() {
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
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View groupView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
        return new TeamViewHolder(groupView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position) {
        TeamDTO teamDTO = groupList.get(position);
        String image = teamDTO.getImage();
        holder.name.setText(teamDTO.getName());
        holder.setting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent groupSettingPage = new Intent(context, GroupSetting.class);
                groupSettingPage.putExtra("teamDTO", teamDTO);
                groupSettingPage.putExtra("loginMember", loginMember);
                context.startActivity(groupSettingPage);
            }
        });
        if (image == null || image.isEmpty()) {
            holder.photo.setImageResource(R.drawable.basic);
        } else {
            String path = serverIp + image;
            Glide.with(context)
                    .load(path)
                    .into(holder.photo);
        }
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    /*수정 Lee*/
    public void updateList(List<TeamDTO> newList) {
        groupList.clear();
        groupList.addAll(newList);
        notifyDataSetChanged();
    }
}
