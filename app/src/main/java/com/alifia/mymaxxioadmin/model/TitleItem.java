package com.alifia.mymaxxioadmin.model;

public class TitleItem extends ListItem {
    private String date;
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int getType() {
        return TYPE_TITLE;
    }
}
