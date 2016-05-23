package com.example.l.common.activity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import com.example.l.common.R;
import com.example.l.common.api.RequestApi;
import com.example.l.common.api.RequestListener;
import com.example.l.common.base.BaseActivity;
import com.example.l.common.bean.Weather;
import butterknife.Bind;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @Bind(R.id.request_net)
    Button request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }

    @OnClick(R.id.request_net)
    public void request(View view){
        RequestApi.weatherInfo(new RequestListener<Weather>() {
            @Override
            public void onSuccess(Weather response) {
                showToast(response.toString(),0, Gravity.CENTER);
            }

            @Override
            public void onFailure(String error) {
                showToast(error,0, Gravity.CENTER);
            }
        });
    }

    @Override
    public void onClick(View view) {

    }
}
