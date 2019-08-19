package com.lefeee.bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class WorkList {
    /**
     * finishtime_end : 2019-07-20 08:58:00
     * finishtime_start : 2019-07-19 08:57:55
     * classId : 1
     * tasktype : 1
     * paperCode : 70801
     * teamaterialversion : 1
     * testpapertitle : 1
     * subjectId : 1
     */

    private String finishtime_end;
    private String finishtime_start;
    private int classId;
    private String tasktype;
    private String paperCode;
    private String teamaterialversion;
    private String testpapertitle;
    private int subjectId;

    public static WorkList objectFromData(String str) {

        return new com.google.gson.Gson().fromJson(str, WorkList.class);
    }

    public static WorkList objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new com.google.gson.Gson().fromJson(jsonObject.getString(str), WorkList.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<WorkList> arrayWorkListFromData(String str) {

        Type listType = new com.google.gson.reflect.TypeToken<ArrayList<WorkList>>() {
        }.getType();

        return new com.google.gson.Gson().fromJson(str, listType);
    }

    public static List<WorkList> arrayWorkListFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new com.google.gson.reflect.TypeToken<ArrayList<WorkList>>() {
            }.getType();

            return new com.google.gson.Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }

    public String getFinishtime_end() {
        return finishtime_end;
    }

    public void setFinishtime_end(String finishtime_end) {
        this.finishtime_end = finishtime_end;
    }

    public String getFinishtime_start() {
        return finishtime_start;
    }

    public void setFinishtime_start(String finishtime_start) {
        this.finishtime_start = finishtime_start;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getTasktype() {
        return tasktype;
    }

    public void setTasktype(String tasktype) {
        this.tasktype = tasktype;
    }

    public String getPaperCode() {
        return paperCode;
    }

    public void setPaperCode(String paperCode) {
        this.paperCode = paperCode;
    }

    public String getTeamaterialversion() {
        return teamaterialversion;
    }

    public void setTeamaterialversion(String teamaterialversion) {
        this.teamaterialversion = teamaterialversion;
    }

    public String getTestpapertitle() {
        return testpapertitle;
    }

    public void setTestpapertitle(String testpapertitle) {
        this.testpapertitle = testpapertitle;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    @Override
    public String toString() {
        return "WorkList{" +
                "finishtime_end='" + finishtime_end + '\'' +
                ", finishtime_start='" + finishtime_start + '\'' +
                ", classId=" + classId +
                ", tasktype='" + tasktype + '\'' +
                ", paperCode='" + paperCode + '\'' +
                ", teamaterialversion='" + teamaterialversion + '\'' +
                ", testpapertitle='" + testpapertitle + '\'' +
                ", subjectId=" + subjectId +
                '}';
    }
}
