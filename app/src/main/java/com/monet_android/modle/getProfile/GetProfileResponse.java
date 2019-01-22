package com.monet_android.modle.getProfile;

public class GetProfileResponse {

    private String DateOfBirth;

    private String Email;

    private String image_url;

    private String Gender;

    private String Mobile;

    private String fullName;

    private String Country;

    private String City;

    private String States;

    public String getDateOfBirth ()
    {
        return DateOfBirth;
    }

    public void setDateOfBirth (String DateOfBirth)
    {
        this.DateOfBirth = DateOfBirth;
    }

    public String getEmail ()
    {
        return Email;
    }

    public void setEmail (String Email)
    {
        this.Email = Email;
    }

    public String getImage_url ()
    {
        return image_url;
    }

    public void setImage_url (String image_url)
    {
        this.image_url = image_url;
    }

    public String getGender ()
    {
        return Gender;
    }

    public void setGender (String Gender)
    {
        this.Gender = Gender;
    }

    public String getMobile ()
    {
        return Mobile;
    }

    public void setMobile (String Mobile)
    {
        this.Mobile = Mobile;
    }

    public String getFullName ()
    {
        return fullName;
    }

    public void setFullName (String fullName)
    {
        this.fullName = fullName;
    }

    public String getCountry ()
    {
        return Country;
    }

    public void setCountry (String Country)
    {
        this.Country = Country;
    }

    public String getCity ()
    {
        return City;
    }

    public void setCity (String City)
    {
        this.City = City;
    }

    public String getStates ()
    {
        return States;
    }

    public void setStates (String States)
    {
        this.States = States;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [DateOfBirth = "+DateOfBirth+", Email = "+Email+", image_url = "+image_url+", Gender = "+Gender+", Mobile = "+Mobile+", fullName = "+fullName+", Country = "+Country+", City = "+City+", States = "+States+"]";
    }

//    private String value;
//
//    private String attribute;
//
//    private String label;
//
//    private String type;
//
//    public String getValue ()
//    {
//        return value;
//    }
//
//    public void setValue (String value)
//    {
//        this.value = value;
//    }
//
//    public String getAttribute ()
//    {
//        return attribute;
//    }
//
//    public void setAttribute (String attribute)
//    {
//        this.attribute = attribute;
//    }
//
//    public String getLabel ()
//    {
//        return label;
//    }
//
//    public void setLabel (String label)
//    {
//        this.label = label;
//    }
//
//    public String getType ()
//    {
//        return type;
//    }
//
//    public void setType (String type)
//    {
//        this.type = type;
//    }
//
//    @Override
//    public String toString()
//    {
//        return "ClassPojo [value = "+value+", attribute = "+attribute+", label = "+label+", type = "+type+"]";
//    }

}
