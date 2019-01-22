package com.monet_android.modle.question;

import java.util.ArrayList;

public class Question_Response {

    private ArrayList<Question_Questions> Questions;

    public ArrayList<Question_Questions> getQuestions ()
    {
        return Questions;
    }

    public void setQuestions (ArrayList<Question_Questions> Questions)
    {
        this.Questions = Questions;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [Questions = "+Questions+"]";
    }

}
