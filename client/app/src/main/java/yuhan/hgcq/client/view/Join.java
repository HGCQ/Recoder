package yuhan.hgcq.client.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
import yuhan.hgcq.client.controller.SignupForm;

public class Join extends AppCompatActivity {

    ImageButton back,join,nameCheck,emailCheck;
    EditText name,email,password,passwordCheck;
    private Context  context;
    private SignupForm signup;
    private boolean isNotNameCheck=false;
    private boolean isNotEmailCheck=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_join);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        signup =new SignupForm();
        String regName= "^[가-힣A-Za-z0-9]{1,8}$";
        String regPw = "^[A-Za-z][A-Za-z0-9!@#$%^&*()_+]{7,19}$";
        String regEmail = "\\w+@\\w+\\.\\w+(\\.\\w+)?";
        this.context=this;

        back=(ImageButton) findViewById(R.id.back);
        join=(ImageButton) findViewById(R.id.join);
        nameCheck=(ImageButton) findViewById(R.id.nameCheck);
        emailCheck=(ImageButton) findViewById(R.id.emailCheck);

        name=(EditText) findViewById(R.id.name);
        email=(EditText) findViewById(R.id.email);
        password=(EditText) findViewById(R.id.password);
        passwordCheck=(EditText) findViewById(R.id.passwordCheck);

        Intent goToLogin=new Intent(this,Login.class);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(goToLogin);
            }
        });
        nameCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName=name.getText().toString();
                signup.duplicateName(userName, new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if(response.isSuccessful()){
                         if(Pattern.matches(regName,userName)){
                             if(response.body()){
                                 isNotNameCheck=true;
                                 Toast.makeText(context, "사용 가능한 닉네임입니다.", Toast.LENGTH_SHORT).show();
                             } else{
                                 Toast.makeText(context, "중복된 닉네임입니다.", Toast.LENGTH_SHORT).show();
                             }
                         }else{
                             Toast.makeText(context, "닉네임 형식이 잘못됐습니다.", Toast.LENGTH_SHORT).show();
                         }
                        }else{
                            Toast.makeText(context, "중복된 닉네임입니다.", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
                        Toast.makeText(context, "통신 실패.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        emailCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = email.getText().toString();
                signup.duplicateEmail(userEmail, new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if (response.isSuccessful()) {
                            if (Pattern.matches(regEmail, userEmail)) {
                                if (response.body()) {
                                    isNotEmailCheck = true;
                                    Toast.makeText(context, "사용 가능 이메일입니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "중복된 이메일입니다.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(context, "이메일 형식이 잘못됐습니다.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "중복된 이메일 입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
                        Toast.makeText(context, "통신 실패.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = email.getText().toString();
                String userPassword = password.getText().toString();
                String userPasswordCheck = passwordCheck.getText().toString();
                String userName = name.getText().toString();

                // 비어 있는지 확인
                if (userName.isEmpty()) {
                    Toast.makeText(context, "값을 입력하세요.", Toast.LENGTH_SHORT).show();
                    name.requestFocus();
                    return;
                } else if (userEmail.isEmpty()) {
                    Toast.makeText(context, "값을 입력하세요.", Toast.LENGTH_SHORT).show();
                    email.requestFocus();
                    return;
                } else if (userPassword.isEmpty()) {
                    Toast.makeText(context, "값을 입력하세요.", Toast.LENGTH_SHORT).show();
                    password.requestFocus();
                    return;
                } else if (userPasswordCheck.isEmpty()) {
                    Toast.makeText(context, "값을 입력하세요.", Toast.LENGTH_SHORT).show();
                    passwordCheck.requestFocus();
                    return;
                }

                // 정규식 확인
                if (!Pattern.matches(regPw, userPassword)) {
                    Toast.makeText(context, "비밀번호 형식이 잘못됐습니다. 다시 입력하세요.", Toast.LENGTH_SHORT).show();
                    password.requestFocus();
                    return;
                }

                // 비밀번호 일치 확인

                if (userPassword.equals(userPasswordCheck)) {

                    if (isNotEmailCheck) {

                        if (isNotNameCheck) {
                            // 회원 가입
                          signup.joinMember(new yuhan.hgcq.client.model.dto.member.SignupForm(), new Callback<ResponseBody>() {
                              @Override
                              public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                  if (response.isSuccessful()) {
                                      Toast.makeText(context, "회원 가입 성공!", Toast.LENGTH_SHORT).show();
                                      Log.d("회원 가입 성공", "Code: " + response.code());
                                      startActivity(goToLogin);
                                  } else {
                                      Toast.makeText(context, "이미 존재하지 않는 이메일입니다.", Toast.LENGTH_SHORT).show();
                                      Log.d("회원 가입 실패", "Code: " + response.code());
                                  }

                              }

                              @Override
                              public void onFailure(Call<ResponseBody> call, Throwable t) {
                                  Toast.makeText(context, "서버 응답 오류", Toast.LENGTH_SHORT).show();
                                  Log.e("회원 가입 실패", t.getMessage());
                              }
                          });
                        } else {
                            Toast.makeText(context, "이름이 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "이메일이 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
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
}