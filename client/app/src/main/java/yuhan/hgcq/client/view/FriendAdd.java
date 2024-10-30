package yuhan.hgcq.client.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
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
    RecyclerView memberListView;

    /* Adapter */
    FollowerAdapter fa;

    /* http 통신 */
    MemberController mc;

    /* 받아올 값 */
    boolean isPrivate;
    MemberDTO loginMember;

    /* 메인 스레드 */
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
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar(); // actionBar 가져오기
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true); // 커스텀 뷰 사용 허용
            actionBar.setDisplayShowTitleEnabled(false); // 기본 제목 비활성화
            actionBar.setDisplayHomeAsUpEnabled(true);
            // 액션바 배경 색상 설정
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c2dcff")));

            // 커스텀 타이틀 텍스트뷰 설정
            TextView customTitle = new TextView(this);
            customTitle.setText("친구 추가"); // 제목 텍스트 설정
            customTitle.setTextSize(20); // 텍스트 크기 조정
            customTitle.setTypeface(ResourcesCompat.getFont(this, R.font.hangle_l)); // 폰트 설정
            customTitle.setTextColor(getResources().getColor(R.color.white)); // 텍스트 색상 설정

            actionBar.setCustomView(customTitle); // 커스텀 뷰 설정
        }


        EdgeToEdge.enable(this);
        /* Layout */
        setContentView(R.layout.activity_friend_add);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /* 초기화 */
        mc = new MemberController(this);

        empty = findViewById(R.id.empty);
        searchText = findViewById(R.id.searchText);
        memberListView = findViewById(R.id.friendList);

        /* 받아 올 값 */
        Intent getIntent = getIntent();
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
                }
            }

            @Override
            public void onFailure(Call<Members> call, Throwable t) {
                /* Toast 메시지 */
            }
        });

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String text = s.toString();

                if (!text.isEmpty() || !text.equals("")) {
                    mc.memberListByName("%" + text + "%", new Callback<Members>() {
                        @Override
                        public void onResponse(Call<Members> call, Response<Members> response) {
                            if (response.isSuccessful()) {
                                List<MemberDTO> findList = response.body().getMemberList();
                                if (findList != null) {
                                    if (findList.isEmpty()) {
                                        handler.post(() -> {
                                            empty.setVisibility(View.VISIBLE);
                                        });
                                    } else {
                                        handler.post(() -> {
                                            empty.setVisibility(View.INVISIBLE);
                                        });
                                    }
                                    handler.post(() -> {
                                        fa.updateList(findList);
                                    });
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Members> call, Throwable t) {

                        }
                    });
                } else {
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
                            }
                        }

                        @Override
                        public void onFailure(Call<Members> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
