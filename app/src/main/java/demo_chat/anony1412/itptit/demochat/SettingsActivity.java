package demo_chat.anony1412.itptit.demochat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    // Android layout
    private TextView txt_settingDisplayName;
    private TextView txt_settingStatus;
    private Button btn_settingChangeImage;
    private Button btn_settingChangeStatus;
    private CircleImageView img_settingDisplayImage;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Setup();
    }

    private void Maps() {
        txt_settingDisplayName = findViewById(R.id.txt_settings_displayName);
        txt_settingStatus = findViewById(R.id.txt_settings_status);
        btn_settingChangeImage = findViewById(R.id.btn_settings_changeImage);
        btn_settingChangeStatus = findViewById(R.id.btn_settings_changeStatus);
        img_settingDisplayImage = findViewById(R.id.img_setting_displayImage);

        mToolbar = findViewById(R.id.setting_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Setting Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void Setup() {
        Maps();
        SetOnClicked();

        //FireBase
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();

                txt_settingDisplayName.setText(name);
                txt_settingStatus.setText(status);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SetOnClicked() {
        btn_settingChangeStatus.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_settings_changeStatus : {
                Intent statusIntent = new Intent(getApplicationContext(), StatusActivity.class);
                startActivity(statusIntent);
                break;
            }
            case R.id.btn_settings_changeImage : {
                break;
            }
        }
    }
}
