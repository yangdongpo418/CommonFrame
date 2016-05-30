package com.example.l.common.activity;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.l.common.R;
import com.example.l.common.api.BackEndApi;
import com.example.l.common.api.http.RequestListener;
import com.example.l.common.base.ToolBarActivity;
import com.example.l.common.bean.Weather;
import com.example.l.common.constants.Constants;
import com.example.l.common.utils.TLog;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.Bind;
import butterknife.OnClick;

public class MainActivity extends ToolBarActivity {

    @Nullable@Bind(R.id.request_net)
    Button request;

    @Nullable@Bind(R.id.drawee_image)
    SimpleDraweeView image;

    @Nullable@Bind(R.id.main_iv)
    ImageView main_iv;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        super.initView();
        setActionBarTitle("");
        setActionBarTitleColor(R.color.white);
    }

    @Override
    public void initData() {

    }

    @OnClick(R.id.request_net)
    public void request(View view){
        setActionBarTitleColor(R.color.white);
        BackEndApi.weatherInfo(new RequestListener<Weather>() {
            @Override
            public void onSuccess(Weather response) {
                showToast(response.toString(),0, Gravity.CENTER);
                TLog.i(response.toString());
                updateViewState(Constants.STATE_LOADING);
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
    }

    @OnClick(R.id.request_image)
    public void loadImage(View view){
        Uri uri = Uri.parse("http://10.0.2.2:8080/manager/5.png");
        image.setImageURI(uri);
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
        setStatusBarMode(true);
        View view = super.onBeforeSetContentLayout(contentView);
        setLoadingStateEnable(true,contentView);
        return view;
    }
}
