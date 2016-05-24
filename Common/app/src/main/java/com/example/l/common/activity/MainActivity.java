package com.example.l.common.activity;

import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import com.example.l.common.R;
import com.example.l.common.api.BackEndApi;
import com.example.l.common.api.http.RequestListener;
import com.example.l.common.base.BaseActivity;
import com.example.l.common.bean.Weather;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.Bind;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @Bind(R.id.request_net)
    Button request;

    @Bind(R.id.drawee_image)
    SimpleDraweeView image;

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
        BackEndApi.weatherInfo(new RequestListener<Weather>() {
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

    @OnClick(R.id.request_image)
    public void loadImage(View view){
        Uri uri = Uri.parse("http://10.0.2.2:8080/manager/5.png");
        image.setImageURI(uri);
    }


    @Override
    public void onClick(View view) {

    }
}
