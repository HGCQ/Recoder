package yuhan.hgcq.client.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
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
import yuhan.hgcq.client.adapter.ChatAdapter;
import yuhan.hgcq.client.config.ChatWebSocketClient;
import yuhan.hgcq.client.controller.ChatController;
import yuhan.hgcq.client.model.dto.album.AlbumDTO;
import yuhan.hgcq.client.model.dto.chat.ChatDTO;
import yuhan.hgcq.client.model.dto.member.MemberDTO;
import yuhan.hgcq.client.model.dto.team.TeamDTO;

/* 채팅 삭제 로직 추가 예정 */

public class Chat extends AppCompatActivity {
    /* View */
    EditText chatting;
    ImageButton arrow;
    RecyclerView chatListView;
    BottomNavigationView navi;

    /* Adapter */
    ChatAdapter ca;

    /* 받아올 값 */
    boolean isPrivate = false;
    MemberDTO loginMember;
    TeamDTO teamDTO;
    AlbumDTO albumDTO;

    /* http 통신 */
    ChatController cc;
    ChatWebSocketClient webSocket;

    /* 뒤로 가기 */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent galleryPage = new Intent(this, Gallery.class);
                galleryPage.putExtra("loginMember", loginMember);
                galleryPage.putExtra("teamDTO", teamDTO);
                galleryPage.putExtra("albumDTO", albumDTO);
                startActivity(galleryPage);
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
            actionBar.setTitle("채팅");
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c2dcff")));
            actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 활성화
        }


        EdgeToEdge.enable(this);
        /* Layout */
        setContentView(R.layout.activity_chat);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /* 초기화 */
        cc = new ChatController(this);

        chatting = findViewById(R.id.chatting);
        arrow = findViewById(R.id.arrow);
        chatListView = findViewById(R.id.friendList);
        navi = findViewById(R.id.bottom_navigation_view);

        /* 관련된 페이지 */
        Intent albumMainPage = new Intent(this, AlbumMain.class);
        Intent groupMainPage = new Intent(this, GroupMain.class);
        Intent friendListPage = new Intent(this, FriendList.class);
        Intent likePage = new Intent(this, Like.class);
        Intent myPage = new Intent(this, MyPage.class);

        /* 받아 올 값 */
        Intent getIntent = getIntent();
        albumDTO = (AlbumDTO) getIntent.getSerializableExtra("albumDTO");
        teamDTO = (TeamDTO) getIntent.getSerializableExtra("teamDTO");
        loginMember = (MemberDTO) getIntent.getSerializableExtra("loginMember");

        /* 초기 설정 */
        cc.chatList(albumDTO.getAlbumId(), new Callback<List<ChatDTO>>() {
            @Override
            public void onResponse(Call<List<ChatDTO>> call, Response<List<ChatDTO>> response) {
                if (response.isSuccessful()) {
                    List<ChatDTO> chatList = response.body();
                    ca = new ChatAdapter(loginMember, Chat.this, chatList);
                    chatListView.setAdapter(ca);
                    chatListView.scrollToPosition(ca.getItemCount() - 1);
                }
            }

            @Override
            public void onFailure(Call<List<ChatDTO>> call, Throwable t) {
                /* Toast 메시지 */
            }
        });

        /* WebSocket 초기화 */
        if (albumDTO != null) {
            webSocket = new ChatWebSocketClient(albumDTO.getAlbumId(), new ChatWebSocketClient.ChatWebSocketCallback() {
                @Override
                public void onMessageReceived(ChatDTO chatDTO) {
                    runOnUiThread(() -> {
                        ca.addChat(chatDTO);
                        chatListView.smoothScrollToPosition(ca.getItemCount() - 1);
                    });
                }
            });

            webSocket.start();
        }

        /* 보내기 */
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = chatting.getText().toString();

                if (!message.isEmpty()) {
                    ChatDTO chatDTO = new ChatDTO();
                    chatDTO.setAlbumId(albumDTO.getAlbumId());
                    chatDTO.setWriterId(loginMember.getMemberId());
                    chatDTO.setWriterName(loginMember.getName());
                    chatDTO.setMessage(message);

                    webSocket.sendMessage(chatDTO);
                    ca.addChat(chatDTO);
                    chatListView.smoothScrollToPosition(ca.getItemCount() - 1);
                    chatting.setText("");
                }
            }
        });

        /* 네비게이션 */
        navi.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.fragment_home) {
                    if (isPrivate) {
                        albumMainPage.putExtra("isPrivate", true);
                        albumMainPage.putExtra("loginMember", loginMember);
                        startActivity(albumMainPage);
                    } else {
                        groupMainPage.putExtra("loginMember", loginMember);
                        startActivity(groupMainPage);
                    }
                    return true;
                } else if (itemId == R.id.fragment_friend) {
                    if (loginMember == null) {
                        Toast.makeText(Chat.this, "로그인 후 이용 가능합니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        if (isPrivate) {
                            friendListPage.putExtra("isPrivate", true);
                        }
                        friendListPage.putExtra("loginMember", loginMember);
                        startActivity(friendListPage);
                    }
                    return true;
                } else if (itemId == R.id.fragment_like) {
                    if (isPrivate) {
                        likePage.putExtra("isPrivate", true);
                    }
                    likePage.putExtra("loginMember", loginMember);
                    startActivity(likePage);
                    return true;
                } else if (itemId == R.id.fragment_setting) {
                    if (loginMember == null) {
                        Toast.makeText(Chat.this, "로그인 후 이용 가능합니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        if (isPrivate) {
                            myPage.putExtra("isPrivate", true);
                        }
                        myPage.putExtra("loginMember", loginMember);
                        startActivity(myPage);
                    }
                    return true;
                }
                return false;
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