package com.example.l.common.bean;

/**
 * Created by byang059 on 5/19/2016.
 */
public class WeatherInfo {
    public String city;
    public String cityid;
    public String temp1;
    public String temp2;
    public String weather;
    public String img1;
    public String img2;
    public String ptime;

    @Override
    public String toString() {
        return "WeatherInfo{" +
                "city='" + city + '\'' +
                ", cityid='" + cityid + '\'' +
                ", temp1='" + temp1 + '\'' +
                ", temp2='" + temp2 + '\'' +
                ", weather='" + weather + '\'' +
                ", img1='" + img1 + '\'' +
                ", img2='" + img2 + '\'' +
                ", ptime='" + ptime + '\'' +
                '}';
    }
}

