package com.example.miniinstagram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniinstagram.R;
import com.example.miniinstagram.UI_Activity.CommentActivity;
import com.example.miniinstagram.UI_Activity.HomepageActivity;
import com.example.miniinstagram.UI_Activity.UserListActivity;
import com.example.miniinstagram.model.Notification;
import com.example.miniinstagram.model.NotificationType;
import com.example.miniinstagram.model.Post;
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
import com.hendraanggrian.appcompat.socialview.widget.SocialTextView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context mContext;
    private List<Post> mPosts;
    private FirebaseUser fbUser;

    private DatabaseReference databaseRef;

    private String TAG = "PostAdapter: ";
    private String databaseUsers = "Users";
    private String databasePosts = "Posts";
    private String databaseNotifications = "Notifications";
    private String databaseLikes = "Likes";
    private String databasePostComments = "Post-comments";

    private ValueEventListener getAuthorInfoListener;

    public PostAdapter(Context mContext, List<Post> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;

        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Post post = mPosts.get(position);

        getPostContent(post, holder);
        // Show author info of the post to user, including profile image, username and name
        getAuthorInfo(holder, post.getAuthorID());

        isPostLikedByUser(post.getPostID(), holder);
        toLikeOrNot(holder, post.getPostID(), post.getAuthorID());

        getLikesCount(post.getPostID(), holder);

        getCommentsNum(post.getPostID(), holder);
        addAndViewComments(holder, post.getPostID(), post.getAuthorID());

        goToUserProfile(holder, post.getAuthorID());
        getAllLikesUsers(holder, post.getPostID());

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    private void getAllLikesUsers(@NonNull ViewHolder holder, String postID) {
        holder.likesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, UserListActivity.class);
                intent.putExtra("userListTitle", "Likes");
                intent.putExtra("postID", postID);
                mContext.startActivity(intent);
            }
        });
    }

    /**
     * When click on the username or avatar of a post, go to that person's profile page
     * @param holder
     * @param userID
     */
    private void goToUserProfile(@NonNull PostAdapter.ViewHolder holder, String userID) {
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

        holder.nameTextView.setOnClickListener(new View.OnClickListener() {
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

    /**
     * When user click on Comment ImageView or number of comments of a post, jump to Comment page
     * @param holder
     * @param postID
     * @param authorID
     */
    private void addAndViewComments(@NonNull ViewHolder holder, String postID, String authorID) {
        holder.commentImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(mContext,"Open Comments page and add comments", Toast.LENGTH_SHORT).show();
                goToCommentPage(postID, authorID);
            }
        });

        holder.commentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(mContext,"Open Comments page", Toast.LENGTH_SHORT).show();
                goToCommentPage(postID, authorID);
            }
        });
    }

    // Open Comment page to check comments and add comments.
    private void goToCommentPage(String postID, String authorID) {
        /**
         * Use putExtra() and getStringExtra() to pass String values from this
         * activity (mContext) to another activity (CommentActivity).
         */
        Intent intent = new Intent(mContext, CommentActivity.class);
//        Intent intent = new Intent(mContext, NewPostActivity.class);
        intent.putExtra("postID", postID);
        intent.putExtra("authorID", authorID);
        mContext.startActivity(intent);
    }

    /**
     * Show how many comments the post has.
     *
     * @param postID
     * @param holder
     */
    private void getCommentsNum(String postID, @NonNull ViewHolder holder) {
        DatabaseReference ref = databaseRef.child(databasePostComments).child(postID);

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long commentsNum = snapshot.getChildrenCount();
                holder.commentTextView.setText("View all " + commentsNum + " comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Can't get number of comments of the post", error.toException());
            }
        };

        ref.addListenerForSingleValueEvent(listener);
    }

    /**
     * When user click on the like-shape imageview, she likes / unlikes the post. And the icons
     * will be adjusted accordingly.
     *
     * @param holder
     * @param postID
     * @param authorID, id of the author of the post
     */
    private void toLikeOrNot(@NonNull ViewHolder holder, String postID, String authorID) {
        holder.likesImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.likesImageView.getTag().equals("like")) {
                    databaseRef.child(databaseLikes)
                                     .child(postID)
                                     .child(fbUser.getUid())
                                     .setValue(true);

                    holder.likesImageView.setTag("liked");
                    holder.likesImageView.setImageResource(R.drawable.ic_liked);
                    getLikesCount(postID, holder);
                    sendNotifications(authorID, postID);
                } else {
                    databaseRef.child(databaseLikes)
                                     .child(postID)
                                     .child(fbUser.getUid())
                                     .removeValue();

                    holder.likesImageView.setTag("like");
                    holder.likesImageView.setImageResource(R.drawable.ic_like);
                    getLikesCount(postID, holder);
                }
            }
        });
    }

    /**
     * Upload notification to database.
     *
     * @param userID, to whom the notification will be send
     * @param postID, id of the post that is commented
     */
    private void sendNotifications(String userID, String postID) {
        DatabaseReference ref = databaseRef.child(databaseNotifications).child(userID);
        String notificationID = ref.push().getKey();

        Notification notification = new Notification(notificationID, fbUser.getUid(), NotificationType.NOTIFICATION_TYPE_LIKES);
        notification.setPostID(postID);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(notificationID, notification.toMap());

        OnCompleteListener<Void> listener = new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.w(TAG, "onComplete: upload notification success");
                    return;
                }

                DatabaseException e = (DatabaseException) task.getException();
                Log.e(TAG, "KX: can't upload notifications" + e.getMessage().toString());
            }
        };

        ref.updateChildren(childUpdates).addOnCompleteListener(listener);
    }

    /**
     * Get post image and description and show them accordingly.
     *
     * @param post
     * @param holder
     */
    private void getPostContent(Post post, @NonNull ViewHolder holder) {
        // Show image of the post to user
        Picasso.get()
               .load(post.getPostImageUrl())
               .placeholder(R.drawable.ic_add_photo_png)
               .into(holder.postImageImageView);

        if (post.getDescription() == null || post.getDescription().length() == 0) {
            holder.descriptionTextView.setVisibility(View.GONE);
        } else {
            holder.descriptionTextView.setVisibility(View.VISIBLE);
            holder.descriptionTextView.setText(post.getDescription());
        }
    }

    /**
     * Count how many likes the post had and show on the text accordingly.
     *
     * @param postID
     * @param holder
     */
    private void getLikesCount(String postID, @NonNull ViewHolder holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                                                .getReference()
                                                .child(databaseLikes)
                                                .child(postID);

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long likesNum = snapshot.getChildrenCount();
                if (likesNum != 0) {
                    holder.likesTextView.setText(likesNum + " likes");
                } else {
                    holder.likesTextView.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Can't get likes of the post", error.toException());
            }
        };

        ref.addListenerForSingleValueEvent(listener);
    }

    /**
     * Check if the post is liked by the current user, and adjust the icon and text accordingly.
     *
     * @param postID
     * @param holder
     */
    private void isPostLikedByUser(String postID, @NonNull ViewHolder holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                                                .getReference()
                                                .child(databaseLikes)
                                                .child(postID);

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(fbUser.getUid()).exists()) {
                    holder.likesImageView.setImageResource(R.drawable.ic_liked);
                    holder.likesImageView.setTag("liked");
                } else {
                    holder.likesImageView.setImageResource(R.drawable.ic_like);
                    holder.likesImageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Can't get likes of the post", error.toException());
            }
        };

        ref.addListenerForSingleValueEvent(listener);
    }

    /**
     * Retrieve the author info of the post and show them accordingly
     * the author info included profile image, username and name.
     * @param holder
     * @param userID
     */
    private void getAuthorInfo(@NonNull ViewHolder holder, String userID) {
        DatabaseReference ref = databaseRef.child(databaseUsers).child(userID);

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
                holder.nameTextView.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i(TAG, "Can't get author info of the post");
            }
        };

        ref.addListenerForSingleValueEvent(listener);
        // ref.addValueEventListener(listener);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView profileImageImageView;
        public ImageView postImageImageView;
        public ImageView likesImageView;
        public ImageView commentImageView;
        public ImageView moreImageView;
        public TextView usernameTextView;
        public TextView likesTextView;
        public TextView nameTextView;
        public SocialTextView descriptionTextView;
        public TextView commentTextView;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImageImageView = itemView.findViewById(R.id.profile_image);
            postImageImageView = itemView.findViewById(R.id.post_image);
            likesImageView = itemView.findViewById(R.id.likes);
            commentImageView = itemView.findViewById(R.id.comment);
            moreImageView = itemView.findViewById(R.id.more_options);

            usernameTextView = itemView.findViewById(R.id.username);
            likesTextView = itemView.findViewById(R.id.num_likes);
            nameTextView = itemView.findViewById(R.id.author);
            descriptionTextView = itemView.findViewById(R.id.description);
            commentTextView = itemView.findViewById(R.id.comments_text);
        }
    }
}
