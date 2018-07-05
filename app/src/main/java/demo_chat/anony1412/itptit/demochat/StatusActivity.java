package demo_chat.anony1412.itptit.demochat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity implements View.OnClickListener{
    // Android Layout
    private TextInputLayout txt_mStatus;
    private Button btn_mSaveChange;
    private Toolbar mToolbar;

    //FireBase
    private DatabaseReference mDataBase;
    private FirebaseUser mCurrentUser;

    //Progress
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        Setup();
    }

    private void Setup() {
        Maps();
        SetOnClicked();
    }

    private void SetOnClicked() {
        btn_mSaveChange.setOnClickListener(this);
    }

    private void Maps() {
        // Android Layout
        txt_mStatus = findViewById(R.id.txt_status_ChangeStatus);
        String status_value = getIntent().getStringExtra("status_value");
        txt_mStatus.getEditText().setText(status_value);

        btn_mSaveChange = findViewById(R.id.btn_status_saveChange);

        // Toolbar
        mToolbar = findViewById(R.id.status_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //FireBase
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();
        mDataBase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_status_saveChange) {
            String status = txt_mStatus.getEditText().getText().toString();

            //Progress
            mProgress = new ProgressDialog(this);
            mProgress.setTitle("Change Status");
            mProgress.setMessage("Please wait while we save the change!");
            mProgress.setCanceledOnTouchOutside(false);
            mProgress.show();

            mDataBase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        
                        mProgress.dismiss();
                    
                    } else {

                        Toast.makeText(StatusActivity.this, "Cập nhật trạng thái mới thất bại!", Toast.LENGTH_SHORT).show();
                        
                    }
                }
            });
        }
    }
}
