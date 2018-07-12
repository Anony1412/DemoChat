package demo_chat.anony1412.itptit.demochat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    //FireBase
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    // Storage FireBase
    private StorageReference mImageStore;

    // Upload Task
    UploadTask uploadTask;

    // Android layout
    private TextView txt_settingDisplayName;
    private TextView txt_settingStatus;
    private Button btn_settingChangeImage;
    private Button btn_settingChangeStatus;
    private CircleImageView img_settingDisplayImage;

    //Toolbar
    private Toolbar mToolbar;

    //Progress Dialog
    private ProgressDialog mProgress;

    private static final int GALLERY_PICK = 1;
    private static final int MAX_LENGTH = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Storage FireBase
        mImageStore = FirebaseStorage.getInstance().getReference();

        Setup();
    }

    private void Maps() {
        //Android Layout
        txt_settingDisplayName = findViewById(R.id.txt_settings_displayName);
        txt_settingStatus = findViewById(R.id.txt_settings_status);
        btn_settingChangeImage = findViewById(R.id.btn_settings_changeImage);
        btn_settingChangeStatus = findViewById(R.id.btn_settings_changeStatus);
        img_settingDisplayImage = findViewById(R.id.img_setting_displayImage);

        //Toolbar
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
                String image = dataSnapshot.child("image").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                txt_settingDisplayName.setText(name);
                txt_settingStatus.setText(status);

                Picasso.get().load(image).into(img_settingDisplayImage);
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
            case R.id.btn_settings_changeStatus: {

                String status_value = txt_settingStatus.getText().toString();
                Intent statusIntent = new Intent(getApplicationContext(), StatusActivity.class);
                statusIntent.putExtra("status_value", status_value);
                startActivity(statusIntent);
                break;

            }
            case R.id.btn_settings_changeImage: {

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri).setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                mProgress = new ProgressDialog(this);
                mProgress.setTitle("Update Image");
                mProgress.setMessage("Please wait while updating image!");
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();

                String current_uid = mCurrentUser.getUid();

                final Uri resultUri = result.getUri();
                final StorageReference filePath = mImageStore.child("profile_images").child(current_uid + ".jpg");

                // đã getDownloadUrl() được bằng phương thức này ^^
                uploadTask = filePath.putFile(resultUri);
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();

                            mUserDatabase.child("image").setValue(String.valueOf(downloadUri)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mProgress.dismiss();
                                    Toast.makeText(getApplicationContext(), "Updating successfuly!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }
    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++) {
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

}

