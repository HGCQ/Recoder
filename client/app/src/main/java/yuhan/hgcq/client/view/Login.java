package yuhan.hgcq.client.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.content.DialogInterface;
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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import yuhan.hgcq.client.R;
import yuhan.hgcq.client.config.NetworkClient;
import yuhan.hgcq.client.controller.MemberController;
import yuhan.hgcq.client.model.dto.member.LoginForm;

public class Login extends AppCompatActivity {

    /* View */
    ImageButton login, join;
    EditText email, pw;

    /* 서버와 통신 */
    MemberController mc;

    /* 쿠기 */
    NetworkClient client;

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
        getSupportActionBar().setTitle("로그인");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /* 쿠키 */
        client = NetworkClient.getInstance(this.getApplicationContext());

        /* 서버와 연결할 Controller 생성 */
        mc = new MemberController(this);

        /* View와 Layout 연결 */
        login = findViewById(R.id.login);
        join = findViewById(R.id.join);

        email = findViewById(R.id.id);
        pw = findViewById(R.id.password);

        /* 관련된 페이지 */
        Intent groupMainPage = new Intent(this, GroupMain.class);
        Intent joinPage = new Intent(this, Join.class);

        /* 로그인 버튼 눌림 */
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = email.getText().toString();
                String userPw = pw.getText().toString();

                LoginForm form = new LoginForm();
                form.setEmail(userEmail);
                form.setPassword(userPw);

                mc.loginMember(form, new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            handler.post(() -> {
                                Toast.makeText(Login.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                            });
                            Log.i("Login", "Success");
                            client.saveCookie();
                            startActivity(groupMainPage);
                        } else {
                            handler.post(() -> {
                                Toast.makeText(Login.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                            });
                            Log.i("Login", "Fail");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        handler.post(() -> {
                            Toast.makeText(Login.this, "서버와 통신 실패했습니다. 네트워크를 확인해주세요.", Toast.LENGTH_SHORT).show();
                        });
                        Log.e("Login Error", t.getMessage());
                    }
                });
            }
        });

        /* 회원 가입 버튼 눌림 */
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(joinPage);
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
