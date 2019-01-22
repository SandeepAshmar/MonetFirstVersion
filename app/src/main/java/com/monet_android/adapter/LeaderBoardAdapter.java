package com.monet_android.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.monet_android.R;
import com.monet_android.modle.leaderboard.LeaderBoardLeaders;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class LeaderBoardAdapter extends RecyclerView.Adapter<LeaderBoardAdapter.ViewHolder> {

    private Context ctx;
    private ArrayList<LeaderBoardLeaders> boardLeaders;

    public LeaderBoardAdapter(Context ctx, ArrayList<LeaderBoardLeaders> boardLeaders) {
        this.ctx = ctx;
        this.boardLeaders = boardLeaders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_leaderboard, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LeaderBoardLeaders leaders = boardLeaders.get(position);

        int rankCount = Integer.parseInt(leaders.getRank());

        if(rankCount > 99){
            holder.tv_leadRank.setTextSize(10);
        }else{
            holder.tv_leadRank.setTextSize(14);
        }

        holder.tv_leadRank.setText(leaders.getRank());
        holder.tv_leadName.setText(leaders.getName());
        holder.tv_leadPoints.setText(leaders.getPoints()+" xp");

        if(leaders.getUser_profile_image() == null){
            holder.img_leadUser.setBackgroundResource(R.drawable.ic_user);
        }else{
            Glide.with(ctx).load(leaders.getUser_profile_image()).into(holder.img_leadUser);
        }

    }

    @Override
    public int getItemCount() {
        return boardLeaders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_leadRank, tv_leadName, tv_leadPoints;
        private CircleImageView img_leadUser;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_leadRank = itemView.findViewById(R.id.tv_leadRank);
            tv_leadName = itemView.findViewById(R.id.tv_leadName);
            tv_leadPoints = itemView.findViewById(R.id.tv_leadPoints);
            img_leadUser = itemView.findViewById(R.id.img_leadUser);

        }
    }
}
