package com.example.website.Tasks;

public class Mesimot {


    private String taskName;
    private String taskInfo;
    private String Status;
    private String Date;
    private boolean isSelected;


    public String getTaskName(){
        return taskName;
    }

    public String getTaskInfo(){
        return taskInfo;

    }
    public String getStatus(){
        return Status;
    }
    public String getDate(){
        return Date;
    }



    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setTaskInfo(String taskInfo) {
        this.taskInfo = taskInfo;
    }
    public void setDate(String date){
        this.Date = date;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


}
