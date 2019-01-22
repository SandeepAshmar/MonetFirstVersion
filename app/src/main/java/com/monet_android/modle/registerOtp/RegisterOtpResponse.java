package com.monet_android.modle.registerOtp;

public class RegisterOtpResponse {

    private String Status;

    private String Details;

    public String getStatus ()
    {
        return Status;
    }

    public void setStatus (String Status)
    {
        this.Status = Status;
    }

    public String getDetails ()
    {
        return Details;
    }

    public void setDetails (String Details)
    {
        this.Details = Details;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [Status = "+Status+", Details = "+Details+"]";
    }
}
