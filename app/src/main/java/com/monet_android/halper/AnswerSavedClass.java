package com.monet_android.halper;

import java.util.ArrayList;

public class AnswerSavedClass {

    public ArrayList<String> radioQuesIds = new ArrayList<>();
    public ArrayList<String> radioAnsIds = new ArrayList<>();
    public ArrayList<String> gridQuesIds = new ArrayList<>();
    public ArrayList<String> gridOptionIds = new ArrayList<>();
    public ArrayList<String> gridAnsIds = new ArrayList<>();
    public ArrayList<String> checkQuesId = new ArrayList<>();
    public ArrayList<String> checkAnsId = new ArrayList<>();
    public ArrayList<String> rateQuesId = new ArrayList<>();
    public ArrayList<String> rateAnsId = new ArrayList<>();

    public ArrayList<String> getRateQuesId() {
        return rateQuesId;
    }

    public void setRateQuesId(String rateQuesId) {
        this.rateQuesId.add(rateQuesId);
    }

    public ArrayList<String> getRateAnsId() {
        return rateAnsId;
    }

    public void setRateAnsId(String rateAnsId) {
        this.rateAnsId.add(rateAnsId);
    }

    public ArrayList<String> getRadioAnsIds() {
        return radioAnsIds;
    }

    public void setRadioAnsIds(String radioAnsIds) {
        this.radioAnsIds.add(radioAnsIds);
    }

    public ArrayList<String> getRadioQuesIds() {
        return radioQuesIds;
    }

    public void setRadioQuesIds(String radioQuesIds) {
        this.radioQuesIds.add(radioQuesIds);
    }

    public ArrayList<String> getGridQuesIds() {
        return gridQuesIds;
    }

    public void setGridQuesIds(String gridQuesIds) {
        this.gridQuesIds.add(gridQuesIds);
    }

    public ArrayList<String> getGridOptionIds() {
        return gridOptionIds;
    }

    public void setGridOptionIds(String gridOptionIds) {
        this.gridOptionIds.add(gridOptionIds);
    }

    public ArrayList<String> getGridAnsIds() {
        return gridAnsIds;
    }

    public void setGridAnsIds(String gridAnsIds) {
        this.gridAnsIds.add(gridAnsIds);
    }

    public ArrayList<String> getCheckQuesId() {
        return checkQuesId;
    }

    public void setCheckQuesId(String checkQuesId) {
        this.checkQuesId.add(checkQuesId);
    }

    public ArrayList<String> getCheckAnsId() {
        return checkAnsId;
    }

    public void setCheckAnsId(String checkAnsId) {
        this.checkAnsId.add(checkAnsId);
    }
}
