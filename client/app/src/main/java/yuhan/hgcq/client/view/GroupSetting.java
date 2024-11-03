package yuhan.hgcq.client.view;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.IOException;
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
    TextView createGroupText, followingListViewTop;
    ImageButton friendAdd;
    Button groupLeave, save, groupImage;
    RecyclerView groupSetList, followingList;
    ImageView followingListView;

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

    /* Intent 요청 코드 */
    private static final int GALLERY = 1000;
    private static final int REQUEST_PERMISSION = 1111;
    private List<MemberInTeamDTO> updatedMemberList;


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
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true); // 커스텀 뷰 사용 허용
            actionBar.setDisplayShowTitleEnabled(false); // 기본 제목 비활성화
            actionBar.setDisplayHomeAsUpEnabled(true);
            // 액션바 배경 색상 설정
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c2dcff")));

            // 커스텀 타이틀 텍스트뷰 설정
            TextView customTitle = new TextView(this);
            customTitle.setText("그룹 설정"); // 제목 텍스트 설정
            customTitle.setTextSize(20); // 텍스트 크기 조정
            customTitle.setTypeface(ResourcesCompat.getFont(this, R.font.hangle_l)); // 폰트 설정
            customTitle.setTextColor(getResources().getColor(R.color.white)); // 텍스트 색상 설정

            actionBar.setCustomView(customTitle); // 커스텀 뷰 설정
        }
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

        groupSetList = findViewById(R.id.groupSetList);
        followingListView = findViewById(R.id.followingListView);
        followingList = findViewById(R.id.followingList);
        groupImage = findViewById(R.id.groupImage);
        followingListViewTop = findViewById(R.id.followingListViewTop);
        createGroupText = findViewById(R.id.createGroupText);
        friendAdd = findViewById(R.id.friendAdd);
        groupLeave = findViewById(R.id.groupLeave);
        save = findViewById(R.id.save);

        /* 갤러리 */
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        gallery.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        gallery.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

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
                        groupSetList.setAdapter(mita);
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

        /* 그룹 대표 이미지 설정 */
        groupImage.setOnClickListener(v -> {
            /* Android 11 이상 */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    startActivityForResult(Intent.createChooser(gallery, "사진 선택"), GALLERY);
                } else {
                    Toast.makeText(GroupSetting.this, "권한이 없습니다.", Toast.LENGTH_SHORT).show();
                    Intent permission = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    permission.addCategory("android.intent.category.DEFAULT");
                    permission.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                    startActivityForResult(permission, REQUEST_PERMISSION);
                }
            }
        });

        /* 저장 !*/
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
                                // Fetch updated member list after invitation
                                tc.memberListInTeam(teamDTO.getTeamId(), new Callback<List<MemberInTeamDTO>>() {
                                    @Override
                                    public void onResponse(Call<List<MemberInTeamDTO>> call, Response<List<MemberInTeamDTO>> response) {
                                        if (response.isSuccessful()) {
                                            updatedMemberList = response.body(); // Update the list
                                            handler.post(() -> {
                                                Toast.makeText(v.getContext(), "초대하였습니다.", Toast.LENGTH_SHORT).show();
                                                followingList.setVisibility(View.INVISIBLE);
                                                followingListView.setVisibility(View.INVISIBLE);
                                                followingListViewTop.setVisibility(View.INVISIBLE);
                                                save.setVisibility(View.INVISIBLE);
                                                mita.updateData(updatedMemberList); // Now this should work without error
                                            });
                                        } else {
                                            handler.post(() -> {
                                                Toast.makeText(v.getContext(), "회원 목록을 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                                            });
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<List<MemberInTeamDTO>> call, Throwable t) {
                                        handler.post(() -> {
                                            Toast.makeText(v.getContext(), "오류 발생", Toast.LENGTH_SHORT).show();
                                        });
                                    }
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
                                followingList.setVisibility(View.VISIBLE);
                                followingListView.setVisibility(View.VISIBLE);
                                followingListViewTop.setVisibility(View.VISIBLE);
                                save.setVisibility(View.VISIBLE);
                                followingList.setAdapter(fa);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData(); // 사용자가 선택한 이미지의 URI
                // 서버에 이미지 업로드
                tc.upload(teamDTO.getTeamId(), uri, new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            handler.post(() -> {
                                Toast.makeText(GroupSetting.this, "프로필 등록을 성공했습니다.", Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            handler.post(() -> {
                                Toast.makeText(GroupSetting.this, "프로필 등록을 실패했습니다.", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        handler.post(() -> {
                            Toast.makeText(GroupSetting.this, "서버와 통신을 실패했습니다.", Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }
        }
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
                followingListView.getGlobalVisibleRect(recyclerViewRect);

                if (!recyclerViewRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    handler.post(() -> {
                        followingList.setVisibility(View.INVISIBLE);
                        followingListView.setVisibility(View.INVISIBLE);
                        followingListViewTop.setVisibility(View.INVISIBLE);
                        save.setVisibility(View.INVISIBLE);
                    });
                }

            }
        }
        return super.dispatchTouchEvent(ev);
    }
}