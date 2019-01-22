package com.monet_android.adapter;

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
import com.monet_android.halper.CheckBoxClickListner;
import com.monet_android.modle.question.Question_Options;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import static com.monet_android.activity.questions.QuestionScreen.savedQuesAndAnswers;

public class CheckBoxTypeAdapter extends RecyclerView.Adapter<CheckBoxTypeAdapter.ViewHolder> {

    private Context ctx;
    private CheckBoxClickListner iClickListener;
    private ArrayList<Question_Options> optionsArrayList;
    private String noneId = "";

    public CheckBoxTypeAdapter(Context ctx, CheckBoxClickListner iClickListener, ArrayList<Question_Options> optionsArrayList) {
        this.ctx = ctx;
        this.iClickListener = iClickListener;
        this.optionsArrayList = optionsArrayList;
    }

    @NonNull
    @Override
    public CheckBoxTypeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question_radio, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CheckBoxTypeAdapter.ViewHolder holder, final int position) {
        final Question_Options question_options = optionsArrayList.get(position);

        holder.rd_opetionValue.setText(question_options.getOption_value());

        holder.rd_view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(question_options.getOption_value().equalsIgnoreCase("none") || question_options.getOption_value().equalsIgnoreCase("none of the above") ||
                        question_options.getOption_value().equalsIgnoreCase("none of above") ||  question_options.getOption_value().equalsIgnoreCase("prefer not to answer")){

                    if(savedQuesAndAnswers.getCheckAnsId().contains(question_options.getOption_id())){
                        savedQuesAndAnswers.getCheckAnsId().clear();
                    }else{
                        savedQuesAndAnswers.getCheckAnsId().clear();
                        if (iClickListener != null) {
                            iClickListener.onItemCheckBoxClick(v, position, question_options.getQuestion_id(), question_options.getOption_id());
                        }
                        noneId = question_options.getOption_id();
                    }
                }else{
                    if(savedQuesAndAnswers.getCheckAnsId().contains(noneId)){
                        savedQuesAndAnswers.getCheckAnsId().clear();
                        if (iClickListener != null) {
                            iClickListener.onItemCheckBoxClick(v, position, question_options.getQuestion_id(), question_options.getOption_id());
                        }
                    }else{
                        if (iClickListener != null) {
                            iClickListener.onItemCheckBoxClick(v, position, question_options.getQuestion_id(), question_options.getOption_id());
                        }
                    }

                }
                notifyDataSetChanged();
            }
        });

        changeColor(holder, question_options);

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

    public void changeColor(ViewHolder holder, Question_Options question_options) {

        if (savedQuesAndAnswers == null || savedQuesAndAnswers.getCheckAnsId().size() == 0) {
            Log.e("", "");
        } else {
            if (savedQuesAndAnswers.getCheckQuesId().contains(question_options.getQuestion_id())) {
                if (savedQuesAndAnswers.getCheckAnsId().contains(question_options.getOption_id())) {
                    holder.rd_view.setBackgroundResource(R.drawable.ic_selected_background);
                    holder.rd_opetionValue.setTextColor(Color.parseColor("#ffffff"));
                } else {
                    holder.rd_view.setBackground(null);
                    holder.rd_opetionValue.setTextColor(Color.parseColor("#000000"));
                }
            } else {
                holder.rd_view.setBackground(null);
                holder.rd_opetionValue.setTextColor(Color.parseColor("#000000"));
            }
        }

    }

}
