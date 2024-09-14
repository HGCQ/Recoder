package yuhan.hgcq.client.controller;

import android.content.Context;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import yuhan.hgcq.client.config.NetworkClient;
import yuhan.hgcq.client.model.dto.album.AlbumDTO;
import yuhan.hgcq.client.model.dto.team.MemberInTeamDTO;
import yuhan.hgcq.client.model.dto.team.TeamCreateForm;
import yuhan.hgcq.client.model.dto.team.TeamDTO;
import yuhan.hgcq.client.model.dto.team.TeamInviteForm;
import yuhan.hgcq.client.model.dto.team.TeamMemberDTO;
import yuhan.hgcq.client.model.dto.team.TeamUpdateForm;
import yuhan.hgcq.client.model.service.AlbumService;
import yuhan.hgcq.client.model.service.TeamService;

public class TeamController {

    private TeamService teamService;

    public TeamController(Context context) {
        NetworkClient client = NetworkClient.getInstance(context.getApplicationContext());
        teamService = client.getTeamService();
    }

    public void createTeam(TeamCreateForm teamCreateForm, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = teamService.createTeam(teamCreateForm);
        call.enqueue(callback);
    }

    public void inviteTeam(TeamInviteForm teamInviteForm, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = teamService.inviteTeam(teamInviteForm);
        call.enqueue(callback);
    }

    public void expelTeam(TeamMemberDTO teamMemberDTO, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = teamService.expelTeam(teamMemberDTO);
        call.enqueue(callback);
    }

    public void updateTeam(TeamUpdateForm teamUpdateForm, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = teamService.updateTeam(teamUpdateForm);
        call.enqueue(callback);
    }

    public void deleteTeam(TeamDTO teamDTO, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = teamService.deleteTeam(teamDTO);
        call.enqueue(callback);
    }

    public void authorizeTeam(TeamMemberDTO teamMemberDTO, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = teamService.authorizeTeam(teamMemberDTO);
        call.enqueue(callback);
    }

    public void revokeTeam(TeamMemberDTO teamMemberDTO, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = teamService.revokeTeam(teamMemberDTO);
        call.enqueue(callback);
    }

    public void teamList(Callback<List<TeamDTO>> callback) {
        Call<List<TeamDTO>> call = teamService.teamList();
        call.enqueue(callback);
    }

    public void searchTeam(String name, Callback<List<TeamDTO>> callback) {
        Call<List<TeamDTO>> call = teamService.searchTeam(name);
        call.enqueue(callback);
    }

    public void memberlistTeam(Long teamId, Callback<List<MemberInTeamDTO>> callback) {
        Call<List<MemberInTeamDTO>> call = teamService.memberlistTeam(teamId);
        call.enqueue(callback);
    }

    public void adminlistTeam(Long teamId, Callback<List<MemberInTeamDTO>> callback) {
        Call<List<MemberInTeamDTO>> call = teamService.adminlistTeam(teamId);
        call.enqueue(callback);
    }
}
