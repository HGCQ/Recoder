package yuhan.hgcq.client.view;

import android.content.Context;
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
import android.content.DialogInterface;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.time.LocalDateTime;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import yuhan.hgcq.client.R;
import yuhan.hgcq.client.controller.AlbumController;
import yuhan.hgcq.client.localDatabase.Repository.AlbumRepository;
import yuhan.hgcq.client.localDatabase.callback.Callback;
import yuhan.hgcq.client.localDatabase.entity.Album;
import yuhan.hgcq.client.model.dto.album.AlbumCreateForm;
import yuhan.hgcq.client.model.dto.member.MemberDTO;
import yuhan.hgcq.client.model.dto.team.TeamDTO;

public class CreateAlbum extends AppCompatActivity {
    /* View */
    Button save;
    EditText createAlbumName;

    /* 받아올 값 */
    boolean isPrivate;
    MemberDTO loginMember;
    TeamDTO teamDTO;

    /* http 통신 */
    AlbumController ac;

    /* Room DB */
    AlbumRepository ar;

    /* 메인 스레드 */
    Handler handler = new Handler(Looper.getMainLooper());

    /* 뒤로 가기 */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent albumMainPage = new Intent(this, AlbumMain.class);
                if (isPrivate) {
                    albumMainPage.putExtra("isPrivate", true);
                } else {
                    albumMainPage.putExtra("teamDTO", teamDTO);
                }
                albumMainPage.putExtra("loginMember", loginMember);
                startActivity(albumMainPage);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("앨범 생성");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        EdgeToEdge.enable(this);
        /* Layout */
        setContentView(R.layout.activity_create_album);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /* 초기화 */
        ac = new AlbumController(this.getApplicationContext());

        ar = new AlbumRepository(this.getApplicationContext());

        save = findViewById(R.id.save);
        createAlbumName = findViewById(R.id.AlbumText);

        /* 관련된 페이지 */
        Intent albumMainPage = new Intent(this, AlbumMain.class);

        /* 받아올 값 */
        Intent getIntent = getIntent();
        isPrivate = getIntent.getBooleanExtra("isPrivate", false);
        teamDTO = (TeamDTO) getIntent.getSerializableExtra("teamDTO");
        loginMember = (MemberDTO) getIntent.getSerializableExtra("loginMember");

        /* 제목 */
        if (isPrivate) {
            getSupportActionBar().setTitle("[개인] 앨범 생성");
        } else if (teamDTO != null) {
            getSupportActionBar().setTitle("[공유] 앨범 생성");
        }

        /* 생성 */
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String albumName = createAlbumName.getText().toString();

                /* 개인 */
                if (isPrivate) {
                    onClick_setting_costume_save("앨범을 생성하시겠습니까?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Album album = Album.create(LocalDateTime.now(), LocalDateTime.now(), albumName);
                            ar.create(album, new Callback<Boolean>() {
                                @Override
                                public void onSuccess(Boolean result) {
                                    if (result) {
                                        handler.post(() -> {
                                            Toast.makeText(CreateAlbum.this, "앨범을 생성했습니다.", Toast.LENGTH_SHORT).show();
                                        });
                                        albumMainPage.putExtra("isPrivate", true);
                                        albumMainPage.putExtra("loginMember", loginMember);
                                        startActivity(albumMainPage);
                                    } else {
                                        handler.post(() -> {
                                            Toast.makeText(CreateAlbum.this, "앨범을 생성하는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                }

                                @Override
                                public void onError(Exception e) {
                                    handler.post(() -> {
                                        Toast.makeText(CreateAlbum.this, "앨범을 생성하는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                                    });
                                }
                            });
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(CreateAlbum.this, "취소했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                /* 공유 */
                else {
                    onClick_setting_costume_save("앨범을 생성하시겠습니까?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (teamDTO != null) {
                                Long teamId = teamDTO.getTeamId();

                                AlbumCreateForm form = new AlbumCreateForm(teamId, albumName);
                                ac.createAlbum(form, new retrofit2.Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        if (response.isSuccessful()) {
                                            handler.post(() -> {
                                                Toast.makeText(CreateAlbum.this, "앨범을 생성했습니다.", Toast.LENGTH_SHORT).show();
                                            });
                                            albumMainPage.putExtra("teamDTO", teamDTO);
                                            albumMainPage.putExtra("loginMember", loginMember);
                                            startActivity(albumMainPage);
                                        } else {
                                            handler.post(() -> {
                                                Toast.makeText(CreateAlbum.this, "앨범을 생성하는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                                            });
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        handler.post(() -> {
                                            Toast.makeText(CreateAlbum.this, "앨범을 생성하는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                });
                            }
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(CreateAlbum.this, "취소했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
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