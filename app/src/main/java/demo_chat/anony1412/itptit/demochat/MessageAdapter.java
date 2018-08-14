package demo_chat.anony1412.itptit.demochat;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private List<Messages> mMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference mRootRef;

    public MessageAdapter(List<Messages> mMessagesList) {
        this.mMessagesList = mMessagesList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout, parent,false);

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        String current_user_id = null;
        try {

            current_user_id = mAuth.getCurrentUser().getUid();

        } catch (NullPointerException e) {
        }


        Messages c = mMessagesList.get(position);

        String from_user = c.getFrom();

        // Set avatar for user
        mRootRef.child("Users").child(from_user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
                holder.setImage(thumb_image);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Set background for message frame
        if (from_user.equals(current_user_id)) {

            holder.messageText.setBackgroundResource(R.drawable.message_text_background);
            holder.messageText.setTextColor(Color.WHITE);

        } else {

            holder.messageText.setBackgroundResource(R.drawable.user_chat_message_text_background);
            holder.messageText.setTextColor(Color.BLACK);

        }

        holder.messageText.setText(c.getMessage());

    }

    @Override
    public int getItemCount() {
        return mMessagesList.size();
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public CircleImageView profileImage;
        public MessageViewHolder(View view) {
            super(view);

            messageText = view.findViewById(R.id.message_text_layout);
            profileImage = view.findViewById(R.id.message_profile_layout);
        }

        public void setImage(String thumb_image) {
            Picasso.get().load(thumb_image).placeholder(R.drawable.avatar_56px).resize(56, 56).into(profileImage);
        }
    }
}
