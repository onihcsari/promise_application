package com.example.project1;

public class DBfile {
    String location;
    String name;
    String what;
    String number;
    int year;
    int month;
    int day;
    int hour;
    int minute;

    public DBfile(){} // 생성자 메서드

    public String getLocation() {
        return location;
    }
    public String getName(){
        return name;
    }
    public String getWhat(){
        return what;
    }
    public String getNumber() {
        return number;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    //값을 추가할때 쓰는 함수, MainActivity에서 addanimal함수에서 사용할 것임.
    public DBfile(String location, String name, String what, String number, int year, int month, int day, int hour, int minute){
        this.location = location;
        this.name = name;
        this.what = what;
        this.number = number;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }

    public String toString(){
        return "시간=" + getHour() + "시" + getMinute() + "분" + getName();
    }
}