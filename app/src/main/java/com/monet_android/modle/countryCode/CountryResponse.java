package com.monet_android.modle.countryCode;

public class CountryResponse {

    private String ip_countries_code;

    private String _id;

    private String countries_name;

    private String countries_id;

    public String getIp_countries_code ()
    {
        return ip_countries_code;
    }

    public void setIp_countries_code (String ip_countries_code)
    {
        this.ip_countries_code = ip_countries_code;
    }

    public String get_id ()
    {
        return _id;
    }

    public void set_id (String _id)
    {
        this._id = _id;
    }

    public String getCountries_name ()
    {
        return countries_name;
    }

    public void setCountries_name (String countries_name)
    {
        this.countries_name = countries_name;
    }

    public String getCountries_id ()
    {
        return countries_id;
    }

    public void setCountries_id (String countries_id)
    {
        this.countries_id = countries_id;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [ip_countries_code = "+ip_countries_code+", _id = "+_id+", countries_name = "+countries_name+", countries_id = "+countries_id+"]";
    }

}
