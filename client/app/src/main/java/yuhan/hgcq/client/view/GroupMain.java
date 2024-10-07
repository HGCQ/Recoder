package yuhan.hgcq.client.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import yuhan.hgcq.client.R;
import yuhan.hgcq.client.adapter.TeamAdapter;
import yuhan.hgcq.client.controller.TeamController;
import yuhan.hgcq.client.model.dto.album.AlbumDTO;
import yuhan.hgcq.client.model.dto.member.MemberDTO;
import yuhan.hgcq.client.model.dto.team.TeamDTO;

public class GroupMain extends AppCompatActivity {
    /* View */
    ImageButton groupAdd;
    EditText searchText;
    RecyclerView groupList;
    BottomNavigationView navi;
    TextView empty;

    /* Adapter */
    TeamAdapter ta;

    /* http 통신 */
    TeamController tc;

    /* 받아올 값 */
    MemberDTO loginMember;

    /* 메인 스레드 */
    Handler handler = new Handler(Looper.getMainLooper());

    /* 뒤로 가기 */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent selectPage = new Intent(this, Select.class);
                startActivity(selectPage);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Recoder");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        EdgeToEdge.enable(this);
        /* Layout */
        setContentView(R.layout.activity_group_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /* 초기화 */
        tc = new TeamController(this);

        groupAdd = findViewById(R.id.groupAdd);
        empty = findViewById(R.id.empty);
        searchText = findViewById(R.id.searchText);
        groupList = findViewById(R.id.GroupList);
        navi = findViewById(R.id.bottom_navigation_view);

        /* 관련된 페이지 */
        Intent groupMainPage = new Intent(this, GroupMain.class);
        Intent friendListPage = new Intent(this, FriendList.class);
        Intent likePage = new Intent(this, Like.class);
        Intent createGroupPage = new Intent(this, CreateGroup.class);
        Intent albumMainPage = new Intent(this, AlbumMain.class);
        Intent myPage = new Intent(this, MyPage.class);


        /* 받아 올 값 */
        Intent getIntent = getIntent();
        loginMember = (MemberDTO) getIntent.getSerializableExtra("loginMember");

        /* 초기 설정 */
        tc.teamList(new Callback<List<TeamDTO>>() {
            @Override
            public void onResponse(Call<List<TeamDTO>> call, Response<List<TeamDTO>> response) {
                if (response.isSuccessful()) {
                    List<TeamDTO> findGroupList = response.body();
                    if (findGroupList.isEmpty()) {
                        handler.post(() -> {
                            empty.setVisibility(View.VISIBLE);
                        });
                    } else {
                        handler.post(() -> {
                            empty.setVisibility(View.INVISIBLE);
                        });
                    }
                    ta = new TeamAdapter(GroupMain.this, loginMember, findGroupList);
                    handler.post(() -> {
                        groupList.setAdapter(ta);
                        groupList.post(new Runnable() {
                            @Override
                            public void run() {
                                int visibleItemCount = 4;  // 화면에 보일 아이템 개수
                                int itemHeight = getResources().getDimensionPixelSize(R.dimen.item_height);  // 아이템 높이
                                ViewGroup.LayoutParams params = groupList.getLayoutParams();
                                params.height = itemHeight * (visibleItemCount / 2);
                                groupList.setLayoutParams(params);
                            }
                        });
                    });
                    ta.setOnItemClickListener(new TeamAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            TeamDTO teamDTO = findGroupList.get(position);
                            albumMainPage.putExtra("teamDTO", teamDTO);
                            albumMainPage.putExtra("loginMember", loginMember);
                            startActivity(albumMainPage);
                        }
                    });
                } else {
                    /* Toast 메시지 */
                }
            }

            @Override
            public void onFailure(Call<List<TeamDTO>> call, Throwable t) {
                /* Toast 메시지 */
            }
        });

        /* 그룹 생성 */
        groupAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGroupPage.putExtra("loginMember", loginMember);
                startActivity(createGroupPage);
            }
        });

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text=s.toString();
                tc.searchTeam("%" + text + "%", new Callback<List<TeamDTO>>() {
                    @Override
                    public void onResponse(Call<List<TeamDTO>> call, Response<List<TeamDTO>> response) {

                       if (response.isSuccessful()){
                           List<TeamDTO> teamList = response.body();

                        if(teamList!=null){
                            if(teamList.isEmpty()){
                                handler.post(()->{
                                    empty.setVisibility(View.VISIBLE);
                                });
                            }else{
                                handler.post(()->{
                                   empty.setVisibility(View.INVISIBLE);
                                });
                            }
                            handler.post(() -> {
                                ta.updateList(teamList);
                            });
                        }else{
                            Log.i("Found Private Group By Name", "Fail");
                        }
                       }else{
                           Log.e("Search Error", "Failed to fetch Groups: " + response.message());
                       }
                    }
                    @Override
                    public void onFailure(Call<List<TeamDTO>> call, Throwable t) {
                        Log.e("Search Error", "Request failed: " + t.getMessage());
                        handler.post(() -> {
                            Toast.makeText(GroupMain.this, "그룹 검색에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /* 내비게이션 바 */
        navi.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.fragment_home) {
                    groupMainPage.putExtra("loginMember", loginMember);
                    startActivity(groupMainPage);
                    return true;
                } else if (itemId == R.id.fragment_friend) {
                    if (loginMember == null) {
                        handler.post(() -> {
                            Toast.makeText(GroupMain.this, "로그인 후 이용 가능합니다.", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        friendListPage.putExtra("loginMember", loginMember);
                        startActivity(friendListPage);
                    }
                    return true;
                } else if (itemId == R.id.fragment_like) {
                    likePage.putExtra("loginMember", loginMember);
                    startActivity(likePage);
                    return true;
                } else if (itemId == R.id.fragment_setting) {
                    myPage.putExtra("loginMember", loginMember);
                    startActivity(myPage);
                    return true;
                }
                return false;
            }
        });
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