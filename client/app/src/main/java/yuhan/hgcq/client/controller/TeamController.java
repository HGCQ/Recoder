package yuhan.hgcq.client.controller;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import yuhan.hgcq.client.model.dto.album.AlbumDTO;
import yuhan.hgcq.client.model.dto.team.TeamDTO;
import yuhan.hgcq.client.model.dto.team.TeamMemberDTO;
import yuhan.hgcq.client.model.service.AlbumService;
import yuhan.hgcq.client.model.service.TeamService;

public class TeamController {

    private TeamService teamService;

    public void createTeam(TeamDTO teamDTO, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = teamService.createTeam(teamDTO);
        call.enqueue(callback);
    }

    public void teamList(TeamDTO teamDTO, Callback<List<TeamDTO>> callback) {
        Call<List<TeamDTO>> call = teamService.teamList();
        call.enqueue(callback);
    }

    public void inviteTeam(TeamMemberDTO teamMemberDTO, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = teamService.inviteTeam(teamMemberDTO);
        call.enqueue(callback);
    }

    public void expelTeam(TeamMemberDTO teamMemberDTO, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = teamService.expelTeam(teamMemberDTO);
        call.enqueue(callback);
    }

    public void updateTeam(TeamDTO teamDTO, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = teamService.updateTeam(teamDTO);
        call.enqueue(callback);
    }

    public void deleteTeam(TeamDTO teamDTO, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = teamService.deleteTeam(teamDTO);
        call.enqueue(callback);
    }

    public void searchTeam(String name, Callback<List<TeamDTO>> callback) {
        Call<List<TeamDTO>> call = teamService.searchTeam(name);
        call.enqueue(callback);
    }
}
