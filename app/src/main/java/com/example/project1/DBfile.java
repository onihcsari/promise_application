package com.example.project1;

import java.util.Date;
import java.util.List;

public class DBfile {
    String date;
    String time;
    String title;
    String location;
    String category;
    String number;
    List<String> uid;
    public DBfile(){} // 생성자 메서드

    public String getDate() {
        return date;
    }
    public String getTime() {
        return time;
    }
    public String getTitle() {
        return title;
    }
    public String getLocation() {
        return location;
    }
    public String getCategory() {
        return category;
    }
    public String getNumber() {
        return number;
    }
    public List<String> getUid(){
        return uid;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public void setUid(List<String> uid){
        this.uid = uid;
    }

    //값을 추가할때 쓰는 함수, MainActivity에서 addanimal함수에서 사용할 것임.
    public DBfile(String date, String time, String title, String location, String category, String number, List<String> uid){
        this.date = date;
        this.time = time;
        this.title = title;
        this.location = location;
        this.category = category;
        this.number = number;
        this.uid = uid;
    }
}