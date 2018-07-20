package demo_chat.anony1412.itptit.demochat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    // Android Layout
    private Toolbar mToolbar;
    private RecyclerView rv_usersList;

    // FireBase
    private DatabaseReference mUsersDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        Setup();
    }

    private void Setup() {

        // Toolbar
        mToolbar = findViewById(R.id.users_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // FireBase
        mUsersDataBase = FirebaseDatabase.getInstance().getReference().child("Users");

        // RecyclerView
        rv_usersList = findViewById(R.id.rv_usersList);
        rv_usersList.setHasFixedSize(true);
        rv_usersList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();

        // FireBase mới đã thay đổi phương thức khởi tạo FireBaseRecyclerAdapter, đọc dữ liệu bằng phương thức mới:
        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(mUsersDataBase, Users.class)
                        .build();

        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options) {

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_single_layout, parent, false);


                return new UsersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull Users model) {
                holder.setDisplayName(model.getName());
                holder.setStatus(model.getStatus());
                holder.setImage(model.getThumb_image(), getApplicationContext());

                final String userID = getRef(position).getKey();
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent profileIntent = new Intent(UsersActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("userID", userID);
                        startActivity(profileIntent);
                    }
                });
            }
        };
        rv_usersList.setAdapter(firebaseRecyclerAdapter);

        // dòng này giải quyết tất cả vấn đề :v
        // lắng nghe sự thay đổi của các sự kiện, chắc là vậy ?!?
        firebaseRecyclerAdapter.startListening();
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setDisplayName(String name) {

            TextView userNameView = mView.findViewById(R.id.user_single_DisplayName);
            userNameView.setText(name);
        }

        public void setStatus(String status) {

            TextView userStatusView = mView.findViewById(R.id.user_single_Status);
            userStatusView.setText(status);
        }

        public void setImage(String thumb_image, Context context) {

            CircleImageView userImageView = mView.findViewById(R.id.user_single_image);
            Picasso.get().load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);
        }
    }
}
