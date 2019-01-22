package com.monet_android.modle.getCampDetails;

public class GetCampDetails_Response {

    private String cmp_id;

    private String c_thumb_url;

    private String cmp_name;

    private String c_length;

    public String getCmp_id ()
    {
        return cmp_id;
    }

    public void setCmp_id (String cmp_id)
    {
        this.cmp_id = cmp_id;
    }

    public String getC_thumb_url ()
    {
        return c_thumb_url;
    }

    public void setC_thumb_url (String c_thumb_url)
    {
        this.c_thumb_url = c_thumb_url;
    }

    public String getCmp_name ()
    {
        return cmp_name;
    }

    public void setCmp_name (String cmp_name)
    {
        this.cmp_name = cmp_name;
    }

    public String getC_length ()
    {
        return c_length;
    }

    public void setC_length (String c_length)
    {
        this.c_length = c_length;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [cmp_id = "+cmp_id+", c_thumb_url = "+c_thumb_url+", cmp_name = "+cmp_name+", c_length = "+c_length+"]";
    }

}
