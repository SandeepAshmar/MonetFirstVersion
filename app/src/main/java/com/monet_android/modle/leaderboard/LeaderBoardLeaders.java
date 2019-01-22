package com.monet_android.modle.leaderboard;

public class LeaderBoardLeaders {

    private String user_profile_image;

    private String rank;

    private String name;

    private String user_id;

    private String points;

    public String getUser_profile_image ()
    {
        return user_profile_image;
    }

    public void setUser_profile_image (String user_profile_image)
    {
        this.user_profile_image = user_profile_image;
    }

    public String getRank ()
    {
        return rank;
    }

    public void setRank (String rank)
    {
        this.rank = rank;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getUser_id ()
    {
        return user_id;
    }

    public void setUser_id (String user_id)
    {
        this.user_id = user_id;
    }

    public String getPoints ()
    {
        return points;
    }

    public void setPoints (String points)
    {
        this.points = points;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [user_profile_image = "+user_profile_image+", rank = "+rank+", name = "+name+", user_id = "+user_id+", points = "+points+"]";
    }
    
}
