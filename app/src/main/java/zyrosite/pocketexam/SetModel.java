package zyrosite.pocketexam;

import java.util.Date;

public class SetModel {
    private int index, no_of_questions, duration;
    private String subject, categoryID, setID, date;
    private boolean published;
    private Date timeStamp;
    public SetModel() {
    }

    public SetModel(int index, int no_of_questions, int duration, String subject, String categoryID, String setID, String date, boolean published, Date timeStamp) {
        this.index = index;
        this.no_of_questions = no_of_questions;
        this.duration = duration;
        this.subject = subject;
        this.categoryID = categoryID;
        this.setID = setID;
        this.date = date;
        this.published = published;
        this.timeStamp = timeStamp;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getNo_of_questions() {
        return no_of_questions;
    }

    public void setNo_of_questions(int no_of_questions) {
        this.no_of_questions = no_of_questions;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public String getSetID() {
        return setID;
    }

    public void setSetID(String setID) {
        this.setID = setID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }
}
