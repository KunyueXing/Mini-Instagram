package com.example.miniinstagram.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniinstagram.R;
import com.example.miniinstagram.model.Group;
import com.example.miniinstagram.model.User;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {
    private Context mContext;
    private List<Group> mGroups;

    public GroupAdapter(Context mContext, List<Group> mGroups) {
        this.mGroups = mGroups;
        this.mContext = mContext;
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
    }

    @Override
    public int getItemCount() {
        return mGroups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView groupNameTextView;
        public TextView groupMemberNumTextView;
        public ImageView deleteImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            groupMemberNumTextView = itemView.findViewById(R.id.groupMemberNumTextView);
            groupNameTextView = itemView.findViewById(R.id.groupNameTextView);
            deleteImageView = itemView.findViewById(R.id.deleteGroupImageView);
        }
    }
}
