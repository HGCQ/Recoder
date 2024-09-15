package yuhan.hgcq.client.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.content.DialogInterface;
import android.widget.ImageButton;

import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
import yuhan.hgcq.client.model.dto.member.SignupForm;

public class Join extends AppCompatActivity {

    /* View */
    EditText name, email, pw, pwCheck;
    ImageButton nameCheck, emailCheck, join;

    /* http */
    MemberController mc;

    /* 중복 확인용 */
    String checkedName, checkedEmail;
    boolean isDuplicateName = false;
    boolean isDuplicateEmail = false;

    /* 정규식 */
    String regName = "^[가-힣A-Za-z0-9]{1,8}$";
    String regPw = "^[A-Za-z][A-Za-z0-9!@#$%^&*()_+]{7,19}$";
    String regEmail = "\\w+@\\w+\\.\\w+(\\.\\w+)?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setTitle("회원가입");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_join);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /* 서버와 연결할 Controller 생성 */
        mc = new MemberController(this);

        /* View와 Layout 연결 */
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        pw = findViewById(R.id.password);
        pwCheck = findViewById(R.id.passwordCheck);

        nameCheck = findViewById(R.id.nameCheck);
        emailCheck = findViewById(R.id.emailCheck);
        join = findViewById(R.id.join);

        /* 관련된 페이지 */
        Intent loginPage = new Intent(this, Login.class);

        /* 뒤로 가기 버튼 눌림 */

        /* 이름 중복 확인 버튼 눌림 */
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
                                Toast.makeText(Join.this, "사용 가능한 닉네임입니다.", Toast.LENGTH_SHORT).show();
                                email.requestFocus();
                            } else {
                                Toast.makeText(Join.this, "중복된 닉네임입니다.", Toast.LENGTH_SHORT).show();
                                name.requestFocus();
                            }
                        }

                        @Override
                        public void onFailure(Call<Boolean> call, Throwable t) {
                            Toast.makeText(Join.this, "서버와 통신 실패했습니다. 네트워크를 확인해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(Join.this, "닉네임 형식이 잘못됐습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /* 이메일 중복 확인 버튼 눌림 */
        emailCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = email.getText().toString();
                if (Pattern.matches(regEmail, userEmail)) {
                    mc.duplicateEmail(userEmail, new Callback<Boolean>() {
                        @Override
                        public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                            if (response.body()) {
                                isDuplicateEmail = true;
                                checkedEmail = userEmail;
                                Toast.makeText(Join.this, "사용 가능한 이메일입니다.", Toast.LENGTH_SHORT).show();
                                pw.requestFocus();
                            } else {
                                Toast.makeText(Join.this, "중복된 이메일입니다.", Toast.LENGTH_SHORT).show();
                                email.requestFocus();
                            }
                        }

                        @Override
                        public void onFailure(Call<Boolean> call, Throwable t) {
                            Toast.makeText(Join.this, "서버와 통신 실패했습니다. 네트워크를 확인해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(Join.this, "이메일 형식이 잘못됐습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /* 회원 가입 버튼 눌림 */
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = name.getText().toString();
                String userEmail = email.getText().toString();
                String userPw = pw.getText().toString();
                String userPwCheck = pwCheck.getText().toString();

                /* 값이 비어 있는 지 확인 */
                if (userName.isEmpty()) {
                    Toast.makeText(Join.this, "닉네임을 입력하세요.", Toast.LENGTH_SHORT).show();
                    name.requestFocus();
                    return;
                } else if (userEmail.isEmpty()) {
                    Toast.makeText(Join.this, "이메일을 입력하세요.", Toast.LENGTH_SHORT).show();
                    email.requestFocus();
                    return;
                } else if (userPw.isEmpty()) {
                    Toast.makeText(Join.this, "비밀 번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                    pw.requestFocus();
                    return;
                } else if (userPwCheck.isEmpty()) {
                    Toast.makeText(Join.this, "비밀 번호 확인을 입력하세요.", Toast.LENGTH_SHORT).show();
                    pwCheck.requestFocus();
                    return;
                }

                /* 비밀 번호 형식 확인 */
                if (!Pattern.matches(regPw, userPw)) {
                    Toast.makeText(Join.this, "비밀 번호 형식이 잘못됐습니다. 다시 입력하세요.", Toast.LENGTH_SHORT).show();
                    pw.requestFocus();
                    return;
                }

                if (userPw.equals(userPwCheck)) {

                    if (isDuplicateName && userName.equals(checkedName)) {

                        if (isDuplicateEmail && userEmail.equals(checkedEmail)) {
                            onClick_setting_costume_save("가입하시겠습니까?", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SignupForm form = new SignupForm(userName, userEmail, userPw);
                                    mc.joinMember(form, new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            if (response.isSuccessful()) {
                                                Toast.makeText(Join.this, "회원 가입 성공했습니다.", Toast.LENGTH_SHORT).show();
                                                startActivity(loginPage);
                                            } else {
                                                Toast.makeText(Join.this, "회원 가입 실패했습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            Toast.makeText(Join.this, "회원 가입 실패했습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(Join.this, "취소했습니다.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(Join.this, "이메일 중복 확인을 해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Join.this, "닉네임 중복 확인을 해주세요.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Join.this, "비밀 번호와 비밀 번호 확인이 일치하지 않습니다. 다시 입력하세요.", Toast.LENGTH_SHORT).show();
                    pw.requestFocus();
                }
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
}

