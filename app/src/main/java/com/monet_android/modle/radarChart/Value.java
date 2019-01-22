package com.monet_android.modle.radarChart;

public class Value {

    private String current;

    private String all;

    public String getCurrent ()
    {
        return current;
    }

    public void setCurrent (String current)
    {
        this.current = current;
    }

    public String getAll ()
    {
        return all;
    }

    public void setAll (String all)
    {
        this.all = all;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [current = "+current+", all = "+all+"]";
    }

}
