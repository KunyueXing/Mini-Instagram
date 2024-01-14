package com.example.miniinstagram.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniinstagram.R;
import com.example.miniinstagram.UI_Activity.HomepageActivity;
import com.example.miniinstagram.UI_Activity.MainActivity;
import com.example.miniinstagram.model.Comment;
import com.example.miniinstagram.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.protobuf.Value;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.units.qual.A;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context mContext;
    private List<Comment> mComment;
    private String postID;
    private FirebaseUser fbUser;
    private DatabaseReference dbReference;
    private String databaseUsers;
    private String TAG = "CommentAdapter: ";
    private String databasePostComments = "Post-comments";
    private String databaseComments = "Comments";

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

        goToUserProfile(holder, comment.getAuthorID());

        if (!comment.getAuthorID().equals(fbUser.getUid())) {
            holder.deleteCommentImageView.setVisibility(View.INVISIBLE);
        } else {
            // The delete option only appears when the comment is posted by the current user
            holder.deleteCommentImageView.setVisibility(View.VISIBLE);

            deleteComment(holder, comment.getCommentID());
        }
    }

    @Override
    public int getItemCount() {
        return mComment.size();
    }

    /**
     * When user click on the delete imageview, check if user wants to delete the comment. If yes,
     * delete the comment.
     * @param holder
     * @param commentID
     */
    private void deleteComment(@NonNull ViewHolder holder, String commentID) {
        holder.deleteCommentImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle("Do you want to delete?");

                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                OnCompleteListener<Void> listener = new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        DatabaseException e = (DatabaseException) task.getException();
                                        Log.e(TAG, "KX: failed deleting comment-" + e.getMessage().toString());
                                    }
                                };

                                // To delete a children from multiple places in a single API call
                                Map<String, Object> childUpdates = new HashMap<String, Object>();
                                childUpdates.put("/" + databaseComments + "/" + commentID, null);
                                childUpdates.put("/" + databasePostComments + "/" + postID + "/" + commentID, null);

                                dbReference.updateChildren(childUpdates)
                                        .addOnCompleteListener(listener);
                                dialogInterface.dismiss();
                            }
                        });

                alertDialog.show();
            }
        });
    }

    /**
     * When click on the username or avatar of a comment, go to that person's profile page
     * @param holder
     * @param userID
     */
    private void goToUserProfile(@NonNull ViewHolder holder, String userID) {
        holder.usernameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openProfilePage(userID);
            }
        });

        holder.profileImageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openProfilePage(userID);
            }
        });
    }

    private void openProfilePage(String userID) {
        Intent intent = new Intent(mContext, HomepageActivity.class);
        intent.putExtra("profileUserID", userID);
        mContext.startActivity(intent);
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
        public ImageView deleteCommentImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImageImageView = itemView.findViewById(R.id.profileImageImageView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
            deleteCommentImageView = itemView.findViewById(R.id.deleteCommentImageView);
        }
    }
}
