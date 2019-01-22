package com.monet_android.modle.register;

public class RegisterResponse {

    private String user_gender;

    private String user_country;

    private String user_lname;

    private String user_phone;

    private String api_token;

    private String user_password;

    private String user_id;

    private String user_email;

    private String user_dob;

    private String user_fname;

    public String getUser_gender ()
    {
        return user_gender;
    }

    public void setUser_gender (String user_gender)
    {
        this.user_gender = user_gender;
    }

    public String getUser_country ()
    {
        return user_country;
    }

    public void setUser_country (String user_country)
    {
        this.user_country = user_country;
    }

    public String getUser_lname ()
    {
        return user_lname;
    }

    public void setUser_lname (String user_lname)
    {
        this.user_lname = user_lname;
    }

    public String getUser_phone ()
    {
        return user_phone;
    }

    public void setUser_phone (String user_phone)
    {
        this.user_phone = user_phone;
    }

    public String getApi_token ()
    {
        return api_token;
    }

    public void setApi_token (String api_token)
    {
        this.api_token = api_token;
    }

    public String getUser_password ()
    {
        return user_password;
    }

    public void setUser_password (String user_password)
    {
        this.user_password = user_password;
    }

    public String getUser_id ()
    {
        return user_id;
    }

    public void setUser_id (String user_id)
    {
        this.user_id = user_id;
    }

    public String getUser_email ()
    {
        return user_email;
    }

    public void setUser_email (String user_email)
    {
        this.user_email = user_email;
    }

    public String getUser_dob ()
    {
        return user_dob;
    }

    public void setUser_dob (String user_dob)
    {
        this.user_dob = user_dob;
    }

    public String getUser_fname ()
    {
        return user_fname;
    }

    public void setUser_fname (String user_fname)
    {
        this.user_fname = user_fname;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [user_gender = "+user_gender+", user_country = "+user_country+", user_lname = "+user_lname+", user_phone = "+user_phone+", api_token = "+api_token+", user_password = "+user_password+", user_id = "+user_id+", user_email = "+user_email+", user_dob = "+user_dob+", user_fname = "+user_fname+"]";
    }

}
