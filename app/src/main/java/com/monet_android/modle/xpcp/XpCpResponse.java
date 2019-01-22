package com.monet_android.modle.xpcp;

public class XpCpResponse {

    private String xp_gain;

    private String xp;

    private String rp_id;

    private String r_cash_point;

    private String total_points_earned;

    private String last_update;

    private String title;

    private String level;

    private String max_xp;

    private String cp_gain;

    private String points;

    private String rp_u_id;

    public String getXp_gain ()
    {
        return xp_gain;
    }

    public void setXp_gain (String xp_gain)
    {
        this.xp_gain = xp_gain;
    }

    public String getXp ()
    {
        return xp;
    }

    public void setXp (String xp)
    {
        this.xp = xp;
    }

    public String getRp_id ()
    {
        return rp_id;
    }

    public void setRp_id (String rp_id)
    {
        this.rp_id = rp_id;
    }

    public String getR_cash_point ()
    {
        return r_cash_point;
    }

    public void setR_cash_point (String r_cash_point)
    {
        this.r_cash_point = r_cash_point;
    }

    public String getTotal_points_earned ()
    {
        return total_points_earned;
    }

    public void setTotal_points_earned (String total_points_earned)
    {
        this.total_points_earned = total_points_earned;
    }

    public String getLast_update ()
    {
        return last_update;
    }

    public void setLast_update (String last_update)
    {
        this.last_update = last_update;
    }

    public String getTitle ()
    {
        return title;
    }

    public void setTitle (String title)
    {
        this.title = title;
    }

    public String getLevel ()
    {
        return level;
    }

    public void setLevel (String level)
    {
        this.level = level;
    }

    public String getMax_xp ()
    {
        return max_xp;
    }

    public void setMax_xp (String max_xp)
    {
        this.max_xp = max_xp;
    }

    public String getCp_gain ()
    {
        return cp_gain;
    }

    public void setCp_gain (String cp_gain)
    {
        this.cp_gain = cp_gain;
    }

    public String getPoints ()
    {
        return points;
    }

    public void setPoints (String points)
    {
        this.points = points;
    }

    public String getRp_u_id ()
    {
        return rp_u_id;
    }

    public void setRp_u_id (String rp_u_id)
    {
        this.rp_u_id = rp_u_id;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [xp_gain = "+xp_gain+", xp = "+xp+", rp_id = "+rp_id+", r_cash_point = "+r_cash_point+", total_points_earned = "+total_points_earned+", last_update = "+last_update+", title = "+title+", level = "+level+", max_xp = "+max_xp+", cp_gain = "+cp_gain+", points = "+points+", rp_u_id = "+rp_u_id+"]";
    }

}
