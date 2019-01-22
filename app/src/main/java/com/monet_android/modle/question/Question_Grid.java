package com.monet_android.modle.question;

public class Question_Grid {

    private String grid_id;

    private String grid_value;

    private String grid_q_id;

    public String getGrid_id ()
    {
        return grid_id;
    }

    public void setGrid_id (String grid_id)
    {
        this.grid_id = grid_id;
    }

    public String getGrid_value ()
    {
        return grid_value;
    }

    public void setGrid_value (String grid_value)
    {
        this.grid_value = grid_value;
    }

    public String getGrid_q_id ()
    {
        return grid_q_id;
    }

    public void setGrid_q_id (String grid_q_id)
    {
        this.grid_q_id = grid_q_id;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [grid_id = "+grid_id+", grid_value = "+grid_value+", grid_q_id = "+grid_q_id+"]";
    }

}
