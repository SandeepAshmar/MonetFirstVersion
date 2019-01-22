package com.monet_android.modle.radarChart;

public class RadarResponse {

    private Disgusted disgusted;

    private Sad sad;

    private Happy happy;

    private Scared scared;

    private Angry angry;

    private Suprised suprised;

    private Neutral neutral;

    public Disgusted getDisgusted ()
    {
        return disgusted;
    }

    public void setDisgusted (Disgusted disgusted)
    {
        this.disgusted = disgusted;
    }

    public Sad getSad ()
    {
        return sad;
    }

    public void setSad (Sad sad)
    {
        this.sad = sad;
    }

    public Happy getHappy ()
    {
        return happy;
    }

    public void setHappy (Happy happy)
    {
        this.happy = happy;
    }

    public Scared getScared ()
    {
        return scared;
    }

    public void setScared (Scared scared)
    {
        this.scared = scared;
    }

    public Angry getAngry ()
    {
        return angry;
    }

    public void setAngry (Angry angry)
    {
        this.angry = angry;
    }

    public Suprised getSuprised ()
    {
        return suprised;
    }

    public void setSuprised (Suprised suprised)
    {
        this.suprised = suprised;
    }

    public Neutral getNeutral ()
    {
        return neutral;
    }

    public void setNeutral (Neutral neutral)
    {
        this.neutral = neutral;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [disgusted = "+disgusted+", sad = "+sad+", happy = "+happy+", scared = "+scared+", angry = "+angry+", suprised = "+suprised+", neutral = "+neutral+"]";
    }

}
