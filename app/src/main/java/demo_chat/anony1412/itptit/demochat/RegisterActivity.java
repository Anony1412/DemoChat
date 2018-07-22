package demo_chat.anony1412.itptit.demochat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private Button btn_mCreate;
    private TextInputEditText tedt_mDisplayName;
    private TextInputEditText tedt_mEmail;
    private TextInputEditText tedt_mPassWord;

    private Toolbar mToolbar;

    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        Maps();
        SettingOnClicked();
    }

    private void Maps() {
        btn_mCreate = findViewById(R.id.btn_reg_create);
        tedt_mDisplayName = findViewById(R.id.reg_display_name);
        tedt_mEmail = findViewById(R.id.reg_email);
        tedt_mPassWord = findViewById(R.id.reg_password);

        mToolbar = findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgress = new ProgressDialog(this);
    }

    private void SettingOnClicked() {
        btn_mCreate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
//        Toast.makeText(this, "Clicked on Create Account!", Toast.LENGTH_SHORT).show();
        String display_name = tedt_mDisplayName.getText().toString();
        String email = tedt_mEmail.getText().toString();
        String password = tedt_mPassWord.getText().toString();

        if (!TextUtils.isEmpty(display_name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            mProgress.setTitle("Sign Up Your Account");
            mProgress.setMessage("Please wait while we create your account!");
            mProgress.setCanceledOnTouchOutside(false);
            mProgress.show();
            register_user(display_name, email, password);
        }
    }

    private void register_user(final String display_name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("name", display_name);
                    userMap.put("status", "default");
                    userMap.put("image", "default");
                    userMap.put("thumb_image", "default");

                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mProgress.dismiss();
                                Intent main_Intent = new Intent(RegisterActivity.this, MainActivity.class);
                                main_Intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(main_Intent);
                                finish();
                            }
                        }
                    });

                } else {
                    mProgress.hide();
                    Toast.makeText(RegisterActivity.this, "Đăng ký tài khoản thất bại!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
