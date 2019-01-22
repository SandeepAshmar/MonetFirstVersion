package com.monet_android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.monet_android.R;
import com.monet_android.halper.IOnItemClickListener;

import java.util.ArrayList;

import static com.monet_android.activity.questions.QuestionScreen.btn_questionNext;
import static com.monet_android.activity.questions.QuestionScreen.savedQuesAndAnswers;

public class RateAdapter extends RecyclerView.Adapter<RateAdapter.ViewHolder> {

    private Context context;
    private int size;
    private int row_index = -1;
    int i = 0;
    private IOnItemClickListener iOnItemClickListener;
    private String quesId;
    private String ansId;

    public RateAdapter(Context context, int size, IOnItemClickListener iOnItemClickListener, String quesId) {
        this.context = context;
        this.size = size;
        this.iOnItemClickListener = iOnItemClickListener;
        this.quesId = quesId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question_rate, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        i = i + 1;
        holder.tv_rate.setText(String.valueOf(i));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iOnItemClickListener != null) {
                    iOnItemClickListener.onItemClick(v, position);
                    i = 0;
                    notifyDataSetChanged();
                }
            }
        });

        if (savedQuesAndAnswers.getRateQuesId().contains(quesId)) {
            int pos = savedQuesAndAnswers.getRateQuesId().indexOf(quesId);
            ansId = String.valueOf(savedQuesAndAnswers.getRateAnsId().get(pos));
            if (holder.tv_rate.getText().equals(ansId)) {
                holder.rate_view.setBackgroundResource(R.drawable.rate_backround);
                holder.tv_rate.setTextColor(Color.parseColor("#ffffff"));
                btn_questionNext.setBackgroundResource(R.drawable.btn_pro_activate);
                btn_questionNext.setEnabled(true);
            } else {
                holder.rate_view.setBackgroundResource(R.drawable.circle);
                holder.tv_rate.setTextColor(Color.parseColor("#000000"));
            }
        } else {
            holder.rate_view.setBackgroundResource(R.drawable.circle);
            holder.tv_rate.setTextColor(Color.parseColor("#000000"));
        }


    }

    @Override
    public int getItemCount() {
        return size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_rate;
        View rate_view;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_rate = itemView.findViewById(R.id.tv_rate);
            rate_view = itemView.findViewById(R.id.rate_view);
        }
    }
}
