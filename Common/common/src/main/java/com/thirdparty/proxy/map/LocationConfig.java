package com.thirdparty.proxy.map;

import com.amap.api.location.AMapLocationClientOption;

/**
 * @author:dongpo 创建时间: 6/8/2016
 * 描述:
 * 修改:
 */
public class LocationConfig {
    public AMapLocationClientOption option = null;
    public static final int MODE_BATTERY_SAVING = 1;
    public static final int MODE_DEVICE_SENSORS = 2;
    public static final int MODE_HIGHT_ACCURACY = 3;


    LocationConfig() {
        option = new AMapLocationClientOption();
    }

    public void setGpsFirst(boolean gpsFirst) {
        option.setGpsFirst(gpsFirst);
    }

    public void setLocationCacheEnable(boolean isCacheEnable) {
        option.setLocationCacheEnable(isCacheEnable);
    }

    public void setOnceLocation(boolean isLocateOnce) {
        option.setOnceLocation(isLocateOnce);
    }

    public void setInterval(long interval) {
        option.setInterval(interval);
    }

    public void setNeedAddress(boolean isNeedAddress) {
        option.setNeedAddress(isNeedAddress);
    }

    public void setLocationMode(int mode) {
        switch (mode){
            case MODE_BATTERY_SAVING:
                option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
                break;
            case MODE_DEVICE_SENSORS:
                option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Device_Sensors);
                break;
            case MODE_HIGHT_ACCURACY:
                option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
                break;
            default:
                option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
                break;
        }

    }

    public AMapLocationClientOption get() {
        return option;
    }

    public static class LocationOptionBuilder {
        public LocationConfig option = null;

        LocationOptionBuilder() {
            option = new LocationConfig();
        }

        public LocationOptionBuilder setGpsFirst(boolean gpsFirst) {
            option.setGpsFirst(gpsFirst);
            return this;
        }

        public LocationOptionBuilder setLocationCacheEnable(boolean isCacheEnable) {
            option.setLocationCacheEnable(isCacheEnable);
            return this;
        }

        public LocationOptionBuilder setOnceLocation(boolean isLocateOnce) {
            option.setOnceLocation(isLocateOnce);
            return this;
        }

        public LocationOptionBuilder setInterval(long interval) {
            option.setInterval(interval);
            return this;
        }

        public LocationOptionBuilder setNeedAddress(boolean isNeedAddress) {
            option.setNeedAddress(isNeedAddress);
            return this;
        }

        public LocationOptionBuilder setLocationMode(int mode) {
            option.setLocationMode(mode);
            return this;
        }

        public LocationConfig build() {
            return option;
        }
    }

    public static LocationOptionBuilder newBuilder() {
        return new LocationOptionBuilder();
    }
}
