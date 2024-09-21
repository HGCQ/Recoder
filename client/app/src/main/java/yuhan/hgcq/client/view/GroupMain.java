package yuhan.hgcq.client.view;

import android.content.Context;
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
import yuhan.hgcq.client.model.dto.member.MemberDTO;
import yuhan.hgcq.client.model.dto.team.TeamDTO;

public class GroupMain extends AppCompatActivity {

    /* View */
    ImageButton search, groupAdd;
    EditText searchText;
    RecyclerView groupList;
    BottomNavigationView navi;
    TextView empty;

    /* Adapter */
    TeamAdapter ta;

    /* 서버와 통신 */
    TeamController tc;

    /* 받아온 값 */
    MemberDTO loginMember;

    /* Toast */
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
        getSupportActionBar().setTitle("Recoder");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_group_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /* 서버와 연결할 Controller 생성 */
        tc = new TeamController(this);

        /* View와 Layout 연결 */
        search = findViewById(R.id.search);
        groupAdd = findViewById(R.id.groupAdd);
        empty=findViewById(R.id.empty);

        searchText = findViewById(R.id.searchText);

        groupList = findViewById(R.id.GroupList);

        navi = findViewById(R.id.bottom_navigation_view);

        /* 관련된 페이지 */
        Intent groupMainPage = new Intent(this, GroupMain.class);
        Intent friendListPage = new Intent(this, FriendList.class);
        Intent likePage = new Intent(this, Like.class);
        Intent createGroupPage = new Intent(this, CreateGroup.class);
        Intent albumMainPage = new Intent(this, AlbumMain.class);
        Intent myPage =new Intent(this, MyPage.class);


        Intent getIntent = getIntent();
        /* 받아 올 값 */
        loginMember = (MemberDTO) getIntent.getSerializableExtra("loginMember");

        /* 초기 설정 */
        tc.teamList(new Callback<List<TeamDTO>>() {

            @Override
            public void onResponse(Call<List<TeamDTO>> call, Response<List<TeamDTO>> response) {
                if (response.isSuccessful()) {
                    List<TeamDTO> findGroupList = response.body();
                    if (findGroupList.isEmpty()) {
                        empty.setVisibility(View.VISIBLE);
                    } else {
                        empty.setVisibility(View.INVISIBLE);
                    }
                    ta = new TeamAdapter(GroupMain.this, loginMember, findGroupList);
                    groupList.setAdapter(ta);
                    ta.setOnItemClickListener(new TeamAdapter.OnItemClickListener() {

                        @Override
                        public void onItemClick(View view, int position) {
                            TeamDTO teamDTO = findGroupList.get(position);
                            albumMainPage.putExtra("teamDTO", teamDTO);
                            albumMainPage.putExtra("loginMember", loginMember);
                            Log.i("TeamDTO Check", "TeamDTO: " + teamDTO);

                            Log.i("Found GroupList", "Success");
                            startActivity(albumMainPage);
                        }
                    });
                } else {
                    Log.i("Found GroupList", "Fail");
                }
            }

            @Override
            public void onFailure(Call<List<TeamDTO>> call, Throwable t) {
                Log.e("Found GroupList Error", t.getMessage());
            }
        });

        /* 그룹 생성 버튼 눌림 */
        groupAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGroupPage.putExtra("loginMember", loginMember);
                startActivity(createGroupPage);
            }
        });

        /* 검색 버튼 눌림 */
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = searchText.getText().toString();
                tc.searchTeam(groupName, new Callback<List<TeamDTO>>() {
                    @Override
                    public void onResponse(Call<List<TeamDTO>> call, Response<List<TeamDTO>> response) {
                        if (response.isSuccessful()) {

                            List<TeamDTO> findGroupList = response.body();
                            if (findGroupList.isEmpty()) {
                                empty.setVisibility(View.VISIBLE);
                            } else {
                                empty.setVisibility(View.INVISIBLE);
                            }
                            ta = new TeamAdapter(GroupMain.this, loginMember, findGroupList);
                            groupList.setAdapter(ta);
                            ta.setOnItemClickListener(new TeamAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    TeamDTO teamDTO = findGroupList.get(position);
                                    albumMainPage.putExtra("teamDTO", teamDTO);
                                    startActivity(albumMainPage);
                                    Log.i("Found GroupList", "Success");
                                }
                            });
                            Log.i("Found Group By Name", "Success");
                        } else {
                            Log.i("Found Group By Name", "Fail");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TeamDTO>> call, Throwable t) {
                        handler.post(() -> {
                            Toast.makeText(GroupMain.this, "그룹 검색에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        });
                        Log.e("Found Group By Name Error", t.getMessage());
                    }
                });
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
                        Toast.makeText(GroupMain.this, "로그인 후 이용 가능합니다.", Toast.LENGTH_SHORT).show();
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