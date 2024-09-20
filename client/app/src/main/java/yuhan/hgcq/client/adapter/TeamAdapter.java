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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Member;
import java.util.List;

import yuhan.hgcq.client.R;
import yuhan.hgcq.client.model.dto.member.MemberDTO;
import yuhan.hgcq.client.model.dto.team.TeamDTO;
import yuhan.hgcq.client.view.GroupSetting;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.TeamViewHolder> {

    private List<TeamDTO> groupList;
    private OnItemClickListener listener;
    private Context context;
    private MemberDTO loginMember;

    public TeamAdapter(Context context, MemberDTO loginMember, List<TeamDTO> groupList) {
        this.context = context;
        this.loginMember = loginMember;
        this.groupList = groupList;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class TeamViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView owner;
        public ImageButton setting;

        public TeamViewHolder(@NonNull View view, OnItemClickListener listener) {
            super(view);

            name = view.findViewById(R.id.title);
            owner = view.findViewById(R.id.date);
            setting = view.findViewById(R.id.groupset);


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
        holder.name.setText(teamDTO.getName());
        holder.owner.setText(teamDTO.getOwner());
        holder.setting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent groupSettingPage = new Intent(context, GroupSetting.class);
                groupSettingPage.putExtra("teamDTO", teamDTO);
                groupSettingPage.putExtra("loginMember", loginMember);
                Log.d("loginMember", loginMember.toString());
                context.startActivity(groupSettingPage);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }
}