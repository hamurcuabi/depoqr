package com.emrehmrc.depoqr.model;

public class MainTaskModel {

    private String taskDate;
    private String taskCreater;
    private String taskTag;
    private String taskDescription;
   private  String taskCountMan;
   private  String taskId;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskCountMan() {
        return taskCountMan;
    }

    public void setTaskCountMan(String taskCountMan) {
        this.taskCountMan = taskCountMan;
    }

    public MainTaskModel() {
    }

    public String getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(String taskDate) {
        this.taskDate = taskDate;
    }

    public String getTaskCreater() {
        return taskCreater;
    }

    public void setTaskCreater(String taskCreater) {
        this.taskCreater = taskCreater;
    }

    public String getTaskTag() {
        return taskTag;
    }

    public void setTaskTag(String taskTag) {
        this.taskTag = taskTag;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

}
