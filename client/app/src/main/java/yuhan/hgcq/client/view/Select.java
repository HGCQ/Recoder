package yuhan.hgcq.client.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import yuhan.hgcq.client.R;
import yuhan.hgcq.client.controller.MemberController;
import yuhan.hgcq.client.model.dto.member.MemberDTO;

public class Select extends AppCompatActivity {
    /* View */
    ImageButton privated, share;

    /* http 통신 */
    MemberController mc;

    /* 받아올 값 */
    MemberDTO loginMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar(); // actionBar 가져오기
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true); // 커스텀 뷰 사용 허용
            actionBar.setDisplayShowTitleEnabled(false); // 기본 제목 비활성화

            // 액션바 배경 색상 설정
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c2dcff")));

            // 커스텀 타이틀 텍스트뷰 설정
            TextView customTitle = new TextView(this);
            customTitle.setText("Recoder"); // 제목 텍스트 설정
            customTitle.setTextSize(20); // 텍스트 크기 조정
            customTitle.setTypeface(ResourcesCompat.getFont(this, R.font.hangle_l)); // 폰트 설정
            customTitle.setTextColor(getResources().getColor(R.color.white)); // 텍스트 색상 설정

            actionBar.setCustomView(customTitle); // 커스텀 뷰 설정
        }

        EdgeToEdge.enable(this);
        /* Layout */
        setContentView(R.layout.activity_select);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /* 초기화 */
        mc = new MemberController(this);

        privated = (ImageButton) findViewById(R.id.privated);
        share = (ImageButton) findViewById(R.id.share);

        /* 관련된 페이지 */
        Intent loginPage = new Intent(this, Login.class);
        Intent albumMainPage = new Intent(this, AlbumMain.class);
        Intent groupMainPage = new Intent(this, GroupMain.class);

        /* 초기 설정 */
        mc.isloginMember(new Callback<MemberDTO>() {
            @Override
            public void onResponse(Call<MemberDTO> call, Response<MemberDTO> response) {
                if (response.isSuccessful()) {
                    loginMember = response.body();
                } else {
                    /* Toast 메시지 */
                }
            }

            @Override
            public void onFailure(Call<MemberDTO> call, Throwable t) {
                /* Toast 메시지 */
            }
        });


        /* 개인 */
        privated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                albumMainPage.putExtra("isPrivate", true);
                if (loginMember != null) {
                    albumMainPage.putExtra("loginMember", loginMember);
                }
                startActivity(albumMainPage);
            }
        });

        /* 공유 */
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginMember != null) {
                    groupMainPage.putExtra("loginMember", loginMember);
                    startActivity(groupMainPage);
                } else {
                    startActivity(loginPage);
                }
            }
        });
    }
}
