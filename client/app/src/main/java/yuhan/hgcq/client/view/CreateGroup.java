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
import yuhan.hgcq.client.controller.FollowController;
import yuhan.hgcq.client.controller.TeamController;
import yuhan.hgcq.client.model.dto.member.MemberDTO;
import yuhan.hgcq.client.model.dto.team.TeamCreateForm;
import yuhan.hgcq.client.model.dto.team.TeamDTO;

public class CreateGroup extends AppCompatActivity {

    /* View */
    EditText createGroupText;
    Button save;
    RecyclerView followingListView;

    /* Adapter */
    FollowAdapter fa;

    /* 서버와 통신 */
    TeamController tc;
    FollowController fc;

    /* 받아온 값 */
    MemberDTO loginMember;

    /* Toast */
    Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent groupMainPage = new Intent(this, GroupMain.class);
                startActivity(groupMainPage);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setTitle("그룹 생성");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_group);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        /* 서버와 연결할 Controller 생성 */
        tc = new TeamController(this);
        fc = new FollowController(this);

        /* View와 Layout 연결 */
        createGroupText = findViewById(R.id.createGroupText);

        save = findViewById(R.id.save);

        followingListView = findViewById(R.id.createGroupList);

        /* 관련된 페이지 */
        Intent groupMainPage = new Intent(this, GroupMain.class);

        Intent getIntent = getIntent();
        /* 받아 올 값 */
        loginMember = (MemberDTO) getIntent.getSerializableExtra("loginMember");

        /* 초기 설정 */
        fc.followingList(new Callback<List<MemberDTO>>() {
            @Override
            public void onResponse(Call<List<MemberDTO>> call, Response<List<MemberDTO>> response) {
                if (response.isSuccessful()) {
                    List<MemberDTO> followingList = response.body();
                    fa = new FollowAdapter(followingList);
                    followingListView.setAdapter(fa);
                    Log.i("Found FollowList", "Success");
                } else {
                    Log.i("Found FollowList", "Fail");
                }
            }

            @Override
            public void onFailure(Call<List<MemberDTO>> call, Throwable t) {
                Log.e("Found FollowList Error", t.getMessage());
            }
        });

        /* 생성 버튼 눌림 */
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = createGroupText.getText().toString();
                onClick_setting_costume_save("그룹을 생성하시겠습니까?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<Long> selectedFollowList = fa.getSelectedItems();
                        TeamCreateForm form = new TeamCreateForm();
                        form.setName(groupName);
                        form.setMembers(selectedFollowList);
                        tc.createTeam(form, new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    groupMainPage.putExtra("loginMember", loginMember);
                                    startActivity(groupMainPage);
                                    Log.i("Create Group", "Success");
                                } else {
                                    Log.i("Create Group", "Fail");
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Log.e("Create Group Error", t.getMessage());
                            }
                        });
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(CreateGroup.this, "취소했습니다.", Toast.LENGTH_SHORT).show();
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
