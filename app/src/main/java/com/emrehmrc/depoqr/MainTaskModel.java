package com.emrehmrc.depoqr;

import java.util.ArrayList;

public class MainTaskModel {

    private String date;
    private String taskMan;
    private String content;
    private ArrayList<String> taskManList;
    private int taskManlistCount;

    public static ArrayList<MainTaskModel> getData() {

        ArrayList<MainTaskModel> datalist = new ArrayList<MainTaskModel>();
        MainTaskModel gecici = new MainTaskModel();

        for (int i = 0; i < 5; i++) {
            ArrayList<String> taskManList = new ArrayList<>();
            taskManList.add("emre");
            taskManList.add("eren");
            taskManList.add("güven");
            gecici.setDate("tarih");
            gecici.setTaskMan("Görevlendiren");
            gecici.setContent("İçerik");
            gecici.setTaskManList(taskManList);
            datalist.add(gecici);
        }
        return datalist;


    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getTaskMan() {
        return taskMan;
    }

    public void setTaskMan(String taskMan) {
        this.taskMan = taskMan;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<String> getTaskManList() {
        return taskManList;
    }

    public void setTaskManList(ArrayList<String> taskManList) {
        this.taskManList = taskManList;
    }

    public int getTaskManlistCount() {
        return taskManlistCount;
    }

    public void setTaskManlistCount(int taskManlistCount) {
        this.taskManlistCount = taskManlistCount;
    }
}
