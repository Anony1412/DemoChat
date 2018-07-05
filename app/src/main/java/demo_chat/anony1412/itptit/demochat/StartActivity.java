package demo_chat.anony1412.itptit.demochat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btn_mReg;
    private Button btn_mLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Maps();
        SetOnClicked();
    }

    private void SetOnClicked() {
        btn_mReg.setOnClickListener(this);
        btn_mLogIn.setOnClickListener(this);
    }

    private void Maps() {
        btn_mReg = findViewById(R.id.btn_start_register);
        btn_mLogIn = findViewById(R.id.btn_start_login);
    }

    @Override
    public void onClick(View v) {
        int idV = v.getId();
        switch (idV) {
            case R.id.btn_start_login: {
                Intent login_Intent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(login_Intent);
                break;
            }
            case R.id.btn_start_register: {
                Intent reg_Intent = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(reg_Intent);
                break;
            }
        }
    }
}
