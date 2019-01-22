package com.monet_android.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.monet_android.R;
import com.monet_android.modle.lastWatchVideoList.LastWatchVideoResponse;

import java.util.ArrayList;

public class LastWatchVideoAdapter extends RecyclerView.Adapter<LastWatchVideoAdapter.ViewHolder> {

    Context context;
    ArrayList<LastWatchVideoResponse> lastWatchVideoResponses;

    public LastWatchVideoAdapter(Context context, ArrayList<LastWatchVideoResponse> lastWatchVideoResponses) {
        this.context = context;
        this.lastWatchVideoResponses = lastWatchVideoResponses;
    }

    @NonNull
    @Override
    public LastWatchVideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_last_video_watch, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LastWatchVideoAdapter.ViewHolder holder, int position) {
        LastWatchVideoResponse videoResponse = lastWatchVideoResponses.get(position);

        holder.tv_lastVideoCmpName.setText(videoResponse.getCmp_name());
        holder.tv_lastVideoCLength.setText(videoResponse.getC_length());

    }

    @Override
    public int getItemCount() {
        return lastWatchVideoResponses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_lastVideoCmpName, tv_lastVideoCLength;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_lastVideoCmpName = itemView.findViewById(R.id.tv_lastVideoCmpName);
            tv_lastVideoCLength = itemView.findViewById(R.id.tv_lastVideoCLength);
        }
    }
}
