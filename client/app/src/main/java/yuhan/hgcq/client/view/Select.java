package yuhan.hgcq.client.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import yuhan.hgcq.client.R;

public class Select extends AppCompatActivity {

    /* View */
    ImageButton privated, share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setTitle("Recoder");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /* View와 Layout 연결 */
        privated = (ImageButton) findViewById(R.id.privated);
        share = (ImageButton) findViewById(R.id.share);

        /* 관련된 페이지 */
        Intent loginPage = new Intent(this, Login.class);
        Intent albumMainPage = new Intent(this, AlbumMain.class);

        /* 개인 버튼 눌림 */
        privated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                albumMainPage.putExtra("isPrivate", true);
                startActivity(albumMainPage);
            }
        });

        /* 공유 버튼 눌림 */
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(loginPage);
            }
        });
    }
}
