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
import com.example.miniinstagram.UI_Activity.HomepageActivity;
import com.example.miniinstagram.model.Group;
import com.example.miniinstagram.model.GroupAdapterCode;
import com.example.miniinstagram.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {
    private Context mContext;
    private List<Group> mGroups;
    private GroupAdapterCode code;
    private String TAG = "CommentAdapter: ";
    private String databaseGroups = "Groups";
    private String databaseUserGroups = "User-groups";
    private DatabaseReference databaseRef;

    public GroupAdapter(Context mContext, List<Group> mGroups, GroupAdapterCode code) {
        this.mGroups = mGroups;
        this.mContext = mContext;
        this.code = code;
        databaseRef = FirebaseDatabase.getInstance().getReference();
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
        }

        showGroupDetail(holder, group.getGroupID());
    }

    @Override
    public int getItemCount() {
        return mGroups.size();
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
