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
import com.example.miniinstagram.UI_Activity.GroupDetailActivity;
import com.example.miniinstagram.UI_Activity.GroupListActivity;
import com.example.miniinstagram.model.Group;
import com.example.miniinstagram.model.GroupAdapterCode;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {
    private Context mContext;
    private List<Group> mGroups;
    private GroupAdapterCode code;
    private String TAG = "GroupAdapter: ";
    private String databaseGroups = "Groups";
    private String databaseUserGroups = "User-groups";
    private DatabaseReference databaseRef;
    private FirebaseUser fbUser;
    private ValueEventListener isInGroupListener;
    private String userID;

    public GroupAdapter(Context mContext, List<Group> mGroups, GroupAdapterCode code) {
        this.mGroups = mGroups;
        this.mContext = mContext;
        this.code = code;
        databaseRef = FirebaseDatabase.getInstance().getReference();
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
    }
    public GroupAdapter(Context mContext, List<Group> mGroups, GroupAdapterCode code, String userID) {
        this.mGroups = mGroups;
        this.mContext = mContext;
        this.code = code;
        this.userID = userID;
        databaseRef = FirebaseDatabase.getInstance().getReference();
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                                  .inflate(R.layout.group_item, parent, false);

        return new GroupAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Group group = mGroups.get(position);
        holder.groupNameTextView.setText(group.getGroupName());
        int groupMemberNumber = 0;
        if (group.getMembers() != null) {
            groupMemberNumber = group.getMembers().size();
        }
        holder.groupMemberNumTextView.setText(groupMemberNumber + " members in this group");

        if (code == GroupAdapterCode.GROUP_ADAPTER_CODE_EDIT) {
            holder.deleteImageView.setVisibility(View.VISIBLE);
            deleteGroup(holder, group.getOwnerID(), group.getGroupID());
        }

        if (code == GroupAdapterCode.GROUP_ADAPTER_CODE_GENERAL) {
            holder.selectImageView.setVisibility(View.VISIBLE);
            holder.selectImageView.setTag("to select");
//            Log.i(TAG, "Initially: " + holder.selectImageView.getTag().toString());
            holder.selectImageView.setImageResource(R.drawable.circle);

            isInGroup(holder.selectImageView, group);
            selectGroup(holder, group);
        }

        showGroupDetail(holder, group.getGroupID());
    }

    @Override
    public int getItemCount() {
        return mGroups.size();
    }

    /**
     * Check if the user (@userID) is in this group. If she is, set the imageview as a star.
     * @param select
     * @param group
     */
    private void isInGroup(final ImageView select, final Group group) {
        String currUserID = fbUser.getUid();
        DatabaseReference ref = databaseRef.child(databaseUserGroups).child(currUserID).child(group.getGroupID());
//        Log.i(TAG, "check if the user is in the group");

        isInGroupListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("members").child(userID).exists()) {
                    select.setTag("selected");
//                    Log.i(TAG, "the user is in the group");
//                    Log.i(TAG, select.getTag().toString());
                    select.setImageResource(R.drawable.check_star);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed checking if user is in a certain group", error.toException());
            }
        };

        ref.addValueEventListener(isInGroupListener);
    }

    /**
     * When click on the @selectImageView, add / delete a user to / from a group accordingly.
     *
     * @param holder
     * @param group
     */
    private void selectGroup(@NonNull ViewHolder holder, Group group) {
        holder.selectImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.selectImageView.getTag().toString().equals("to select")) {
//                    holder.selectImageView.setTag("selected");
//                    holder.selectImageView.setImageResource(R.drawable.check_star);
                    updatedToDatabase(group, true);
                } else {
//                    holder.selectImageView.setTag("to select");
//                    holder.selectImageView.setImageResource(R.drawable.circle);
                    updatedToDatabase(group, null);
                }
            }
        });
    }

    /**
     * Added a user to a group when the @obj is true. Removing a user from a group when the
     * @obj is null. Updated to database.
     * @param group
     * @param obj
     */
    private void updatedToDatabase(Group group, Object obj) {
        group.setMembers(userID, obj);
        String groupID = group.getGroupID();

        Map<String, Object> childUpdates = new HashMap<String, Object>();
        childUpdates.put("/" + databaseGroups + "/" + groupID, group.toMap());
        childUpdates.put("/" + databaseUserGroups + "/" + fbUser.getUid() + "/" + groupID, group.toMap());

        OnCompleteListener<Void> listener = new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
//                    Toast.makeText(mContext, "updated user - group success", Toast.LENGTH_SHORT).show();

                    return;
                }

                DatabaseException e = (DatabaseException) task.getException();
                Log.e(TAG, "KX: failed adding to / deleting from the group" + e.getMessage().toString());
            }
        };

        databaseRef.updateChildren(childUpdates).addOnCompleteListener(listener);
    }

    private void showGroupDetail(@NonNull ViewHolder holder, String groupID) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, GroupDetailActivity.class);
                intent.putExtra("groupID", groupID);
                mContext.startActivity(intent);
            }
        });
    }

    private void deleteGroup(@NonNull ViewHolder holder, String ownerID, String groupID) {
        holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle("Do you want to delete this group?");

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
                                        Log.e(TAG, "KX: failed deleting group-" + e.getMessage().toString());
                                    }
                                };

                                // To delete a children from multiple places in a single API call
                                Map<String, Object> childUpdates = new HashMap<String, Object>();
                                childUpdates.put("/" + databaseGroups + "/" + groupID, null);
                                childUpdates.put("/" + databaseUserGroups + "/" + ownerID + "/" + groupID, null);

                                databaseRef.updateChildren(childUpdates)
                                           .addOnCompleteListener(listener);
                                dialogInterface.dismiss();
                            }
                        });

                alertDialog.show();
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView groupNameTextView;
        public TextView groupMemberNumTextView;
        public ImageView deleteImageView;
        public ImageView selectImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            groupMemberNumTextView = itemView.findViewById(R.id.groupMemberNumTextView);
            groupNameTextView = itemView.findViewById(R.id.groupNameTextView);
            deleteImageView = itemView.findViewById(R.id.deleteGroupImageView);
            selectImageView = itemView.findViewById(R.id.selectImageView);
        }
    }
}
