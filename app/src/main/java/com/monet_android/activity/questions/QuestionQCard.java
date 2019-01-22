package com.monet_android.activity.questions;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.monet_android.R;
import com.monet_android.halper.OnClearFromRecentService;

public class QuestionQCard extends AppCompatActivity {

    private Button btn_quesqa_exit, btn_quesqa_proceed;
    private OnClearFromRecentService onClearFromRecentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_qcard);

        btn_quesqa_exit = findViewById(R.id.btn_quesqa_exit);
        btn_quesqa_proceed = findViewById(R.id.btn_quesqa_proceed);
        onClearFromRecentService = new OnClearFromRecentService();

        btn_quesqa_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_quesqa_exit.setClickable(false);
                finish();
            }
        });

        btn_quesqa_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_quesqa_proceed.setClickable(false);
                startActivity(new Intent(QuestionQCard.this, QuestionScreen.class));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onClearFromRecentService.onTaskRemoved(new Intent(QuestionQCard.this, OnClearFromRecentService.class));
        finish();
    }
}
