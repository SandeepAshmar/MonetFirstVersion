package com.monet_android.modle.leaderboard;

import java.util.ArrayList;

public class LeaderBoardResponse {

    private LeaderBoardUser user;

    private ArrayList<LeaderBoardLeaders> leaders;

    public LeaderBoardUser getUser ()
    {
        return user;
    }

    public void setUser (LeaderBoardUser user)
    {
        this.user = user;
    }

    public ArrayList<LeaderBoardLeaders> getLeaders ()
    {
        return leaders;
    }

    public void setLeaders (ArrayList<LeaderBoardLeaders> leaders)
    {
        this.leaders = leaders;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [user = "+user+", leaders = "+leaders+"]";
    }

}
