package com.monet_android.modle.popularContent;

import com.monet_android.modle.newContent.NewResponse;

import java.util.ArrayList;

public class PopularPojo {

    private ArrayList<PopularResponse> response;

    private String message;

    private String status;

    private String code;

    public ArrayList<PopularResponse> getResponse ()
    {
        return response;
    }

    public void setResponse (ArrayList<PopularResponse> response)
    {
        this.response = response;
    }

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    public String getCode ()
    {
        return code;
    }

    public void setCode (String code)
    {
        this.code = code;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [response = "+response+", message = "+message+", status = "+status+", code = "+code+"]";
    }

}
