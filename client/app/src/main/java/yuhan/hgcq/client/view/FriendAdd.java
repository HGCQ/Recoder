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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import yuhan.hgcq.client.R;
import yuhan.hgcq.client.adapter.FollowerAdapter;
import yuhan.hgcq.client.controller.MemberController;
import yuhan.hgcq.client.model.dto.member.MemberDTO;
import yuhan.hgcq.client.model.dto.member.Members;

public class FriendAdd extends AppCompatActivity {

    /* View */
    TextView empty;
    EditText searchText;
    ImageButton search;
    RecyclerView memberListView;

    /* Adapter */
    FollowerAdapter fa;

    /* 서버와 통신 */
    MemberController mc;

    /* 개인 공유 확인 */
    boolean isPrivate;

    /* 받아올 값 */
    MemberDTO loginMember;

    /* Toast */
    Handler handler = new Handler(Looper.getMainLooper());

    /* 뒤로 가기 */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent friendListPage = new Intent(this, FriendList.class);
                if (isPrivate) {
                    friendListPage.putExtra("isPrivate", isPrivate);
                }
                friendListPage.putExtra("loginMember", loginMember);
                startActivity(friendListPage);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setTitle("친구 추가");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_friend_add);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /* 서버와 연결할 Controller 생성 */
        mc = new MemberController(this);

        /* View와 Layout 연결 */
        empty = findViewById(R.id.empty);

        searchText = findViewById(R.id.searchText);

        search = findViewById(R.id.search);

        memberListView = findViewById(R.id.friendList);

        Intent getIntent = getIntent();
        /* 받아 올 값 */
        isPrivate = getIntent.getBooleanExtra("isPrivate", false);
        loginMember = (MemberDTO) getIntent.getSerializableExtra("loginMember");

        /* 초기 설정 */
        mc.memberList(new Callback<Members>() {
            @Override
            public void onResponse(Call<Members> call, Response<Members> response) {
                if (response.isSuccessful()) {
                    Members body = response.body();
                    List<MemberDTO> memberList = body.getMemberList();
                    if (memberList.isEmpty()) {
                        handler.post(() -> {
                            empty.setVisibility(View.VISIBLE);
                        });
                    } else {
                        handler.post(() -> {
                            empty.setVisibility(View.INVISIBLE);
                        });
                    }
                    fa = new FollowerAdapter(FriendAdd.this, memberList, body.getFollowingList());
                    handler.post(() -> {
                        memberListView.setAdapter(fa);
                    });
                    Log.i("Found MemberList", "Success");
                } else {
                    Log.i("Found MemberList", "Fail");
                }
            }

            @Override
            public void onFailure(Call<Members> call, Throwable t) {
                Log.e("Found MemberList Error", t.getMessage());
            }
        });

        /* 검색 눌림 */
        search.setOnClickListener(v -> {
            String name = searchText.getText().toString();

            mc.memberListByName(name, new Callback<Members>() {
                @Override
                public void onResponse(Call<Members> call, Response<Members> response) {
                    if (response.isSuccessful()) {
                        Members body = response.body();
                        List<MemberDTO> memberList = body.getMemberList();
                        fa = new FollowerAdapter(FriendAdd.this, memberList, body.getFollowingList());
                        handler.post(() -> {
                           memberListView.setAdapter(fa);
                        });
                        Log.i("Found MemberList By Name", "Success");
                    } else {
                        Log.i("Found MemberList By Name", "Fail");
                    }
                }

                @Override
                public void onFailure(Call<Members> call, Throwable t) {
                    Log.e("Found MemberList By Name Error", t.getMessage());
                }
            });
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