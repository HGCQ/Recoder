package yuhan.hgcq.client.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
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
        ActionBar actionBar = getSupportActionBar(); // actionBar 가져오기
        if (actionBar != null) {
            actionBar.setTitle("Recoder");
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c2dcff")));
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
