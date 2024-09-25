package yuhan.hgcq.client.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import yuhan.hgcq.client.R;
import yuhan.hgcq.client.adapter.FollowAdapter;
import yuhan.hgcq.client.adapter.MemberInTeamAdapter;
import yuhan.hgcq.client.controller.TeamController;
import yuhan.hgcq.client.model.dto.member.MemberDTO;
import yuhan.hgcq.client.model.dto.team.MemberInTeamDTO;
import yuhan.hgcq.client.model.dto.team.TeamDTO;

public class GroupSetting extends AppCompatActivity {

    /* View */
    TextView createGroupText;
    Button groupLeave;
    RecyclerView memberListView;

    /* Adapter */
    MemberInTeamAdapter mita;

    /* 받아올 값 */
    MemberDTO loginMember;
    TeamDTO teamDTO;

    /* 서버와 통신 */
    TeamController tc;

    /* Toast */
    Handler handler = new Handler(Looper.getMainLooper());

    /* 뒤로 가기 */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent groupMainPage = new Intent(this, GroupMain.class);
                groupMainPage.putExtra("loginMember", loginMember);
                startActivity(groupMainPage);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setTitle("그룹 설정");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_group_setting);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /* 서버와 연결할 Controller 생성 */
        tc = new TeamController(this);

        /* View와 Layout 연결 */
        createGroupText = findViewById(R.id.createGroupText);


        groupLeave = findViewById(R.id.groupLeave);

        memberListView = findViewById(R.id.followingList);

        /* 관련된 페이지 */
        Intent groupMainPage = new Intent(this, GroupMain.class);

        Intent getIntent = getIntent();
        /* 받아 올 값 */
        loginMember = (MemberDTO) getIntent.getSerializableExtra("loginMember");
        teamDTO = (TeamDTO) getIntent.getSerializableExtra("teamDTO");

        /* 초기 설정 */
        if (teamDTO != null) {
            handler.post(()->{
                createGroupText.setText(teamDTO.getName());
            });
            tc.memberListInTeam(teamDTO.getTeamId(), new Callback<List<MemberInTeamDTO>>() {
                @Override
                public void onResponse(Call<List<MemberInTeamDTO>> call, Response<List<MemberInTeamDTO>> response) {
                    if (response.isSuccessful()) {
                        List<MemberInTeamDTO> memberList = response.body();
                        mita = new MemberInTeamAdapter(memberList);
                        handler.post(()->{
                            memberListView.setAdapter(mita);
                        });
                        Log.i("Found MemberList In Team", "Success");
                    } else {
                        Log.i("Found MemberList In Team", "Fail");
                    }
                }

                @Override
                public void onFailure(Call<List<MemberInTeamDTO>> call, Throwable t) {
                    Log.e("Found MemberList In Team Error", t.getMessage());
                }
            });
        }

        /* 나가기 버튼 눌림 */
        groupLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick_setting_costume_save("그룹에서 나가시겠습니까?\n(회장이면 그룹이 삭제됩니다)", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tc.deleteTeam(teamDTO, new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    handler.post(()->{
                                        Toast.makeText(GroupSetting.this, teamDTO.getName() + " 그룹에서 나갔습니다.", Toast.LENGTH_SHORT).show();
                                    });
                                    Log.i("Delete Team", "Success");
                                    groupMainPage.putExtra("loginMember", loginMember);
                                    startActivity(groupMainPage);
                                } else {
                                    Log.i("Delete Team", "Fail");
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Log.e("Delete Team Error", t.getMessage());
                            }
                        });
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.post(()->{
                            Toast.makeText(GroupSetting.this, "취소했습니다.", Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }
        });
    }

    /* Confirm 창 */
    public void onClick_setting_costume_save(String message,
                                             DialogInterface.OnClickListener positive,
                                             DialogInterface.OnClickListener negative) {

        new AlertDialog.Builder(this)
                .setTitle("Recoder")
                .setMessage(message)
                .setIcon(R.drawable.album)
                .setPositiveButton(android.R.string.yes, positive)
                .setNegativeButton(android.R.string.no, negative)
                .show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}