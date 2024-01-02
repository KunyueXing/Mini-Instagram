package com.example.miniinstagram.Adapter;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniinstagram.R;
import com.example.miniinstagram.model.Post;
import com.example.miniinstagram.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.socialview.widget.SocialTextView;
import com.squareup.picasso.Picasso;

import java.util.EventListener;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context mContext;
    private List<Post> mPosts;
    private FirebaseUser firebaseUser;

    private DatabaseReference databaseReference;

    private String TAG = "PostAdapter: ";
    private String databaseUsers = "Users";
    private String databasePosts = "Posts";
    private String databaseLikes = "Likes";
    private String databasePostComments = "Post-comments";

    private ValueEventListener getAuthorInfoListener;

    public PostAdapter(Context mContext, List<Post> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        final Post post = mPosts.get(position);

        getPostContent(post, holder);
        // Show author info of the post to user, including profile image, username and name
        getAuthorInfo(holder, post.getAuthorID());

//        isPostLikedByUser(post.getPostID(), holder);
//        getLikesCount(post.getPostID(), holder);
//        getCommentsNum(post.getPostID(), holder);

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    /**
     * Show how many comments the post has.
     *
     * @param postID
     * @param holder
     */
//    private void getCommentsNum(String postID, @NonNull ViewHolder holder) {
//        DatabaseReference ref = databaseReference.child(databasePostComments).child(postID);
//
//        ValueEventListener listener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                long commentsNum = snapshot.getChildrenCount();
//                holder.commentTextView.setText("View all " + commentsNum + " comments");
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.i(TAG, "Can't get number of comments of the post from Post-comments");
//            }
//        };
//
//        ref.addValueEventListener(listener);
//    }

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
//    private void getLikesCount(String postID, @NonNull ViewHolder holder) {
//        DatabaseReference ref = FirebaseDatabase.getInstance()
//                                                .getReference()
//                                                .child(databaseLikes)
//                                                .child(postID);
//
//        ValueEventListener listener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                long likesNum = snapshot.getChildrenCount();
//                holder.likesTextView.setText(likesNum + "likes");
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.i(TAG, "Can't get likes of the post");
//            }
//        };
//
//        ref.addValueEventListener(listener);
//    }

    /**
     * Check if the post is liked by the current user, and adjust the icon and text accordingly.
     *
     * @param postID
     * @param holder
     */
//    private void isPostLikedByUser(String postID, @NonNull ViewHolder holder) {
//        DatabaseReference ref = FirebaseDatabase.getInstance()
//                                                .getReference()
//                                                .child(databaseLikes)
//                                                .child(postID);
//
//        ValueEventListener listener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.child(firebaseUser.getUid()).exists()) {
//                    holder.likesImageView.setImageResource(R.drawable.ic_liked);
//                    holder.likesImageView.setTag("liked");
//                } else {
//                    holder.likesImageView.setImageResource(R.drawable.ic_like);
//                    holder.likesImageView.setTag("liked");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.i(TAG, "Can't get likes of the post");
//            }
//        };
//
//        ref.addValueEventListener(listener);
//    }

    /**
     * Retrieve the author info of the post and show them accordingly
     * the author info included profile image, username and name.
     * @param holder
     * @param userID
     */
    private void getAuthorInfo(@NonNull ViewHolder holder, String userID) {
        DatabaseReference ref = databaseReference.child(databaseUsers).child(userID);

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
                holder.authorTextView.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i(TAG, "Can't get author info of the post");
            }
        };

        ref.addValueEventListener(listener);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView profileImageImageView;
        public ImageView postImageImageView;
        public ImageView likesImageView;
        public ImageView commentImageView;
        public ImageView moreImageView;
        public TextView usernameTextView;
        public TextView likesTextView;
        public TextView authorTextView;
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
            authorTextView = itemView.findViewById(R.id.author);
            descriptionTextView = itemView.findViewById(R.id.description);
            commentTextView = itemView.findViewById(R.id.comments_text);
        }
    }
}