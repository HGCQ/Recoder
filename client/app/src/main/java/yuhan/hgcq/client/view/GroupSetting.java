package yuhan.hgcq.client.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import yuhan.hgcq.client.controller.FollowController;
import yuhan.hgcq.client.controller.TeamController;
import yuhan.hgcq.client.model.dto.follow.FollowDTO;
import yuhan.hgcq.client.model.dto.member.MemberDTO;
import yuhan.hgcq.client.model.dto.team.MemberInTeamDTO;
import yuhan.hgcq.client.model.dto.team.TeamDTO;
import yuhan.hgcq.client.model.dto.team.TeamInviteForm;

public class GroupSetting extends AppCompatActivity {
    /* View */
    TextView createGroupText;
    ImageButton friendAdd;
    Button groupLeave, save;
    RecyclerView memberListView;
    RecyclerView memberSettingView;

    /* Adapter */
    MemberInTeamAdapter mita;
    FollowAdapter fa;

    /* 받아올 값 */
    MemberDTO loginMember;
    TeamDTO teamDTO;
    FollowDTO followDTO;

    /* http 통신 */
    TeamController tc;
    FollowController fc;

    /* 메인 스레드 */
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
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("그룹 설정");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        EdgeToEdge.enable(this);
        /* Layout */
        setContentView(R.layout.activity_group_setting);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /* 초기화 */
        tc = new TeamController(this);
        fc = new FollowController(this);

        createGroupText = findViewById(R.id.createGroupText);
        friendAdd = findViewById(R.id.friendAdd);
        groupLeave = findViewById(R.id.groupLeave);
        memberListView = findViewById(R.id.groupSetList);
        memberSettingView = findViewById(R.id.followingList);
        save = findViewById(R.id.save);

        /* 관련된 페이지 */
        Intent groupMainPage = new Intent(this, GroupMain.class);

        /* 받아 올 값 */
        Intent getIntent = getIntent();
        loginMember = (MemberDTO) getIntent.getSerializableExtra("loginMember");
        teamDTO = (TeamDTO) getIntent.getSerializableExtra("teamDTO");
        followDTO = (FollowDTO) getIntent.getSerializableExtra("followDTO");

        /* 초기 설정 */
        createGroupText.setText(teamDTO.getName());
        tc.memberListInTeam(teamDTO.getTeamId(), new Callback<List<MemberInTeamDTO>>() {
            @Override
            public void onResponse(Call<List<MemberInTeamDTO>> call, Response<List<MemberInTeamDTO>> response) {
                if (response.isSuccessful()) {
                    List<MemberInTeamDTO> memberList = response.body();
                    mita = new MemberInTeamAdapter(memberList, GroupSetting.this, teamDTO, loginMember);

                    handler.post(() -> {
                        memberListView.setAdapter(mita);
                    });
                } else {
                    /* Toast 메시지 */
                }
            }

            @Override
            public void onFailure(Call<List<MemberInTeamDTO>> call, Throwable t) {
                /* Toast 메시지 */
            }
        });

        /* 저장 */
        save.setOnClickListener(v -> {
            List<Long> selectedMemberIds = fa.getSelectedItems();
            if (selectedMemberIds.isEmpty()) {
                Toast.makeText(v.getContext(), "선택된 친구가 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            TeamInviteForm form = new TeamInviteForm();
            form.setTeamId(teamDTO.getTeamId());
            form.setMembers(selectedMemberIds);

            onClick_setting_costume_save("초대하시겠습니까?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    tc.inviteTeam(form, new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                handler.post(() -> {
                                    Toast.makeText(v.getContext(), "초대하였습니다.", Toast.LENGTH_SHORT).show();
                                    memberSettingView.setVisibility(View.INVISIBLE);
                                    save.setVisibility(View.INVISIBLE);
                                });
                            } else {
                                handler.post(() -> {
                                    Toast.makeText(v.getContext(), "초대하지 못하였습니다.", Toast.LENGTH_SHORT).show();
                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            handler.post(() -> {
                                Toast.makeText(v.getContext(), "오류 발생", Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(GroupSetting.this, "취소했습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        /* 회원 초대 */
        friendAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fc.inviteFollowingList(teamDTO.getTeamId(), new Callback<List<MemberDTO>>() {
                    @Override
                    public void onResponse(Call<List<MemberDTO>> call, Response<List<MemberDTO>> response) {
                        if (response.isSuccessful()) {
                            List<MemberDTO> followList = response.body();
                            fa = new FollowAdapter(followList, GroupSetting.this, tc, teamDTO);
                            handler.post(() -> {
                                memberSettingView.setVisibility(View.VISIBLE);
                                save.setVisibility(View.VISIBLE);
                                memberSettingView.setAdapter(fa);
                            });
                        } else {
                            /* Toast 메시지 */
                        }
                    }

                    @Override
                    public void onFailure(Call<List<MemberDTO>> call, Throwable t) {
                        /* Toast 메시지 */
                    }
                });
            }
        });

        /* 나가기 */
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
                                    handler.post(() -> {
                                        Toast.makeText(GroupSetting.this, teamDTO.getName() + " 그룹에서 나갔습니다.", Toast.LENGTH_SHORT).show();
                                    });
                                    groupMainPage.putExtra("loginMember", loginMember);
                                    startActivity(groupMainPage);
                                } else {
                                    /* Toast 메시지 */
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                /* Toast 메시지 */
                            }
                        });
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(GroupSetting.this, "취소했습니다.", Toast.LENGTH_SHORT).show();
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

    /* 화면 이벤트 처리 */
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
            } else {
                Rect recyclerViewRect = new Rect();
                memberSettingView.getGlobalVisibleRect(recyclerViewRect);

                if (!recyclerViewRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    handler.post(() -> {
                        memberSettingView.setVisibility(View.INVISIBLE);
                        save.setVisibility(View.INVISIBLE);
                    });
                }

            }
        }
        return super.dispatchTouchEvent(ev);
    }
}