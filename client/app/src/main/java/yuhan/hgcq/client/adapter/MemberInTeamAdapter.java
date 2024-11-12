package yuhan.hgcq.client.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import yuhan.hgcq.client.R;
import yuhan.hgcq.client.config.NetworkClient;
import yuhan.hgcq.client.controller.TeamController;
import yuhan.hgcq.client.model.dto.member.MemberDTO;
import yuhan.hgcq.client.model.dto.team.MemberInTeamDTO;
import yuhan.hgcq.client.model.dto.team.TeamDTO;
import yuhan.hgcq.client.model.dto.team.TeamMemberDTO;

public class MemberInTeamAdapter extends RecyclerView.Adapter<MemberInTeamAdapter.MemberInTeamViewHolder> {

    private List<MemberInTeamDTO> memberList;
    private TeamController tc;
    private TeamMemberDTO tmDTO;
    private TeamDTO teamDTO;
    private MemberDTO loginmember;
    private Context context;
    private String serverIp;

    public MemberInTeamAdapter(List<MemberInTeamDTO> memberList, Context context, TeamDTO teamDTO, MemberDTO loginmember) {
        this.memberList = memberList;
        this.tc = new TeamController(context);
        this.teamDTO = teamDTO;  // 전달받은 teamDTO를 설정
        this.loginmember = loginmember;
        this.serverIp = NetworkClient.getInstance(context).getServerIp();
        this.context = context;
    }

    public static class MemberInTeamViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView level;
        public ImageButton friendDelete, power;
        public ImageView profile;



        public MemberInTeamViewHolder(@NonNull View view) {
            super(view);
            name = view.findViewById(R.id.name);
            level = view.findViewById(R.id.level);
            friendDelete = view.findViewById(R.id.friendDelete);
            power = view.findViewById(R.id.power);
            profile = view.findViewById(R.id.profile);
        }
    }

    @NonNull
    @Override
    public MemberInTeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View memberView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_setting_level, parent, false);
        return new MemberInTeamViewHolder(memberView);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberInTeamViewHolder holder, int position) {
        MemberInTeamDTO dto = memberList.get(position);
        MemberDTO memberDTO=new MemberDTO();
        holder.name.setText(dto.getName());
        holder.power.setVisibility(View.INVISIBLE);
        holder.friendDelete.setVisibility(View.INVISIBLE);

        if(dto.getImage() != null){
            Glide.with(context)
                    .load(serverIp + dto.getImage())
                    .into(holder.profile);
        }

        if (dto.getOwner()) {
            holder.level.setText("그룹장");
        } else if (dto.getAdmin()) {
            holder.level.setText("관리자");
        } else {
            holder.level.setText("일반 회원");
        }

        if (loginmember.getName().equals(teamDTO.getOwner())) {

            if (!loginmember.getMemberId().equals(dto.getMemberId())) {
                holder.power.setVisibility(View.VISIBLE);
                holder.friendDelete.setVisibility(View.VISIBLE);

                holder.power.setOnClickListener(v -> {
                    tmDTO = new TeamMemberDTO();
                    tmDTO.setMemberId(dto.getMemberId());
                    tmDTO.setTeamId(teamDTO.getTeamId());

                    String message = dto.getAdmin() ? "관리자 자격을 박탈하시겠습니까?" : "관리자로 승격 시키겠습니까?";
                    onClick_setting_costume_save(v.getContext(), message, (dialog, which) -> {
                        if (dto.getAdmin()) {
                            tc.revokeAdmin(tmDTO, new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        dto.setAdmin(false);
                                        notifyDataSetChanged();
                                        Toast.makeText(v.getContext(), "권한을 박탈했습니다.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(v.getContext(), "권한을 박탈하지 못했습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Log.e("Grant Error", t.getMessage());
                                }
                            });
                        } else {
                            // 관리자 권한 부여 처리
                            tc.authorizeAdmin(tmDTO, new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        dto.setAdmin(true);
                                        notifyDataSetChanged();
                                        Toast.makeText(v.getContext(), "권한을 부여했습니다.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(v.getContext(), "권한을 부여하지 못했습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Log.e("Grant Error", t.getMessage());
                                }
                            });
                        }
                    }, (dialog, which) -> {
                        Toast.makeText(v.getContext(), "취소했습니다.", Toast.LENGTH_SHORT).show();
                    });
                });

                // 친구 추방 처리
                holder.friendDelete.setOnClickListener(v -> {
                    if (!dto.getOwner() && !dto.getAdmin()) { // 그룹장이나 관리자가 아닌 경우만 추방
                        tmDTO = new TeamMemberDTO();
                        tmDTO.setMemberId(dto.getMemberId());
                        tmDTO.setTeamId(teamDTO.getTeamId());
                        String message = dto.getName() + " 님을 추방하시겠습니끼?";

                        onClick_setting_costume_save(v.getContext(), message, (dialog, which) -> {

                            tc.expelTeam(tmDTO, new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        memberList.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, memberList.size());
                                        Toast.makeText(v.getContext(), dto.getName() + " 님을 추방했습니다.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(v.getContext(), dto.getName() + " 님을 추방하지 못했습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Toast.makeText(v.getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }, (dialog, which) -> {
                            Toast.makeText(v.getContext(), "취소했습니다.", Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }
        } else {
            tc.adminListInTeam(teamDTO.getTeamId(), new Callback<List<Long>>() {
                @Override
                public void onResponse(Call<List<Long>> call, Response<List<Long>> response) {
                    if (response.isSuccessful()) {
                        List<Long> body = response.body();
                        if (body.contains(loginmember.getMemberId())) {

                            if (!dto.getAdmin()) {
                                Log.i("admin", "admin");
                                holder.friendDelete.setVisibility(View.VISIBLE);
                                // 친구 추방 처리
                                holder.friendDelete.setOnClickListener(v -> {
                                    if (!dto.getOwner() && !dto.getAdmin()) { // 그룹장이나 관리자가 아닌 경우만 추방
                                        tmDTO = new TeamMemberDTO();
                                        tmDTO.setMemberId(dto.getMemberId());
                                        tmDTO.setTeamId(teamDTO.getTeamId());
                                        String message = dto.getName() + " 님을 추방하시겠습니끼?";

                                        onClick_setting_costume_save(v.getContext(), message, (dialog, which) -> {

                                            tc.expelTeam(tmDTO, new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                    if (response.isSuccessful()) {
                                                        memberList.remove(position);
                                                        notifyItemRemoved(position);
                                                        notifyItemRangeChanged(position, memberList.size());
                                                        Toast.makeText(v.getContext(), dto.getName() + " 님을 추방했습니다.", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(v.getContext(), dto.getName() + " 님을 추방하지 못했습니다.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                    Toast.makeText(v.getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }, (dialog, which) -> {
                                            Toast.makeText(v.getContext(), "취소했습니다.", Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                });
                            }

                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Long>> call, Throwable t) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }
    public void updateData(List<MemberInTeamDTO> newMemberList) {
        this.memberList.clear(); // Clear the old data
        this.memberList.addAll(newMemberList); // Add new data
        notifyDataSetChanged(); // Notify the adapter of the data change
    }


    // Confirm dialog method
    public void onClick_setting_costume_save(Context context, String message, DialogInterface.OnClickListener positive, DialogInterface.OnClickListener negative) {
        new AlertDialog.Builder(context).setTitle("Recoder").setMessage(message).setIcon(R.drawable.album).setPositiveButton(android.R.string.yes, positive).setNegativeButton(android.R.string.no, negative).show();
    }
}
