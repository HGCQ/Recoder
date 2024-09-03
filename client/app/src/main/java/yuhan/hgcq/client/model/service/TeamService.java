package yuhan.hgcq.client.model.service;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import yuhan.hgcq.client.model.dto.team.TeamDTO;
import yuhan.hgcq.client.model.dto.team.TeamMemberDTO;

public interface TeamService {

    @POST("/team/create")
    Call<ResponseBody> createTeam(@Body TeamDTO teamDTO);

    @GET("/team/list")
    Call<List<TeamDTO>> teamList();

    @POST("/team/invite")
    Call<ResponseBody> inviteTeam(@Body TeamMemberDTO teamMemberDTO);

    @POST("/team/expel")
    Call<ResponseBody> expelTeam(@Body TeamMemberDTO teamMemberDTO);

    @POST("/team/update")
    Call<ResponseBody> updateTeam(@Body TeamDTO teamDTO);

    @POST("/team/delete")
    Call<ResponseBody> deleteTeam(@Body TeamDTO teamDTO);

    @GET("/team/search")
    Call<List<TeamDTO>> searchTeam(@Query("name") String name);
}
