package demo_chat.anony1412.itptit.demochat;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    // Android layout
    private RecyclerView mRequestList;
    private View mMainView;

    // Android Variable
    private String mCurrent_userId;

    // FireBasep
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mRequestsDatabase;
    private FirebaseAuth mAuth;

    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_requests, container, false);

        mRequestList = mMainView.findViewById(R.id.request_lists);

        mAuth = FirebaseAuth.getInstance();

        mCurrent_userId = mAuth.getCurrentUser().getUid();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);

        mRequestsDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrent_userId);
        mRequestsDatabase.keepSynced(true);

        mRequestList.setHasFixedSize(true);
        mRequestList.setLayoutManager(new LinearLayoutManager(getContext()));

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Requests> options
                = new FirebaseRecyclerOptions.Builder<Requests>()
                .setQuery(mRequestsDatabase, Requests.class)
                .build();

        FirebaseRecyclerAdapter<Requests, RequestsViewHolder> adapter
                = new FirebaseRecyclerAdapter<Requests, RequestsViewHolder>(options) {


            @NonNull
            @Override
            public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_request_single_layout, parent, false);

                return new RequestsViewHolder(view);

            }

            @Override
            protected void onBindViewHolder(@NonNull final RequestsViewHolder holder, int position, @NonNull Requests model) {

                final String list_user_id = getRef(position).getKey();

                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String user_Name = dataSnapshot.child("name").getValue().toString();
                        String user_thumb = dataSnapshot.child("thumb_image").getValue().toString();

                        holder.setName(user_Name);
                        holder.setAvatar(user_thumb, getContext());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        };

        mRequestList.setAdapter(adapter);
        adapter.startListening();
    }

    private class RequestsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public RequestsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setName(String user_name) {
            TextView userName = mView.findViewById(R.id.requests_single_displayName);
            userName.setText(user_name);
        }

        public void setAvatar(String user_thumb, Context context) {
            CircleImageView userAvatar = mView.findViewById(R.id.requests_single_image);
            Picasso.get()
                    .load(user_thumb)
                    .placeholder(R.drawable.default_avatar)
                    .resize(64, 64)
                    .into(userAvatar);
        }
    }
}
