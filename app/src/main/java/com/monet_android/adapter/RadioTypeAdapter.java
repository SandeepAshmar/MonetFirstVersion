package com.monet_android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.monet_android.R;
import com.monet_android.activity.questions.QuestionScreen;
import com.monet_android.halper.RadioClickListner;
import com.monet_android.modle.question.Question_Options;

import java.util.ArrayList;

import static com.monet_android.activity.questions.QuestionScreen.savedQuesAndAnswers;

public class RadioTypeAdapter extends RecyclerView.Adapter<RadioTypeAdapter.ViewHolder> {

    private Context ctx;
    private RadioClickListner iClickListener;
    private ArrayList<Question_Options> optionsArrayList;
    private int row_index = -1;

    public RadioTypeAdapter(Context ctx, RadioClickListner iClickListener, ArrayList<Question_Options> optionsArrayList) {
        this.ctx = ctx;
        this.iClickListener = iClickListener;
        this.optionsArrayList = optionsArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question_radio, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final Question_Options question_options = optionsArrayList.get(position);

        holder.rd_opetionValue.setText(question_options.getOption_value());

        holder.rd_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iClickListener != null) {
                    iClickListener.onItemClick(v, position, question_options.getQuestion_id(), question_options.getOption_id());
                    notifyDataSetChanged();
                }
            }
        });


        colorChange(holder, question_options);

    }

    @Override
    public int getItemCount() {
        return optionsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView rd_opetionValue;
        RelativeLayout rd_view;

        public ViewHolder(View itemView) {
            super(itemView);
            rd_opetionValue = itemView.findViewById(R.id.rd_opetionValue);
            rd_view = itemView.findViewById(R.id.rd_view);
        }
    }

    public void colorChange(ViewHolder holder, Question_Options question_options) {
        if (savedQuesAndAnswers == null || savedQuesAndAnswers.getRadioQuesIds().size() == 0 || savedQuesAndAnswers.getRadioAnsIds().size() == 0) {
            Log.e("", "");
        } else {
            if(savedQuesAndAnswers.getRadioQuesIds().contains(question_options.getQuestion_id())){
                if (savedQuesAndAnswers.getRadioAnsIds().contains(question_options.getOption_id())) {
                    holder.rd_view.setBackgroundResource(R.drawable.ic_selected_background);
                    holder.rd_opetionValue.setTextColor(Color.parseColor("#ffffff"));
                    QuestionScreen.btn_questionNext.setBackgroundResource(R.drawable.btn_pro_activate);
                    QuestionScreen.btn_questionNext.setEnabled(true);
                } else {
                    holder.rd_view.setBackground(null);
                    holder.rd_opetionValue.setTextColor(Color.parseColor("#000000"));
                }
            }else{
                holder.rd_view.setBackground(null);
                holder.rd_opetionValue.setTextColor(Color.parseColor("#000000"));
            }
        }
    }
}

