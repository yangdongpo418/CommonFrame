package com.thirdparty.proxy.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.amap.api.location.AMapLocation;

/**
 * @author:dongpo 创建时间: 6/7/2016
 * 描述:
 * 修改:
 */
public class Location implements Parcelable{
    public double longitude;
    public double latitude;
    public float precision;
    public String country;
    public String province;
    public String city;
    public String area;
    public String address;
    public int statsCode;
    public String road;
    public String street;
    public String poiName;
    public String streetNum;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.longitude);
        dest.writeDouble(this.latitude);
        dest.writeFloat(this.precision);
        dest.writeString(this.country);
        dest.writeString(this.province);
        dest.writeString(this.city);
        dest.writeString(this.area);
        dest.writeString(this.address);
        dest.writeInt(this.statsCode);
        dest.writeString(this.road);
        dest.writeString(this.street);
        dest.writeString(this.poiName);
        dest.writeString(this.streetNum);
    }

    public Location() {
    }

    protected Location(Parcel in) {
        this.longitude = in.readDouble();
        this.latitude = in.readDouble();
        this.precision = in.readFloat();
        this.country = in.readString();
        this.province = in.readString();
        this.city = in.readString();
        this.area = in.readString();
        this.address = in.readString();
        this.statsCode = in.readInt();
        this.road = in.readString();
        this.street = in.readString();
        this.poiName = in.readString();
        this.streetNum = in.readString();
    }

    public Location(AMapLocation loc){
        this.statsCode = loc.getErrorCode();
        this.address = loc.getAddress();
        this.country = loc.getCountry();
        this.longitude = loc.getLongitude();
        this.latitude = loc.getLatitude();
        this.precision = loc.getAccuracy();
        this.city = loc.getCity();
        this.area = loc.getDistrict();
        this.province = loc.getProvince();
        this.road = loc.getRoad();
        this.street = loc.getStreet();
        this.poiName = loc.getPoiName();
        this.streetNum = loc.getStreetNum();
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel source) {
            return new Location(source);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    @Override
    public String toString() {
        return "Location{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                ", precision=" + precision +
                ", country='" + country + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", area='" + area + '\'' +
                ", address='" + address + '\'' +
                ", statsCode=" + statsCode +
                ", road='" + road + '\'' +
                ", street='" + street + '\'' +
                ", poiName='" + poiName + '\'' +
                ", streetNum='" + streetNum + '\'' +
                '}';
    }
}
