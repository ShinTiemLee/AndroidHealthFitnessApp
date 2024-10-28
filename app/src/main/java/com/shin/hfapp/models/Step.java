package com.shin.hfapp.models;

public class Step {
    private String date;
    private int steps;

    // No-argument constructor
    public Step() {
    }

    // Parameterized constructor
    public Step(int steps, String date) {
        this.steps = steps;
        this.date = date;
    }

    // Getter and setter methods
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    // Override toString method to display the step details in ListView
    @Override
    public String toString() {
        return "Date: " + date + ", Steps: " + steps;
    }
}
