package com.monet_android.modle.radarChart;

public class Neutral {

    private Value value;

    private Image_path image_path;

    public Value getValue ()
    {
        return value;
    }

    public void setValue (Value value)
    {
        this.value = value;
    }

    public Image_path getImage_path ()
    {
        return image_path;
    }

    public void setImage_path (Image_path image_path)
    {
        this.image_path = image_path;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [value = "+value+", image_path = "+image_path+"]";
    }

}
