package com.example.matedicine.model;

public class Medicine {
    public Medicine(String image, String title, String time, String pills, String amount) {
        this.image = image;
        this.title = title;
        this.time = time;
        this.pills = pills;
        this.amount = amount;
    }

    public Medicine() {
    }

    private String id;
    private String image;
    private String title;
    private String time;
    private String pills;

    private String amount;

    public String getAmount() {
        return amount;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPills() {
        return pills;
    }

    public void setPills(String pills) {
        this.pills = pills;
    }
}
