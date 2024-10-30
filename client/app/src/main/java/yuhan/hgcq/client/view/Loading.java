package yuhan.hgcq.client.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import yuhan.hgcq.client.R;

public class Loading extends AppCompatActivity {
    /* Setting */
    private static final int LOADING_TIME = 3000; // 3초

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar(); // 액션바 가져오기
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
        setContentView(R.layout.activity_loading);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Glide.with(this)
                .asGif()
                .load(R.drawable.loading)
                .into((ImageView) findViewById(R.id.loading));

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(Loading.this, Select.class);
            startActivity(intent);
            finish();
        }, LOADING_TIME);
    }
}
