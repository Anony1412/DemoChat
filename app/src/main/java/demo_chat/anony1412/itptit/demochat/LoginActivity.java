package demo_chat.anony1412.itptit.demochat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Toolbar mToolbar;
    private TextInputEditText tedt_log_email;
    private TextInputEditText tedt_log_password;
    private Button btn_mLogin;
    private ProgressDialog mProgress;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        Maps();
        SetOnClicked();
    }

    private void SetOnClicked() {
        btn_mLogin.setOnClickListener(this);
    }

    private void Maps() {
        btn_mLogin = findViewById(R.id.btn_login);
        tedt_log_email = findViewById(R.id.login_email);
        tedt_log_password = findViewById(R.id.login_password);

        mToolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Login User");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgress = new ProgressDialog(this);
    }

    @Override
    public void onClick(View v) {
        String email = tedt_log_email.getText().toString();
        String password = tedt_log_password.getText().toString();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            mProgress.setTitle("Sign In User");
            mProgress.setMessage("Please wait while we check your credentials!");
            mProgress.setCanceledOnTouchOutside(false);
            mProgress.show();

            login_user(email, password);
        }
    }

    private void login_user(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mProgress.dismiss();

                    Intent main_Intent = new Intent(LoginActivity.this, MainActivity.class);
                    main_Intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(main_Intent);
                } else {
                    mProgress.hide();
                    Toast.makeText(LoginActivity.this, "Dang nhap that bai!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
