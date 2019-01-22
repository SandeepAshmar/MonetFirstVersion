package com.monet_android.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.monet_android.R;
import com.monet_android.halper.IOnItemClickListener;
import com.monet_android.modle.reacionIcon.Item;

import java.util.ArrayList;

import static com.monet_android.activity.reaction.ReactionWatchVideo.iconList;

public class IconsAdapter extends RecyclerView.Adapter<IconsAdapter.ViewHolder> {

//    private ArrayList<Item> items = new ArrayList<>();
    private Context context;
    private IOnItemClickListener iClickListener;


    public IconsAdapter(ArrayList<Item> items, Context context, IOnItemClickListener iClickListener) {
//        this.items = items;
        this.context = context;
        this.iClickListener = iClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_icons_grid, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.textView.setText(iconList.get(position).getReactionName());

        if (iconList.get(position).getReactionName().equals("Like")) {
            holder.imageView.setImageResource(R.drawable.ic_like);
        }
        if (iconList.get(position).getReactionName().equals("Love")) {
            holder.imageView.setImageResource(R.drawable.ic_love);
        }
        if (iconList.get(position).getReactionName().equals("Want")) {
            holder.imageView.setImageResource(R.drawable.ic_want);
        }
        if (iconList.get(position).getReactionName().equals("Memorable")) {
            holder.imageView.setImageResource(R.drawable.ic_memorable);
        }
        if (iconList.get(position).getReactionName().equals("Dislike")) {
            holder.imageView.setImageResource(R.drawable.ic_dislike);
        }
        if (iconList.get(position).getReactionName().equals("Accurate")) {
            holder.imageView.setImageResource(R.drawable.ic_helpful);
        }
        if (iconList.get(position).getReactionName().equals("Misleading")) {
            holder.imageView.setImageResource(R.drawable.ic_not_helpful);
        }
        if (iconList.get(position).getReactionName().equals("Engaging")) {
            holder.imageView.setImageResource(R.drawable.ic_engaging);
        }
        if (iconList.get(position).getReactionName().equals("Boring")) {
            holder.imageView.setImageResource(R.drawable.ic_bore);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iClickListener != null) {
                    iClickListener.onItemClick(v, position);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return iconList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.tv_gridItems);
            imageView = itemView.findViewById(R.id.img_gridItems);
        }
    }
}
