package yuhan.hgcq.client.model.service;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import yuhan.hgcq.client.model.dto.team.MemberInTeamDTO;
import yuhan.hgcq.client.model.dto.team.TeamCreateForm;
import yuhan.hgcq.client.model.dto.team.TeamDTO;
import yuhan.hgcq.client.model.dto.team.TeamInviteForm;
import yuhan.hgcq.client.model.dto.team.TeamMemberDTO;
import yuhan.hgcq.client.model.dto.team.TeamUpdateForm;

public interface TeamService {

    @POST("/team/create")
    Call<ResponseBody> createTeam(@Body TeamCreateForm teamCreateForm);

    @POST("/team/invite")
    Call<ResponseBody> inviteTeam(@Body TeamInviteForm teamInviteForm);

    @POST("/team/expel")
    Call<ResponseBody> expelTeam(@Body TeamMemberDTO teamMemberDTO);

    @POST("/team/update")
    Call<ResponseBody> updateTeam(@Body TeamUpdateForm teamUpdateForm);

    @POST("/team/delete")
    Call<ResponseBody> deleteTeam(@Body TeamDTO teamDTO);

    @POST("/team/authorize")
    Call<ResponseBody> authorizeAdmin(@Body TeamMemberDTO teamMemberDTO);

    @POST("/team/revoke")
    Call<ResponseBody> revokeAdmin(@Body TeamMemberDTO teamMemberDTO);

    @GET("/team/list")
    Call<List<TeamDTO>> teamList();

    @GET("/team/list/name")
    Call<List<TeamDTO>> searchTeam(@Query("name") String name);

    @GET("/team/memberlist/teamId")
    Call<List<MemberInTeamDTO>> memberListInTeam(@Query("teamId") Long teamId);

    @GET("/team/adminlist/teamId")
    Call<List<Long>> adminListInTeam(@Query("teamId") Long teamId);

    @Multipart
    @POST("/team/upload")
    Call<ResponseBody> upload(@Part("teamId") RequestBody teamId, @Part MultipartBody.Part file);
}
