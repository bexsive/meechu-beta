package com.inertiamobility.meechu;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "UserRecyclerViewAdapter";

    private List<User> users;
    private Context mContext;

    public UserRecyclerViewAdapter(Context mContext, List<User> users) {
        this.users = users;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        // Example using glide for user profile pictures

        // .load(path)
        //Glide.with(mContext).asBitmap().load(events.get(position).getUserId()).into(holder.image);

        holder.userNameText.setText(users.get(position).getFirstName() + " " + users.get(position).getLastName());


        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Launch profile page
                Toast.makeText(mContext, users.get(position).getFirstName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        // Images
        CircleImageView image;
        TextView userNameText;
        RelativeLayout parentLayout;


        public ViewHolder(View itemView) {
            super(itemView);
            // Profile pictures
            image = itemView.findViewById(R.id.image);

            userNameText = itemView.findViewById(R.id.userNameText);

            parentLayout = itemView.findViewById(R.id.parent_layout);

        }
    }
}