package com.example.l.common.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.l.common.R;
import com.example.l.common.api.BackEndApi;
import com.example.l.common.api.http.RequestListener;
import com.example.l.common.base.ToolBarActivity;
import com.example.l.common.utils.TLog;
import com.example.l.common.widget.PullReFreshViewSimple;
import com.facebook.drawee.view.SimpleDraweeView;
import com.thirdparty.proxy.bean.Location;
import com.thirdparty.proxy.map.LocationClient;

import butterknife.Bind;
import butterknife.OnClick;

public class MainActivity extends ToolBarActivity {

    @Nullable@Bind(R.id.request_net)
    Button request;

    @Nullable@Bind(R.id.drawee_image)
    SimpleDraweeView image;

    @Bind(R.id.main_pull_refresh)
    PullReFreshViewSimple pullRefresh;

    @Bind(R.id.location_address)
    TextView locationAddress;

    private BroadcastReceiver mReceiver;
    private LocationClient mLocationClient;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        super.initView();
        setActionBarTitle("");
        setActionBarTitleColor(R.color.white);

        pullRefresh.setOnRefreshListener(new PullReFreshViewSimple.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("Log_text", "PullActivity+onRefresh+下拉刷新了");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(10000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                    }
                }).start();
            }
        });
    }

    @Override
    public void initData() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationClient.ACTION_LOCATION_FAILURE);
        filter.addAction(LocationClient.ACTION_LOCATION_SUCCESS);
        filter.addCategory(LocationClient.CATEGORY_LOCATION);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Location location = intent.getParcelableExtra(LocationClient.LOCATION);
                String action = intent.getAction();
                if(TextUtils.equals(action,LocationClient.ACTION_LOCATION_FAILURE)){
                    showToast("定位失败",android.R.drawable.sym_def_app_icon, Gravity.CENTER);
                }else{
                    showToast("定位成功",android.R.drawable.sym_def_app_icon, Gravity.CENTER);
                    locationAddress.setText(location.toString());
                }
            }
        };

        registerReceiver(mReceiver,filter);

        mLocationClient = new LocationClient(getApplicationContext());
    }

    @OnClick(R.id.request_net)
    public void request(View view){
        setActionBarTitleColor(R.color.white);
        BackEndApi.baiDu(new RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                showToast(response.toString(),0, Gravity.CENTER);
                TLog.i(response);
//                updateViewState(Constants.STATE_LOADING);
            }

            @Override
            public void onFailure(String error) {
                showToast(error,0, Gravity.CENTER);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        if(mLocationClient !=null){
            mLocationClient.onDestroy();
            mLocationClient = null;
        }
    }

    @OnClick(R.id.request_image)
    public void loadImage(View view){
        Uri uri = Uri.parse("http://10.0.2.2:8080/manager/5.png");
        image.setImageURI(uri);
        showToast("加载图片",android.R.drawable.sym_def_app_icon,Gravity.LEFT);
    }

    @OnClick(R.id.complete)
    public void finishRefresh(View view){
        pullRefresh.complete();
        showToast("举报点击",android.R.drawable.sym_def_app_icon,Gravity.LEFT);
    }

    @OnClick(R.id.location)
    public void location(View view){
        mLocationClient.startLocation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_check:
//                showToast("举报点击",android.R.drawable.sym_def_app_icon,Gravity.LEFT);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected View onBeforeSetContentLayout(View contentView) {
        setStatusBarMode(false);
        setLoadingStateEnable(true,contentView);
        View view = super.onBeforeSetContentLayout(contentView);
        return view;
    }

}
