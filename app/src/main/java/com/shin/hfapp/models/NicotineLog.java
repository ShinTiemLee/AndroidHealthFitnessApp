package com.shin.hfapp.models;

public class NicotineLog {
    private String type;
    private int count;
    private String date;
    private String notes;

    public NicotineLog(String type, int count, String date, String notes) {
        this.type = type;
        this.count = count;
        this.date = date;
        this.notes = notes;
    }

    public String getType() {
        return type;
    }

    public int getCount() {
        return count;
    }

    public String getDate() {
        return date;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setType(String type) {
        this.type = type;
    }
}
