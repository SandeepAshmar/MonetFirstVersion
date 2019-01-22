package com.monet_android.modle.xpcp;

public class XpCpPojo {

    private XpCpResponse response;

    private String message;

    private String status;

    private String code;

    public XpCpResponse getResponse ()
    {
        return response;
    }

    public void setResponse (XpCpResponse response)
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
