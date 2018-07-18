package demo_chat.anony1412.itptit.demochat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    // Android Layout
    private ImageView mProfileImage;
    private TextView mProfileDisplayName, mProfileStatus, mProfileTotalFriends;
    private Button mProfileSendFriendsReqBtn;
    private Button mProfileDeclineFriendBtn;

    private String mCurrent_state;
    private String userID;

    // FireBase
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mFriendReqDatabase;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mNotificationDatabase;

    // ProgressDialog
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Android
        userID  = getIntent().getStringExtra("userID");

        // FireBase
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");

        // Android
        Setup();

        //
        mCurrent_state = "not_friend";
    }

    private void Setup() {

        Maps();
        setDialog();
        setFirebase();
        setOnClicked();

    }

    private void Maps() {
        // Android Layout
        mProfileImage = findViewById(R.id.profile_Image);

        mProfileDisplayName = findViewById(R.id.profile_DisplayName);

        mProfileStatus = findViewById(R.id.profile_Status);

        mProfileTotalFriends = findViewById(R.id.profile_totalFriends);

        mProfileSendFriendsReqBtn = findViewById(R.id.profile_sendFriendReqBtn);

        mProfileDeclineFriendBtn = findViewById(R.id.profile_declineFriendReqBtn);
        mProfileDeclineFriendBtn.setVisibility(View.INVISIBLE);
        mProfileDeclineFriendBtn.setEnabled(false);
    }

    private void setDialog() {
        // ProgressDialog
        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Loading User Profile");
        mProgress.setMessage("Please wait while loading user profile!");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();
    }

    private void setFirebase() {

        // FireBase
        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                // Android
                mProfileDisplayName.setText(name);
                mProfileStatus.setText(status);
                Picasso.get().load(image).placeholder(R.drawable.default_avatar).into(mProfileImage);

                // ----- FRIEND LIST / REQUEST FEATURE -----
                mFriendReqDatabase.child(mCurrentUser.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(userID)) {
                            String req_type = dataSnapshot.child(userID).child("request_type").getValue().toString();
                            if (req_type.equals("received")) {

                                mCurrent_state = "req_received";
                                mProfileSendFriendsReqBtn.setText("Accept Friend Request");

                                mProfileDeclineFriendBtn.setVisibility(View.VISIBLE);
                                mProfileDeclineFriendBtn.setEnabled(true);

                            } else if (req_type.equals("sent")) {

                                mCurrent_state = "req_sent";
                                mProfileSendFriendsReqBtn.setText("Cancel Friend Request");


                                mProfileDeclineFriendBtn.setVisibility(View.INVISIBLE);
                                mProfileDeclineFriendBtn.setEnabled(false);
                            }

                            // ProgressDialog
                            mProgress.dismiss();
                        } else {
                            mFriendDatabase.child(mCurrentUser.getUid())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.hasChild(userID)) {

                                        mCurrent_state = "friends";
//                                        mProfileSendFriendsReqBtn.setText("Unfriend this person");
                                        mProfileSendFriendsReqBtn.setText("Unfriend " + mProfileDisplayName.getText());

                                        mProfileDeclineFriendBtn.setVisibility(View.INVISIBLE);
                                        mProfileDeclineFriendBtn.setEnabled(false);

                                    }

                                    // ProgressDialog
                                    mProgress.dismiss();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                    // ProgressDialog
                                    mProgress.dismiss();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setOnClicked() {
        mProfileSendFriendsReqBtn.setOnClickListener(this);
        mProfileDeclineFriendBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.profile_sendFriendReqBtn : {

                mProfileSendFriendsReqBtn.setEnabled(false);

                // ------------ NOT FRIEND STATE | SEND FRIEND REQUEST -----------
                if (mCurrent_state.equals("not_friend")) {
                    mFriendReqDatabase.child(mCurrentUser.getUid()).child(userID).child("request_type")
                            .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                mFriendReqDatabase.child(userID).child(mCurrentUser.getUid()).child("request_type")
                                        .setValue("received")
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @SuppressLint("ResourceAsColor")
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        HashMap<String, String> notificationData = new HashMap<>();
                                        notificationData.put("from", mCurrentUser.getUid());
                                        notificationData.put("type", "request");

                                        mNotificationDatabase.child(userID).push().setValue(notificationData)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                mCurrent_state = "req_sent";
                                                mProfileSendFriendsReqBtn.setText("Cancel Friend Request");

                                                mProfileDeclineFriendBtn.setVisibility(View.INVISIBLE);
                                                mProfileDeclineFriendBtn.setEnabled(false);

                                            }
                                        });

                                    }
                                });
                            } else {

                                Toast.makeText(ProfileActivity.this,
                                        "Failed Sending Request.",
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }

                            mProfileSendFriendsReqBtn.setEnabled(true);
                        }
                    });
                }

                // ------------ CANCEL FRIEND STATE -----------
                if (mCurrent_state.equals("req_sent")) {
                    mFriendReqDatabase.child(mCurrentUser.getUid()).child(userID).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendReqDatabase.child(userID).child(mCurrentUser.getUid()).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {

                                @SuppressLint("ResourceAsColor")
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mProfileSendFriendsReqBtn.setEnabled(true);
                                    mCurrent_state = "not_friend";
                                    mProfileSendFriendsReqBtn.setText("Send Friend Request");

                                    mProfileDeclineFriendBtn.setVisibility(View.INVISIBLE);
                                    mProfileDeclineFriendBtn.setEnabled(false);

                                }
                            });
                        }
                    });
                }

                // ------------- REQ RECEIVED STATE -----------
                if (mCurrent_state.equals("req_received")) {

                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                    mFriendDatabase.child(mCurrentUser.getUid()).child(userID).setValue(currentDate)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendDatabase.child(userID).child(mCurrentUser.getUid()).setValue(currentDate)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            mFriendReqDatabase.child(mCurrentUser.getUid()).child(userID)
                                                    .removeValue()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            mFriendReqDatabase.child(userID)
                                                                    .child(mCurrentUser.getUid()).removeValue()
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {

                                                                        @SuppressLint("ResourceAsColor")
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {

                                                                            mProfileSendFriendsReqBtn.setEnabled(true);
                                                                            mCurrent_state = "friends";
                                                                            mProfileSendFriendsReqBtn.setText("Unfriend " + mProfileDisplayName.getText());

                                                                            mProfileDeclineFriendBtn.setVisibility(View.INVISIBLE);
                                                                            mProfileDeclineFriendBtn.setEnabled(false);
                                                                        }
                                                                    });
                                                        }
                                                    });
                                        }
                                    });
                        }
                    });
                }

                break;
            }
            case R.id.profile_declineFriendReqBtn : {

                break;
            }
        }
    }
}
