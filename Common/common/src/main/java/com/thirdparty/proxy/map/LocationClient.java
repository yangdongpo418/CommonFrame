package com.thirdparty.proxy.map;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.poisearch.PoiSearch;
import com.thirdparty.proxy.bean.Location;
import com.thirdparty.proxy.map.util.Constants;

/**
 * @author:dongpo 创建时间: 6/7/2016
 * 描述:
 * 修改:
 */
public class LocationClient implements AMapLocationListener {

    public static final String ACTION_LOCATION_SUCCESS = "action_location_success";
    public static final String ACTION_LOCATION_FAILURE = "action_location_failure";
    public static final String CATEGORY_LOCATION = "catetory_location";
    public static final String LOCATION = "location";

    /**
     * 开始定位
     */
    public final static int MSG_LOCATION_START = 0;
    /**
     * 定位完成
     */
    public final static int MSG_LOCATION_FINISH = 1;
    /**
     * 停止定位
     */
    public final static int MSG_LOCATION_STOP = 2;

    private AMapLocationClient locationClient = null;
    private LocationConfig locationOption = null;

    Handler mHandler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            switch (msg.what) {
                //开始定位
                case MSG_LOCATION_START:

                    break;
                // 定位完成
                case MSG_LOCATION_FINISH:
                    AMapLocation loc = (AMapLocation) msg.obj;
                    Location location = new Location(loc);
                    if (loc.getErrorCode() == 0) {
                        sendLocationBroadcast(ACTION_LOCATION_SUCCESS, location);
                    } else {
                        sendLocationBroadcast(ACTION_LOCATION_FAILURE, location);
                    }

                    break;
                //停止定位
                case MSG_LOCATION_STOP:

                    break;
                default:
                    break;
            }
        }

        ;
    };
    private Context mContext;
    private PoiSearch.Query mQuery;

    public LocationClient(Context context) {
        mContext = context;
        locationClient = new AMapLocationClient(context);
        // 设置定位监听
        locationClient.setLocationListener(this);
    }


    public void onDestroy() {
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }

        if (mContext != null) {
            mContext = null;
        }
    }

    public void startLocation() {
        if (locationOption == null) {
            locationOption = getDefaultConfig();
        }
        // 设置定位参数
        locationClient.setLocationOption(locationOption.get());
        // 启动定位
        locationClient.startLocation();
        mHandler.sendEmptyMessage(MSG_LOCATION_START);
    }

    public Location getLastLocation() {
        AMapLocation lastKnownLocation = locationClient.getLastKnownLocation();
        if (lastKnownLocation == null) {
            Location location = new Location();
            location.longitude = Constants.SHANGHAI.longitude;
            location.latitude = Constants.SHANGHAI.latitude;
            return location;
        }
        return new Location(lastKnownLocation);
    }

    public void stopLocation() {
        locationClient.stopLocation();
    }

    public void sendLocationBroadcast(String action, Location location) {
        Intent intent = new Intent(action);
        intent.addCategory(CATEGORY_LOCATION);
        intent.putExtra(LOCATION, location);
        mContext.sendBroadcast(intent);
    }

    public LocationConfig getDefaultConfig() {
        return LocationConfig.newBuilder().setLocationMode(LocationConfig.MODE_BATTERY_SAVING).setLocationCacheEnable(true).setOnceLocation(true).build();
    }


    @Override
    public void onLocationChanged(AMapLocation loc) {
        if (null != loc) {
            Message msg = mHandler.obtainMessage();
            msg.obj = loc;
            msg.what = MSG_LOCATION_FINISH;
            mHandler.sendMessage(msg);
        }
    }

}
