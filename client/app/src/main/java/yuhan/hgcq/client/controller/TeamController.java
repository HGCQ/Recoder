package yuhan.hgcq.client.controller;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import yuhan.hgcq.client.config.NetworkClient;
import yuhan.hgcq.client.model.dto.team.MemberInTeamDTO;
import yuhan.hgcq.client.model.dto.team.TeamCreateForm;
import yuhan.hgcq.client.model.dto.team.TeamDTO;
import yuhan.hgcq.client.model.dto.team.TeamInviteForm;
import yuhan.hgcq.client.model.dto.team.TeamMemberDTO;
import yuhan.hgcq.client.model.dto.team.TeamUpdateForm;
import yuhan.hgcq.client.model.service.TeamService;

public class TeamController {

    private TeamService teamService;
    private Context context;

    public TeamController(Context context) {
        this.context = context;
        NetworkClient client = NetworkClient.getInstance(context.getApplicationContext());
        teamService = client.getTeamService();
    }

    /**
     * 그룹 생성
     *
     * @param teamCreateForm 그룹 생성 폼
     * @param callback       비동기 콜백
     */
    public void createTeam(TeamCreateForm teamCreateForm, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = teamService.createTeam(teamCreateForm);
        call.enqueue(callback);
    }

    /**
     * 그룹에 초대
     *
     * @param teamInviteForm 회원 초대 폼
     * @param callback       비동기 콜백
     */
    public void inviteTeam(TeamInviteForm teamInviteForm, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = teamService.inviteTeam(teamInviteForm);
        call.enqueue(callback);
    }

    /**
     * 그룹에서 추방
     *
     * @param teamMemberDTO 그룹 회원 DTO
     * @param callback      비동기 콜백
     */
    public void expelTeam(TeamMemberDTO teamMemberDTO, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = teamService.expelTeam(teamMemberDTO);
        call.enqueue(callback);
    }

    /**
     * 그룹 수정
     *
     * @param teamUpdateForm 그룹 수정 폼
     * @param callback       비동기 콜백
     */
    public void updateTeam(TeamUpdateForm teamUpdateForm, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = teamService.updateTeam(teamUpdateForm);
        call.enqueue(callback);
    }

    /**
     * 그룹 삭제
     *
     * @param teamDTO  그룹 DTO
     * @param callback 비동기 콜백
     */
    public void deleteTeam(TeamDTO teamDTO, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = teamService.deleteTeam(teamDTO);
        call.enqueue(callback);
    }

    /**
     * 관리자 권한 부여
     *
     * @param teamMemberDTO 그룹 회원 DTO
     * @param callback      비동기 콜백
     */
    public void authorizeAdmin(TeamMemberDTO teamMemberDTO, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = teamService.authorizeAdmin(teamMemberDTO);
        call.enqueue(callback);
    }

    /**
     * 관리자 권한 박탈
     *
     * @param teamMemberDTO 그룹 회원 DTO
     * @param callback      비동기 콜백
     */
    public void revokeAdmin(TeamMemberDTO teamMemberDTO, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = teamService.revokeAdmin(teamMemberDTO);
        call.enqueue(callback);
    }

    /**
     * 그룹 리스트
     *
     * @param callback 비동기 콜백
     */
    public void teamList(Callback<List<TeamDTO>> callback) {
        Call<List<TeamDTO>> call = teamService.teamList();
        call.enqueue(callback);
    }

    /**
     * 그룹 검색
     *
     * @param name     그룹 이름
     * @param callback 비동기 콜백
     */
    public void searchTeam(String name, Callback<List<TeamDTO>> callback) {
        Call<List<TeamDTO>> call = teamService.searchTeam(name);
        call.enqueue(callback);
    }

    /**
     * 그룹에 속한 회원 리스트
     *
     * @param teamId   그룹 id
     * @param callback 비동기 콜백
     */
    public void memberListInTeam(Long teamId, Callback<List<MemberInTeamDTO>> callback) {
        Call<List<MemberInTeamDTO>> call = teamService.memberListInTeam(teamId);
        call.enqueue(callback);
    }

    /**
     * 그룹에 속한 관리자 리스트
     *
     * @param teamId   그룹 id
     * @param callback 비동기 콜백
     */
    public void adminListInTeam(Long teamId, Callback<List<Long>> callback) {
        Call<List<Long>> call = teamService.adminListInTeam(teamId);
        call.enqueue(callback);
    }

    public void upload(Long teamId, Uri uri, Callback<ResponseBody> callback) {
        RequestBody teamIdPart = RequestBody.create(
                MediaType.parse("text/plain"),
                String.valueOf(teamId)
        );

        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            byte[] imageBytes = IOUtils.toByteArray(inputStream); // Apache commons-io 사용
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), imageBytes);
            MultipartBody.Part image = MultipartBody.Part.createFormData("file", getFileNameFromUri(uri), reqFile);
            Call<ResponseBody> call = teamService.upload(teamIdPart, image);
            call.enqueue(callback);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getFileNameFromUri(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DISPLAY_NAME };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
            cursor.close();
            return fileName;
        }
        return "unknown_file_name";
    }
}
