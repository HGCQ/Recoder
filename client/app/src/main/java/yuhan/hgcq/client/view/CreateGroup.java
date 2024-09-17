
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
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import yuhan.hgcq.client.R;
import yuhan.hgcq.client.controller.FollowController;
import yuhan.hgcq.client.controller.TeamController;
import yuhan.hgcq.client.localDatabase.callback.Callback;
import yuhan.hgcq.client.model.dto.member.MemberDTO;
import yuhan.hgcq.client.model.dto.team.TeamCreateForm;
import yuhan.hgcq.client.model.service.FollowService;

public class CreateGroup extends AppCompatActivity {


    /* View */
    ImageButton addGroup;
    EditText createGroupName;
    Button save;
    /* 개인, 공유 확인 */
    Boolean isPrivate;
    /* 서버와 통신 */
    TeamController tc;
    FollowController fc;
    FollowService fs;
    /* 로컬 DB */
    Long teamId;
    /* Toast */
    Handler handler = new Handler(Looper.getMainLooper());

    /* Request Code */
    /* 뒤로 가기 */
  /*   back.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(groupMainPage);
        }
    });

*/
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

        tc = new TeamController(this.getApplicationContext());
        fc=new FollowController(this.getApplicationContext());


        /* 로컬 DB 연결할 Repository 생성 *//*
        /* View와 Layout 연결 */

        save = findViewById(R.id.save);
        createGroupName = findViewById(R.id.Groupname);

        /* 관련된 페이지 */

        Intent groupMainPage = new Intent(this, GroupMain.class);

        Intent getIntent = getIntent();

        /* 개인, 공유 확인 */

        isPrivate = getIntent.getBooleanExtra("isPrivate", false);
        teamId = getIntent.getLongExtra("teamId", 0L);

        /* 받아 올 값 */
        /* 생성 버튼 눌림 */
        /* 개인 */
        /* 공유 */
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = createGroupName.getText().toString();

                onClick_setting_costume_save("그룹을 생성하시겠습니까?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /*리사이클 뷰가 없음
                        * 그룹을 생성할 때 리사이클 뷰 안 그룹 안이 어떻게 보이는건지 모르겟다^^
                        * 팔로워 이름이랑 아... 안 만들어져 있구나 못하는게 당연하구나^^ */
                        TeamCreateForm form = new TeamCreateForm(, fs.followList().toString());
                        tc.createTeam(form, new retrofit2.Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    handler.post(() -> {
                                        Toast.makeText(CreateGroup.this, "그룹을 생성했습니다.", Toast.LENGTH_SHORT).show();
                                    });
                                    Log.i("Group Create", "Success");
                                    groupMainPage.putExtra("teamId", teamId);
                                    startActivity(groupMainPage);
                                } else {
                                    handler.post(() -> {
                                        Toast.makeText(CreateGroup.this, "앨범을 생성하는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                                    });
                                    Log.i("Group Create", "Fail");
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                handler.post(() -> {
                                    Toast.makeText(CreateGroup.this, "앨범을 생성하는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                                });
                                Log.e("Shared Album Create", "Error");
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
