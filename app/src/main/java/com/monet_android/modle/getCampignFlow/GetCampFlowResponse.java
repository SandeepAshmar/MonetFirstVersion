package com.monet_android.modle.getCampignFlow;

import java.util.ArrayList;

public class GetCampFlowResponse {

    private String cmp_id;

    private ArrayList<String> cmp_sequence;

    private String c_thumb_url;

    private String c_duration;

    private String cmp_eval;

    private String c_type;

    private String c_url;

    private String c_length;

    private String c_id;

    private String cf_id;

    public String getCmp_id ()
    {
        return cmp_id;
    }

    public void setCmp_id (String cmp_id)
    {
        this.cmp_id = cmp_id;
    }

    public ArrayList<String> getCmp_sequence ()
    {
        return cmp_sequence;
    }

    public void setCmp_sequence (ArrayList<String> cmp_sequence)
    {
        this.cmp_sequence = cmp_sequence;
    }

    public String getC_thumb_url ()
    {
        return c_thumb_url;
    }

    public void setC_thumb_url (String c_thumb_url)
    {
        this.c_thumb_url = c_thumb_url;
    }

    public String getC_duration ()
    {
        return c_duration;
    }

    public void setC_duration (String c_duration)
    {
        this.c_duration = c_duration;
    }

    public String getCmp_eval ()
    {
        return cmp_eval;
    }

    public void setCmp_eval (String cmp_eval)
    {
        this.cmp_eval = cmp_eval;
    }

    public String getC_type ()
    {
        return c_type;
    }

    public void setC_type (String c_type)
    {
        this.c_type = c_type;
    }

    public String getC_url ()
    {
        return c_url;
    }

    public void setC_url (String c_url)
    {
        this.c_url = c_url;
    }

    public String getC_length ()
    {
        return c_length;
    }

    public void setC_length (String c_length)
    {
        this.c_length = c_length;
    }

    public String getC_id ()
    {
        return c_id;
    }

    public void setC_id (String c_id)
    {
        this.c_id = c_id;
    }

    public String getCf_id ()
    {
        return cf_id;
    }

    public void setCf_id (String cf_id)
    {
        this.cf_id = cf_id;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [cmp_id = "+cmp_id+", cmp_sequence = "+cmp_sequence+", c_thumb_url = "+c_thumb_url+", c_duration = "+c_duration+", cmp_eval = "+cmp_eval+", c_type = "+c_type+", c_url = "+c_url+", c_length = "+c_length+", c_id = "+c_id+", cf_id = "+cf_id+"]";
    }

}
