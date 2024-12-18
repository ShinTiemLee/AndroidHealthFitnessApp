package com.shin.hfapp.models;

public class BMIRecord {
    private String id;
    private float weight;
    private float height;
    private float bmi;
    private String date;

    public BMIRecord() {

    }

    public BMIRecord(String id,float weight, float height, float bmi, String date) {
        this.id=id;
        this.weight = weight;
        this.height = height;
        this.bmi = bmi;
        this.date = date;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getBmi() {
        return bmi;
    }

    public void setBmi(float bmi) {
        this.bmi = bmi;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Date: " + date + ", BMI: " + bmi;
    }
}
