package demo_chat.anony1412.itptit.demochat;

import android.content.Intent;
import android.net.Uri;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

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

    private static final int GALLERY_PICK = 1;

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
        btn_settingChangeImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_settings_changeStatus : {

                String status_value = txt_settingStatus.getText().toString();
                Intent statusIntent = new Intent(getApplicationContext(), StatusActivity.class);
                statusIntent.putExtra("status_value", status_value);
                startActivity(statusIntent);
                break;
                
            }
            case R.id.btn_settings_changeImage : {

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
                /*
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);
                */

                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri).setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
                
            }
        }
    }

}
