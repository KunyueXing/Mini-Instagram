package com.example.miniinstagram.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniinstagram.R;
import com.example.miniinstagram.model.Comment;
import com.example.miniinstagram.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.protobuf.Value;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context mContext;
    private List<Comment> mComment;
    private String postID;
    private FirebaseUser fbUser;
    private DatabaseReference dbReference;
    private String databaseUsers;
    private String TAG = "CommentAdapter: ";

    public CommentAdapter(Context mContext, List<Comment> mComment, String postID) {
        this.mContext = mContext;
        this.mComment = mComment;
        this.postID = postID;

        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        dbReference = FirebaseDatabase.getInstance().getReference();
        databaseUsers = "Users";
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                                  .inflate(R.layout.comment_item , parent , false);

        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Comment comment = mComment.get(position);

        holder.commentTextView.setText(comment.getContent());
        getAuthorInfo(holder, comment.getAuthorID());
    }

    @Override
    public int getItemCount() {
        return mComment.size();
    }

    private void getAuthorInfo(@NonNull ViewHolder holder, String authorID) {
        DatabaseReference ref = dbReference.child(databaseUsers).child(authorID);

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if (user.getProfilePicUriStr().equals("default")) {
                    holder.profileImageImageView.setImageResource(R.drawable.default_avatar);
                } else {
                    Picasso.get()
                           .load(user.getProfilePicUriStr())
                           .placeholder(R.drawable.default_avatar)
                           .into(holder.profileImageImageView);
                }

                holder.usernameTextView.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Can't get author info of the post", error.toException());
            }
        };

        ref.addListenerForSingleValueEvent(listener);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView profileImageImageView;
        public TextView usernameTextView;
        public TextView commentTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImageImageView = itemView.findViewById(R.id.profileImageImageView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
        }
    }
}
