package yuhan.hgcq.client.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import yuhan.hgcq.client.R;
import yuhan.hgcq.client.model.dto.team.MemberInTeamDTO;

public class MemberInTeamAdapter extends RecyclerView.Adapter<MemberInTeamAdapter.MemberInTeamViewHolder> {

    private List<MemberInTeamDTO> memberList;
    private OnItemClickListener listener;

    public MemberInTeamAdapter(List<MemberInTeamDTO> memberList) {
        this.memberList = memberList;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class  MemberInTeamViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView rank;

        public MemberInTeamViewHolder(@NonNull View view, OnItemClickListener listener) {
            super(view);

            name = view.findViewById(R.id.name);
            rank = view.findViewById(R.id.email);

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
    public MemberInTeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View memberView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_add, parent, false);
        return new MemberInTeamViewHolder(memberView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberInTeamViewHolder holder, int position) {
        MemberInTeamDTO dto = memberList.get(position);
        holder.name.setText(dto.getName());
        if (dto.getOwner()) {
            holder.rank.setText("그룹장");
        } else if (dto.getAdmin()) {
            holder.rank.setText("관리자");
        } else {
            holder.rank.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }
}