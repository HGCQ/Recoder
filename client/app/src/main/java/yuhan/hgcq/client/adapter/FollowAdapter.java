package yuhan.hgcq.client.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import yuhan.hgcq.client.R;
import yuhan.hgcq.client.controller.FollowController;
import yuhan.hgcq.client.controller.TeamController;
import yuhan.hgcq.client.model.dto.follow.FollowDTO;
import yuhan.hgcq.client.model.dto.member.MemberDTO;
import yuhan.hgcq.client.model.dto.team.MemberInTeamDTO;
import yuhan.hgcq.client.model.dto.team.TeamDTO;
import yuhan.hgcq.client.model.dto.team.TeamInviteForm;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.FollowViewHolder> {

    private List<MemberDTO> followList;
    private TeamDTO teamDTO;
    private  List<MemberInTeamDTO> memberList;

    private TeamController tc;
    private TeamInviteForm inviteForm;


    private List<Integer> selectedItems = new ArrayList<>();
    private OnItemClickListener listener;

    public FollowAdapter(List<MemberDTO> followList, Context context,TeamController tc,TeamDTO teamDTO) {
        this.followList = followList;
        this.tc=new TeamController(context);
        this.teamDTO=teamDTO;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class FollowViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public FollowViewHolder(@NonNull View view, OnItemClickListener listener) {
            super(view);

            name = view.findViewById(R.id.name);

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
    public FollowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View followView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_setting, parent, false);
        return new FollowViewHolder(followView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowViewHolder holder, int position) {
        MemberDTO memberDTO = followList.get(position);
        holder.name.setText(memberDTO.getName());

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
            notifyItemChanged(position);
        });

        /*holder.save.setOnClickListener(v -> {
            List<Long> selectedMemberIds = getSelectedItems();
            if (selectedMemberIds.isEmpty()) {
                Toast.makeText(v.getContext(), "선택된 친구가 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            inviteForm = new TeamInviteForm();
            inviteForm.setTeamId(teamDTO.getTeamId());
            inviteForm.setMembers(getSelectedItems());
            String message = "초대하시겠습니끼?";
            onClick_setting_costume_save(v.getContext(),message,(dialog, which) -> {
                tc.inviteTeam(inviteForm, new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.isSuccessful()){

                            notifyDataSetChanged();
                            Toast.makeText(v.getContext(), "초대하였습니다.", Toast.LENGTH_SHORT).show();

                        }else{
                            Toast.makeText(v.getContext(), "초대하지 못하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(v.getContext() , "오류 발생", Toast.LENGTH_SHORT).show();
                    }
                });
            },(dialog, which) -> {
                Toast.makeText(v.getContext(), "초대를 취소하시겠습니까?", Toast.LENGTH_SHORT).show();
            });
        });*/
    }

    @Override
    public int getItemCount() {
        return followList.size();
    }

    public List<Long> getSelectedItems() {
        List<Long> newList = new ArrayList<>(selectedItems.size());

        for (int position : selectedItems) {
            newList.add(followList.get(position).getMemberId());
        }

        return newList;
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public void updateList(List<MemberDTO> newList) {
        followList.clear();
        ;
        followList.addAll(newList);
        notifyDataSetChanged();
    }
    public void onClick_setting_costume_save(Context context, String message, DialogInterface.OnClickListener positive, DialogInterface.OnClickListener negative) {
        new AlertDialog.Builder(context).setTitle("Recoder").setMessage(message).setIcon(R.drawable.album).setPositiveButton(android.R.string.yes, positive).setNegativeButton(android.R.string.no, negative).show();
    }
}