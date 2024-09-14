package yuhan.hgcq.client.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import yuhan.hgcq.client.R;

public class Login extends AppCompatActivity {

    ImageButton login,join;
    EditText id,password;

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
        login=(ImageButton) findViewById(R.id.login);
        join=(ImageButton) findViewById(R.id.join);

        Intent goToGroupMain=new Intent(this, GroupMain.class);
        Intent goToJoin=new Intent(this, Join.class);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //조건문으로 db에 저장된 회원 정보id,password가  같은 지 비교하고 같다면 로그인 성공 그게 아니라면 로그인을 실패하게 만들기
                startActivity(goToGroupMain);
            }
        });
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(goToJoin);
            }
        });
    }
    public void onClick_setting_costume_save(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Recoder")
                .setMessage("로그인하시겠습니까?")
                .setIcon(R.drawable.album)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // 확인시 처리 로직
                        Toast.makeText(Login.this, "로그인하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(Login.this, "취소하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }
}
