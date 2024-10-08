package yuhan.hgcq.client.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import yuhan.hgcq.client.R;
import yuhan.hgcq.client.controller.MemberController;
import yuhan.hgcq.client.model.dto.member.MemberDTO;
import yuhan.hgcq.client.model.dto.member.MemberUpdateForm;

public class Modify extends AppCompatActivity {
    /* View */
    EditText name, pw, pwCheck;
    ImageButton nameCheck, join;

    /* http 통신 */
    MemberController mc;

    /* 받아올 값 */
    MemberDTO loginMember;

    /* 메인 스레드 */
    Handler handler = new Handler(Looper.getMainLooper());

    /* 중복 확인용 */
    String checkedName;
    boolean isDuplicateName = false;

    /* 정규식 */
    String regName = "^[가-힣A-Za-z0-9]{1,8}$";
    String regPw = "^[A-Za-z][A-Za-z0-9!@#$%^&*()_+]{7,19}$";

    /* 뒤로 가기 */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent myPage = new Intent(this, MyPage.class);
                myPage.putExtra("loginMember", loginMember);
                startActivity(myPage);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("정보 수정");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        EdgeToEdge.enable(this);
        /* Layout */
        setContentView(R.layout.activity_modify);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /* 초기화 */
        mc = new MemberController(this);

        name = findViewById(R.id.name);
        pw = findViewById(R.id.password);
        pwCheck = findViewById(R.id.passwordCheck);
        nameCheck = findViewById(R.id.nameCheck);
        join = findViewById(R.id.join);

        /* 관련된 페이지 */
        Intent myPage = new Intent(this, MyPage.class);

        /* 받아 올 값 */
        loginMember = (MemberDTO) getIntent().getSerializableExtra("loginMember");

        /* 이름 중복 확인 */
        nameCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = name.getText().toString();
                if (Pattern.matches(regName, userName)) {
                    mc.duplicateName(userName, new Callback<Boolean>() {
                        @Override
                        public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                            if (response.body()) {
                                isDuplicateName = true;
                                checkedName = userName;
                                handler.post(() -> {
                                    Toast.makeText(Modify.this, "사용 가능한 닉네임입니다.", Toast.LENGTH_SHORT).show();
                                });
                                pw.requestFocus();
                            } else {
                                handler.post(() -> {
                                    Toast.makeText(Modify.this, "중복된 닉네임입니다.", Toast.LENGTH_SHORT).show();
                                });
                                name.requestFocus();
                            }
                        }

                        @Override
                        public void onFailure(Call<Boolean> call, Throwable t) {
                            handler.post(() -> {
                                Toast.makeText(Modify.this, "서버와 통신 실패했습니다. 네트워크를 확인해주세요.", Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                } else {
                    Toast.makeText(Modify.this, "닉네임 형식이 잘못됐습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /* 정보 수정 */
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = name.getText().toString();
                String userPw = pw.getText().toString();
                String userPwCheck = pwCheck.getText().toString();

                /* 값이 비어 있는 지 확인 */
                if (userName.isEmpty()) {
                    Toast.makeText(Modify.this, "닉네임을 입력하세요.", Toast.LENGTH_SHORT).show();
                    name.requestFocus();
                    return;
                } else if (userPw.isEmpty()) {
                    Toast.makeText(Modify.this, "비밀 번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                    pw.requestFocus();
                    return;
                } else if (userPwCheck.isEmpty()) {
                    Toast.makeText(Modify.this, "비밀 번호 확인을 입력하세요.", Toast.LENGTH_SHORT).show();
                    pwCheck.requestFocus();
                    return;
                }

                /* 비밀 번호 형식 확인 */
                if (!Pattern.matches(regPw, userPw)) {
                    Toast.makeText(Modify.this, "비밀 번호 형식이 잘못됐습니다. 다시 입력하세요.", Toast.LENGTH_SHORT).show();
                    pw.requestFocus();
                    return;
                }

                if (userPw.equals(userPwCheck)) {

                    if (isDuplicateName && userName.equals(checkedName)) {
                        onClick_setting_costume_save("수정하시겠습니까?", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MemberUpdateForm form = new MemberUpdateForm(userName, userPw);
                                mc.updateMember(form, new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        if (response.isSuccessful()) {
                                            loginMember.setName(userName);
                                            handler.post(() -> {
                                                Toast.makeText(Modify.this, "정보 수정 성공했습니다.", Toast.LENGTH_SHORT).show();
                                            });
                                            myPage.putExtra("loginMember", loginMember);
                                            startActivity(myPage);
                                        } else {
                                            handler.post(() -> {
                                                Toast.makeText(Modify.this, "정보 수정 실패했습니다.", Toast.LENGTH_SHORT).show();
                                            });
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        handler.post(() -> {
                                            Toast.makeText(Modify.this, "정보 수정 실패했습니다.", Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                });
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(Modify.this, "취소했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(Modify.this, "닉네임 중복 확인을 해주세요.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Modify.this, "비밀 번호와 비밀 번호 확인이 일치하지 않습니다. 다시 입력하세요.", Toast.LENGTH_SHORT).show();
                    pw.requestFocus();
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