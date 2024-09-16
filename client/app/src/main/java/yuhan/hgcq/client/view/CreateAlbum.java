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

public class CreateAlbum extends AppCompatActivity {

    /* View */
    Button save;
    EditText createAlbumName;

    /* 개인, 공유 확인 */
    boolean isPrivate;
    Long teamId;

    /* 서버와 통신 */
    AlbumController ac;

    /* 로컬 DB */
    AlbumRepository ar;

    /* Toast */
    Handler handler = new Handler(Looper.getMainLooper());

    /* Request Code */

    /* 뒤로 가기 */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent albumMainPage = new Intent(this, AlbumMain.class);
                startActivity(albumMainPage);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setTitle("앨범 생성");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_album);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /* 서버와 연결할 Controller 생성 */
        ac = new AlbumController(this.getApplicationContext());

        /* 로컬 DB 연결할 Repository 생성 */
        ar = new AlbumRepository(this.getApplicationContext());

        /* View와 Layout 연결 */
        save = findViewById(R.id.save);

        createAlbumName = findViewById(R.id.createAlbumText);

        /* 관련된 페이지 */
        Intent albumMainPage = new Intent(this, AlbumMain.class);

        Intent getIntent = getIntent();
        /* 개인, 공유 확인 */
        isPrivate = getIntent.getBooleanExtra("isPrivate", false);

        /* 공유 값 */
        teamId = getIntent.getLongExtra("teamId", 0L);

        /* 생성 버튼 눌림 */
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String albumName = createAlbumName.getText().toString();

                /* 개인 */
                if (isPrivate) {
                    onClick_setting_costume_save("앨범을 생성하시겠습니까?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            /* 나중에 입력된 날짜 값으로 바꾸기 */
                            Album album = Album.create(LocalDateTime.of(2024, 8, 1, 1, 1, 1), LocalDateTime.of(2024, 9, 16, 1, 1, 1), albumName);
                            ar.create(album, new Callback<Boolean>() {
                                @Override
                                public void onSuccess(Boolean result) {
                                    if (result) {
                                        handler.post(() -> {
                                           Toast.makeText(CreateAlbum.this, "앨범을 생성했습니다.", Toast.LENGTH_SHORT).show();
                                        });
                                        Log.i("Private Album Create", "Success");
                                        albumMainPage.putExtra("isPrivate", true);
                                        startActivity(albumMainPage);
                                    } else {
                                        handler.post(() -> {
                                            Toast.makeText(CreateAlbum.this, "앨범을 생성하는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                                        });
                                        Log.i("Private Album Create", "Fail");
                                    }
                                }

                                @Override
                                public void onError(Exception e) {
                                    handler.post(() -> {
                                        Toast.makeText(CreateAlbum.this, "앨범을 생성하는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                                    });
                                    Log.e("Private Album Create", "Error");
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
                            /* 나중에 날짜 값으로 바꾸기 */
                            AlbumCreateForm form = new AlbumCreateForm(teamId, albumName, LocalDateTime.now(), LocalDateTime.now());
                            ac.createAlbum(form, new retrofit2.Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        handler.post(() -> {
                                            Toast.makeText(CreateAlbum.this, "앨범을 생성했습니다.", Toast.LENGTH_SHORT).show();
                                        });
                                        Log.i("Shared Album Create", "Success");
                                        albumMainPage.putExtra("teamId", teamId);
                                        startActivity(albumMainPage);
                                    } else {
                                        handler.post(() -> {
                                            Toast.makeText(CreateAlbum.this, "앨범을 생성하는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                                        });
                                        Log.i("Shared Album Create", "Fail");
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    handler.post(() -> {
                                        Toast.makeText(CreateAlbum.this, "앨범을 생성하는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                                    });
                                    Log.e("Shared Album Create", "Error");
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
