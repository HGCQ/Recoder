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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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

public class Chat extends AppCompatActivity {

    /* View */
    EditText chatting;
    ImageButton arrow;
    RecyclerView chatListView;
    BottomNavigationView navi;

    /* Adapter */
    ChatAdapter ca;

    /* 받아올 값 */
    MemberDTO loginMember;
    TeamDTO teamDTO;
    AlbumDTO albumDTO;

    /* 서버와 통신 */
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
        getSupportActionBar().setTitle("채팅");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /* 서버와 연결할 Controller 생성 */
        cc = new ChatController(this);

        /* View와 Layout 연결 */
        chatting = findViewById(R.id.chatting);

        arrow = findViewById(R.id.arrow);

        chatListView = findViewById(R.id.friendList);

        navi = findViewById(R.id.bottom_navigation_view);

        /* 관련된 페이지 */
        Intent groupMainPage = new Intent(this, GroupMain.class);
        Intent friendListPage = new Intent(this, FriendList.class);
        Intent likePage = new Intent(this, Like.class);

        Intent getIntent = getIntent();
        /* 받아 올 값 */
        albumDTO = (AlbumDTO) getIntent.getSerializableExtra("albumDTO");
        teamDTO = (TeamDTO) getIntent.getSerializableExtra("teamDTO");
        loginMember = (MemberDTO) getIntent.getSerializableExtra("loginMember");

        /* 초기 설정 */
        cc.chatList(albumDTO.getAlbumId(), new Callback<List<ChatDTO>>() {
            @Override
            public void onResponse(Call<List<ChatDTO>> call, Response<List<ChatDTO>> response) {
                if (response.isSuccessful()) {
                    List<ChatDTO> chatList = response.body();
                    ca = new ChatAdapter(loginMember, chatList);
                    chatListView.setAdapter(ca);
                    chatListView.scrollToPosition(ca.getItemCount() - 1);
                    Log.i("Found ChattingList", "Success");
                } else {
                    Log.i("Found ChattingList", "Fail");
                }
            }

            @Override
            public void onFailure(Call<List<ChatDTO>> call, Throwable t) {
                Log.e("Found ChattingList Error", t.getMessage());
            }
        });

        /* WebSocket 초기화 */
        if (albumDTO != null) {
            webSocket = new ChatWebSocketClient(albumDTO.getAlbumId(), new ChatWebSocketClient.ChatWebSocketCallback() {
                @Override
                public void onMessageReceived(ChatDTO chatDTO) {
                    Log.i("WebSocket", "Received message: " + chatDTO.getMessage());
                    runOnUiThread(() -> {
                        ca.addChat(chatDTO);
                        chatListView.smoothScrollToPosition(ca.getItemCount() - 1);
                    });
                }
            });

            webSocket.start();
        }

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
                    chatting.setText(""); // 입력 필드 비우기
                }
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
                        Toast.makeText(Chat.this, "로그인 후 이용 가능합니다.", Toast.LENGTH_SHORT).show();
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