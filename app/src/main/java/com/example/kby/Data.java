package com.example.kby;
import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;

public class Data implements Serializable{       //데이터 직렬화

    private String title;
    private String content;
    private String time;
    private ArrayList<Uri> resId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ArrayList<Uri> getResId() {
        return resId;
    }

    public void setResId(ArrayList<Uri> resId) {
        this.resId = resId;
    }


}