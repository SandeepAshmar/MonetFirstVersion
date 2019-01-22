
package com.monet_android.activity.questions;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.monet_android.R;
import com.monet_android.activity.ThankyouPage;
import com.monet_android.activity.reaction.Reaction_Activity;
import com.monet_android.adapter.CheckBoxTypeAdapter;
import com.monet_android.adapter.RadioTypeAdapter;
import com.monet_android.adapter.RateAdapter;
import com.monet_android.connection.ApiInterface;
import com.monet_android.connection.BaseUrl;
import com.monet_android.halper.CheckBoxClickListner;
import com.monet_android.halper.IOnItemClickListener;
import com.monet_android.halper.OnClearFromRecentService;
import com.monet_android.halper.RadioClickListner;
import com.monet_android.modle.question.Question_Pojo;
import com.monet_android.modle.question.Question_Questions;
import com.monet_android.halper.AnswerSavedClass;
import com.monet_android.modle.survay.SurvayPojo;
import com.monet_android.modle.survay.SurvayPost;
import com.monet_android.qCards.FaceDetectInstructions;
import com.monet_android.utils.AppPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.monet_android.qCards.LandingScreen.arrayList;
import static com.monet_android.qCards.LandingScreen.stagingJson;
import static com.monet_android.utils.AppConstant.SUCCESS;

public class QuestionScreen extends AppCompatActivity {

    private SeekBar sb_question;
    private ApiInterface apiInterface;
    public static String token, type, cmp_id, cf_id, qusId, selectedAnsId, selectedQuesId, questionType;
    public static Button btn_questionNext;
    private int radioType;
    private EditText edt_questionType;
    private ProgressDialog pd;
    public static ArrayList<Question_Questions> questions = new ArrayList<>();
    private RecyclerView radio_rv;
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    public static TextView tv_questionNo, tv_question, tv_questionSelect;
    public static int questionSize;
    private String flagAdapter = "1";
    private int questionNo = 0, header = 0, child = 0;
    private Context context;
    private RadioTypeAdapter radioTypeAdapter;
    private CheckBoxTypeAdapter checkBoxTypeAdapter;
    private RateAdapter rateAdapter;
    public static AnswerSavedClass savedQuesAndAnswers = new AnswerSavedClass();
    private LinearLayout grid_Linear_Layout;
    private JSONObject dataPostJson1 = new JSONObject();
    private JSONObject quesJson = new JSONObject();
    private JSONObject quesJsonGrid = new JSONObject();
    private JSONArray jsonArray2 = new JSONArray();
    private ArrayList<String> selectedGridOptions = new ArrayList<>();
    private RelativeLayout rate_layout;
    private Dialog dialog;
    private String typeFiveReason = "";
    private OnClearFromRecentService onClearFromRecentService;

    private CheckBoxClickListner checkBoxClickListner = new CheckBoxClickListner() {
        @Override
        public void onItemCheckBoxClick(View view, int position, String quesId, String ansId) {

            selectedQuesId = quesId;

            if (savedQuesAndAnswers == null || savedQuesAndAnswers.getCheckQuesId().size() == 0 || savedQuesAndAnswers.getCheckAnsId().size() == 0) {
                savedQuesAndAnswers.setCheckQuesId(quesId);
                savedQuesAndAnswers.setCheckAnsId(ansId);
            } else {
                if (savedQuesAndAnswers.getCheckAnsId().contains(ansId)) {
                    int pos = savedQuesAndAnswers.getCheckAnsId().indexOf(ansId);
                    savedQuesAndAnswers.getCheckAnsId().remove(pos);
                } else {
                    savedQuesAndAnswers.setCheckAnsId(ansId);
                }
            }

            btn_questionNext.setBackgroundResource(R.drawable.btn_pro_activate);
            btn_questionNext.setEnabled(true);
        }
    };

    private RadioClickListner iOnItemClickListener = new RadioClickListner() {
        @Override
        public void onItemClick(View view, int position, String quesId, String ansId) {

            selectedQuesId = quesId;
            selectedAnsId = ansId;

            btn_questionNext.setBackgroundResource(R.drawable.btn_pro_activate);
            btn_questionNext.setEnabled(true);

            if (savedQuesAndAnswers == null || savedQuesAndAnswers.getRadioQuesIds().size() == 0 || savedQuesAndAnswers.getRadioAnsIds().size() == 0) {
                savedQuesAndAnswers.setRadioQuesIds(quesId);
                savedQuesAndAnswers.setRadioAnsIds(ansId);
            } else {
                if (savedQuesAndAnswers.getRadioQuesIds().contains(quesId)) {
                    int pos = savedQuesAndAnswers.getRadioQuesIds().indexOf(quesId);
                    savedQuesAndAnswers.getRadioAnsIds().set(pos, ansId);
                } else {
                    savedQuesAndAnswers.setRadioQuesIds(quesId);
                    savedQuesAndAnswers.setRadioAnsIds(ansId);
                }
            }

            if (questionType.equalsIgnoreCase("5")) {
                String yes = questions.get(questionNo).getOptions().get(position).getOption_value();
                if (yes.equalsIgnoreCase("Yes") || yes.equalsIgnoreCase("Yes ")) {
                    showDialog();
                } else {
                    typeFiveReason = "";
                }
            }
        }
    };

    private IOnItemClickListener rateItemClick = new IOnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {

            btn_questionNext.setBackgroundResource(R.drawable.btn_pro_activate);
            btn_questionNext.setEnabled(true);

            selectedAnsId = String.valueOf(position + 1);

            if (savedQuesAndAnswers == null || savedQuesAndAnswers.getRateQuesId().size() == 0) {
                savedQuesAndAnswers.setRateQuesId(selectedQuesId);
                savedQuesAndAnswers.setRateAnsId(selectedAnsId);
            } else {
                if (savedQuesAndAnswers.getRateQuesId().contains(selectedQuesId)) {
                    int pos = savedQuesAndAnswers.getRateQuesId().indexOf(selectedQuesId);
                    savedQuesAndAnswers.getRateAnsId().set(pos, selectedAnsId);
                } else {
                    savedQuesAndAnswers.setRateQuesId(selectedQuesId);
                    savedQuesAndAnswers.setRateAnsId(selectedAnsId);
                }
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_screen);

        btn_questionNext = findViewById(R.id.btn_questionNext);
        btn_questionNext.setEnabled(false);
        radio_rv = findViewById(R.id.radio_rv);
        tv_questionNo = findViewById(R.id.tv_questionNo);
        tv_question = findViewById(R.id.tv_question);
        tv_questionSelect = findViewById(R.id.tv_questionSelect);
        edt_questionType = findViewById(R.id.edt_questionType);
        grid_Linear_Layout = findViewById(R.id.grid_linearLayout);
        rate_layout = findViewById(R.id.rate_layout);
        context = this;
        apiInterface = BaseUrl.getClient().create(ApiInterface.class);

        onClearFromRecentService = new OnClearFromRecentService();
        disableSelection();

        dialog = new Dialog(QuestionScreen.this, R.style.Theme_Dialog);

        if (AppPreferences.isRedirectUser(this).equalsIgnoreCase("no")) {
            token = "Bearer " + AppPreferences.getApiToken(this);
        } else {
            token = "Bearer " + AppPreferences.getRedirectApiToken(this);
        }

        type = AppPreferences.getQuestionType(this);
        cmp_id = AppPreferences.getCmpId(this);
        cf_id = AppPreferences.getCfId(this);

        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Loading......");
        pd.show();

        linearLayoutManager = new LinearLayoutManager(this);

        gridLayoutManager = new GridLayoutManager(this, 5);

        edt_questionType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    btn_questionNext.setBackgroundResource(R.drawable.btn_pro_activate);
                    btn_questionNext.setEnabled(true);
                } else {
                    btn_questionNext.setBackgroundResource(R.drawable.btn_pro_gray);
                    btn_questionNext.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        getQuestionList();
        nextQuestion();
    }

    private void disableSelection() {
        edt_questionType.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
            }
        });
    }

    private void getQuestionList() {
        Call<Question_Pojo> pojoCall = apiInterface.getQuestioList(token, cmp_id, type);
        pojoCall.enqueue(new Callback<Question_Pojo>() {
            @Override
            public void onResponse(Call<Question_Pojo> call, Response<Question_Pojo> response) {
                pd.dismiss();
                if (response.body() == null) {
                    Toast.makeText(QuestionScreen.this, "Please try again", Toast.LENGTH_SHORT).show();
                } else {
                    if (response.body().getCode().equals(SUCCESS)) {
                        btn_questionNext.setVisibility(View.VISIBLE);
                        questions.addAll(response.body().getResponse().getQuestions());
                        questionSize = questions.size();
                        setQuestions();
                    } else {
                        Toast.makeText(QuestionScreen.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Question_Pojo> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(QuestionScreen.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setQuestions() {

        if (questionNo == (questionSize - 1)) {
            btn_questionNext.setText("submit");
        } else {
            btn_questionNext.setText("next");
        }

        if (questionNo == 0) {
            tv_questionNo.setText("Q1.");
        } else {
            tv_questionNo.setText("Q" + (questionNo + 1) + ".");
        }

        tv_question.setText(questions.get(questionNo).getQuestion());
        qusId = questions.get(questionNo).getQuestion_id();

        if (questions.get(questionNo).getQuestion_type().equals("1")) {
            radio_rv.setVisibility(View.VISIBLE);
            rate_layout.setVisibility(View.GONE);
            grid_Linear_Layout.setVisibility(View.GONE);
            edt_questionType.setVisibility(View.GONE);
            questionType = "1";
            selectedQuesId = questions.get(questionNo).getQuestion_id();
            tv_questionSelect.setText("Please select 1 answer");
            radio_rv.setLayoutManager(linearLayoutManager);
            radioTypeAdapter = new RadioTypeAdapter(this, iOnItemClickListener, questions.get(questionNo).getOptions());
            radio_rv.setAdapter(radioTypeAdapter);

        } else if (questions.get(questionNo).getQuestion_type().equals("2")) {
            radio_rv.setVisibility(View.VISIBLE);
            rate_layout.setVisibility(View.GONE);
            edt_questionType.setVisibility(View.GONE);
            grid_Linear_Layout.setVisibility(View.GONE);
            tv_questionSelect.setText("Please select all that apply");
            questionType = "2";
            selectedQuesId = questions.get(questionNo).getQuestion_id();
            radio_rv.setLayoutManager(linearLayoutManager);
            checkBoxTypeAdapter = new CheckBoxTypeAdapter(this, checkBoxClickListner, questions.get(questionNo).getOptions());
            radio_rv.setAdapter(checkBoxTypeAdapter);

        } else if (questions.get(questionNo).getQuestion_type().equals("3")) {
            radio_rv.setVisibility(View.VISIBLE);
            rate_layout.setVisibility(View.VISIBLE);
            edt_questionType.setVisibility(View.GONE);
            grid_Linear_Layout.setVisibility(View.GONE);
            questionType = "3";
            selectedQuesId = questions.get(questionNo).getQuestion_id();
            radioType = Integer.parseInt(questions.get(questionNo).getRadio_type());
            radio_rv.setLayoutManager(gridLayoutManager);
            rateAdapter = new RateAdapter(this, radioType, rateItemClick, selectedQuesId);
            radio_rv.setAdapter(rateAdapter);
            tv_questionSelect.setText("Please rate on a scale of 1-" + radioType + " with " + radioType + " being the strongest likely");
        } else if (questions.get(questionNo).getQuestion_type().equals("4")) {
            tv_questionSelect.setText("Please type your answer");
            radio_rv.setVisibility(View.GONE);
            selectedQuesId = questions.get(questionNo).getQuestion_id();
            rate_layout.setVisibility(View.GONE);
            edt_questionType.setVisibility(View.VISIBLE);
            grid_Linear_Layout.setVisibility(View.GONE);
            questionType = "4";
        } else if (questions.get(questionNo).getQuestion_type().equals("5")) {
            radio_rv.setVisibility(View.VISIBLE);
            rate_layout.setVisibility(View.GONE);
            edt_questionType.setVisibility(View.GONE);
            grid_Linear_Layout.setVisibility(View.GONE);
            tv_questionSelect.setText("Please select 1 answer");
            questionType = "5";
            selectedQuesId = questions.get(questionNo).getQuestion_id();
            radio_rv.setLayoutManager(linearLayoutManager);
            radioTypeAdapter = new RadioTypeAdapter(this, iOnItemClickListener, questions.get(questionNo).getOptions());
            radio_rv.setAdapter(radioTypeAdapter);
        } else if (questions.get(questionNo).getQuestion_type().equals("6")) {
            radio_rv.setVisibility(View.GONE);
            rate_layout.setVisibility(View.GONE);
            grid_Linear_Layout.setVisibility(View.VISIBLE);
            edt_questionType.setVisibility(View.GONE);
            tv_questionSelect.setText("Grid Choice");
            questionType = "6";
            selectedQuesId = questions.get(questionNo).getQuestion_id();
            grid_Linear_Layout.removeAllViewsInLayout();
            selectedGridOptions.clear();
            createGridAdapter();
        }
    }

    private void createGridAdapter() {
        header = questions.get(questionNo).getOptions().size();
        child = questions.get(questionNo).getOptions().get(0).getGrid().size();
        selectedQuesId = questions.get(questionNo).getQuestion_id();

//        if (savedQuesAndAnswers.getGridQuesIds().contains(selectedQuesId)) {
//            btn_questionNext.setBackgroundResource(R.drawable.btn_pro_activate);
//            btn_questionNext.setEnabled(true);
//        } else {
//            btn_questionNext.setBackgroundResource(R.drawable.btn_pro_gray);
//            btn_questionNext.setEnabled(false);
//        }

        RadioButton radioButton = null;
        RadioGroup radioGroup = null;
        dataPostJson1 = new JSONObject();
        quesJsonGrid = new JSONObject();

        for (int i = 0; i < header; i++) {
            TextView option_Header = new TextView(context);
            radioGroup = new RadioGroup(context);
            radioGroup.setPadding(2, 5, 2, 20);
            option_Header = new TextView(context);
            radioGroup = new RadioGroup(context);
            radioGroup.setVisibility(View.VISIBLE);
            option_Header.setText(questions.get(questionNo).getOptions().get(i).getOption_value());
            option_Header.setTextSize(16);
            option_Header.setTypeface(null, Typeface.BOLD);
            option_Header.setPadding(10, 20, 10, 20);
            option_Header.setId(Integer.parseInt(questions.get(questionNo).getOptions().get(i).getOption_id()));
            option_Header.setTextColor(Color.BLACK);

            for (int j = 0; j < child; j++) {
                radioButton = new RadioButton(context);
                radioButton.setText(questions.get(questionNo).getOptions().get(i).getGrid().get(j).getGrid_value());
                radioButton.setId(Integer.parseInt(questions.get(questionNo).getOptions().get(i).getGrid().get(j).getGrid_id()));
                radioGroup.addView(radioButton);
                radioButton.setId(Integer.parseInt(questions.get(questionNo).getOptions().get(i).getGrid().get(j).getGrid_id()));
            }
            grid_Linear_Layout.addView(option_Header);
            grid_Linear_Layout.addView(radioGroup);
            final RadioGroup finalRadioGroup = radioGroup;
            final TextView finalOption_Header = option_Header;

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    String optionId = String.valueOf(finalOption_Header.getId());
                    String gridOptionId = String.valueOf(finalRadioGroup.getCheckedRadioButtonId());

                    try {
                        quesJson.put(selectedQuesId, dataPostJson1);
                        dataPostJson1.put("options", quesJsonGrid);
                        quesJsonGrid.put(optionId, gridOptionId);
                        dataPostJson1.put("type", "6");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("json", "json " + quesJson);

                    selectedGridOptions.add(String.valueOf(finalOption_Header.getId()));

                    if (selectedGridOptions.size() == header) {
                        if (!savedQuesAndAnswers.getGridQuesIds().contains(selectedQuesId)) {
                            savedQuesAndAnswers.setGridQuesIds(selectedQuesId);
                        }
                        btn_questionNext.setBackgroundResource(R.drawable.btn_pro_activate);
                        btn_questionNext.setEnabled(true);
                    }
                }
            });
        }
    }

    private void nextQuestion() {
        btn_questionNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    setAnsJson();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                questionNo = (questionNo + 1);

                if (questionNo < questionSize) {
                    btn_questionNext.setBackgroundResource(R.drawable.btn_pro_gray);
                    btn_questionNext.setEnabled(false);
                    setQuestions();
                    jsonArray2 = new JSONArray();
                } else {
                    questionNo = (questionNo - 1);
                    submitAnswer();
                }
            }
        });
    }

    private void setPreviousQuestion() {
        questionNo = (questionNo - 1);

        if (questionNo == -1) {
            clearValues();
            finish();
            onClearFromRecentService.onTaskRemoved(new Intent(QuestionScreen.this, OnClearFromRecentService.class));
        } else {
            btn_questionNext.setBackgroundResource(R.drawable.btn_pro_gray);
            btn_questionNext.setEnabled(false);
            setQuestions();
        }
    }

    @Override
    public void onBackPressed() {
        setPreviousQuestion();
    }

    private void setAnsJson() throws JSONException {

        if (questions.get(questionNo).getQuestion_type().equalsIgnoreCase("1")) {
            dataPostJson1 = new JSONObject();
            quesJson.put(selectedQuesId, dataPostJson1);
            dataPostJson1.put("options", Integer.valueOf(selectedAnsId));
            dataPostJson1.put("type", "1");
        } else if (questions.get(questionNo).getQuestion_type().equalsIgnoreCase("2")) {
            dataPostJson1 = new JSONObject();
            quesJson.put(selectedQuesId, dataPostJson1);
            dataPostJson1.put("selectedOptions", jsonArray2);
            for (int i = 0; i < savedQuesAndAnswers.getCheckAnsId().size(); i++) {
                jsonArray2.put(i, Integer.valueOf(savedQuesAndAnswers.getCheckAnsId().get(i)));
            }
            dataPostJson1.put("type", "2");
        } else if (questions.get(questionNo).getQuestion_type().equalsIgnoreCase("3")) {
            dataPostJson1 = new JSONObject();
            quesJson.put(selectedQuesId, dataPostJson1);
            dataPostJson1.put("options", Integer.valueOf(selectedAnsId));
            dataPostJson1.put("type", "3");
        } else if (questions.get(questionNo).getQuestion_type().equalsIgnoreCase("4")) {
            dataPostJson1 = new JSONObject();
            quesJson.put(selectedQuesId, dataPostJson1);
            dataPostJson1.put("options", edt_questionType.getText().toString());
            dataPostJson1.put("type", "4");
        } else if (questions.get(questionNo).getQuestion_type().equalsIgnoreCase("5")) {
            dataPostJson1 = new JSONObject();
            quesJson.put(selectedQuesId, dataPostJson1);
            dataPostJson1.put("id", selectedAnsId);
            dataPostJson1.put("text", typeFiveReason);
            dataPostJson1.put("type", "5");
        }
//        else if (questions.get(questionNo).getQuestion_type().equalsIgnoreCase("6")) {
//            dataPostJson1 = new JSONObject();
//            quesJsonGrid = new JSONObject();
//            quesJson.put(selectedQuesId, dataPostJson1);
//            dataPostJson1.put("options", quesJsonGrid);
//            for (int i = 0; i < savedQuesAndAnswers.getGridAnsIds().size(); i++) {
//                quesJsonGrid.put(savedQuesAndAnswers.getGridOptionIds().get(i), savedQuesAndAnswers.getGridAnsIds().get(i));
//            }
//            dataPostJson1.put("type", "6");
//        }

        Log.d("json", "json " + quesJson);
    }

    private void submitAnswer() {
        pd.show();
        SurvayPost survayPost = new SurvayPost(quesJson.toString(), cf_id, cmp_id, type);
        Call<SurvayPojo> pojoCall = apiInterface.submitSurvayAns(token, survayPost);
        pojoCall.enqueue(new Callback<SurvayPojo>() {
            @Override
            public void onResponse(Call<SurvayPojo> call, Response<SurvayPojo> response) {
                pd.dismiss();
                if (response.body() == null) {
                    Toast.makeText(QuestionScreen.this, response.raw().message(), Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else {
                    if (response.body().getCode().equals(SUCCESS)) {

                        if (type.equalsIgnoreCase("pre")) {
                            try {
                                stagingJson.put("2", "2");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                stagingJson.put("3", "2");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        clearValues();
                        setScreen();
                    } else {
                        Toast.makeText(QuestionScreen.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<SurvayPojo> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(QuestionScreen.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearValues() {
        dataPostJson1 = null;
        quesJson = null;
        jsonArray2 = new JSONArray();
        questions.clear();

        savedQuesAndAnswers.getCheckAnsId().clear();
        savedQuesAndAnswers.getCheckQuesId().clear();
        savedQuesAndAnswers.getGridAnsIds().clear();
        savedQuesAndAnswers.getGridOptionIds().clear();
        savedQuesAndAnswers.getGridQuesIds().clear();
        savedQuesAndAnswers.getRadioAnsIds().clear();
        savedQuesAndAnswers.getRadioQuesIds().clear();
        savedQuesAndAnswers.getRateQuesId().clear();;
        savedQuesAndAnswers.getRateAnsId().clear();
    }

    private void setScreen() {
        int count = Integer.valueOf(AppPreferences.getCmpLength(this));
        int i = AppPreferences.getCmpLengthCount(this);

        if (count == 1) {
            if (arrayList.size() > i) {
                if (arrayList.get(i).equalsIgnoreCase("Pre")) {
                    AppPreferences.setQuestionType(this, "pre");
                    try {
                        stagingJson.put("2", "1");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    AppPreferences.setCmpLengthCount(this, i + 1);
                    startActivity(new Intent(this, QuestionQCard.class));
                    finish();
                } else if (arrayList.get(i).equalsIgnoreCase("Post")) {
                    AppPreferences.setQuestionType(this, "post");
                    try {
                        stagingJson.put("3", "1");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    AppPreferences.setCmpLengthCount(this, i + 1);
                    startActivity(new Intent(this, QuestionQCard.class));
                    finish();
                } else if (arrayList.get(i).equalsIgnoreCase("Emotion")) {
                    AppPreferences.setCmpLengthCount(this, i + 1);
                    try {
                        stagingJson.put("4", "1");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(new Intent(this, FaceDetectInstructions.class));
                    finish();
                } else if (arrayList.get(i).equalsIgnoreCase("Reaction")) {
                    AppPreferences.setCmpLengthCount(this, i + 1);
                    try {
                        stagingJson.put("5", "1");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(new Intent(this, Reaction_Activity.class));
                    finish();
                }
            } else {
                int flag = AppPreferences.getCmpLengthCountFlag(this);
                AppPreferences.setCmpLengthCountFlag(this, flag + 1);
                try {
                    stagingJson.put("6", "1");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(new Intent(this, ThankyouPage.class));
                finish();
            }
        } else {
            if (arrayList.size() > i) {
                if (arrayList.get(i).equalsIgnoreCase("Pre")) {
                    AppPreferences.setQuestionType(this, "pre");
                    try {
                        stagingJson.put("2", "1");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    AppPreferences.setCmpLengthCount(this, i + 1);
                    startActivity(new Intent(this, QuestionQCard.class));
                    finish();
                } else if (arrayList.get(i).equalsIgnoreCase("Post")) {
                    AppPreferences.setQuestionType(this, "post");
                    try {
                        stagingJson.put("3", "1");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    AppPreferences.setCmpLengthCount(this, i + 1);
                    startActivity(new Intent(this, QuestionQCard.class));
                    finish();
                } else if (arrayList.get(i).equalsIgnoreCase("Emotion")) {
                    AppPreferences.setCmpLengthCount(this, i + 1);
                    try {
                        stagingJson.put("4", "1");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(new Intent(this, FaceDetectInstructions.class));
                    finish();
                } else if (arrayList.get(i).equalsIgnoreCase("Reaction")) {
                    AppPreferences.setCmpLengthCount(this, i + 1);
                    try {
                        stagingJson.put("5", "1");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(new Intent(this, Reaction_Activity.class));
                    finish();
                }
            } else {
                int flag = AppPreferences.getCmpLengthCountFlag(this);
                AppPreferences.setCmpLengthCountFlag(this, flag + 1);
                try {
                    stagingJson.put("6", "1");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(new Intent(this, ThankyouPage.class));
                finish();
            }
        }
    }

    @SuppressLint("NewApi")
    public void showDialog() {
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start,
                                       int end, Spanned dest, int dstart, int dend) {

                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i)) &&
                            !Character.toString(source.charAt(i)).equals("_") &&
                            !Character.toString(source.charAt(i)).equals("-") &&
                            !Character.toString(source.charAt(i)).equals(" ")) {
                        return "";
                    }
                }
                return null;
            }
        };

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_typefive);

        final EditText edt_dialogFive;
        Button btn_dialogFive;

        edt_dialogFive = dialog.findViewById(R.id.edt_dialogFive);
        btn_dialogFive = dialog.findViewById(R.id.btn_dialogFive);

        edt_dialogFive.setFilters(new InputFilter[]{filter});

        dialog.show();

        btn_dialogFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_dialogFive.getText().toString().isEmpty()) {
                    Toast.makeText(QuestionScreen.this, "Please enter your review", Toast.LENGTH_SHORT).show();
                } else {
                    typeFiveReason = edt_dialogFive.getText().toString();
                    dialog.dismiss();
                }

            }
        });
    }
}
