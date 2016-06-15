package com.example.l.common.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.amap.api.maps2d.model.Marker;
import com.amap.api.services.core.PoiItem;
import com.example.l.common.R;
import com.example.l.common.api.BackEndApi;
import com.example.l.common.api.http.RequestListener;
import com.example.l.common.base.BaseListViewAdapter;
import com.example.l.common.base.CommViewHolder;
import com.example.l.common.base.ToolBarActivity;
import com.example.l.common.utils.TLog;
import com.example.l.common.utils.WindowUtils;
import com.example.l.common.widget.PullReFreshViewSimple;
import com.facebook.drawee.view.SimpleDraweeView;
import com.thirdparty.proxy.bean.Location;
import com.thirdparty.proxy.map.LocationClient;
import com.thirdparty.proxy.map.view.MapFragment;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

public class MainActivity extends ToolBarActivity implements TextWatcher, View.OnFocusChangeListener {

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
    private MapFragment mMapFragment;
    private EditText mSearch;
    private PopupWindow mPoiView;
    private ListView mPoiLvContent;
    private MapAdapter mMapAdapter;
    private String mKeyword;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        super.initView();
        setActionBarTitle("");
        setActionBarTitleColor(R.color.white);
        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.location_map);

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

        setActionBarTitleEnable(false);
        addActionBarCustomView(R.layout.view_map_search);
        mSearch = (EditText) findViewById(R.id.map_search);
        mSearch.addTextChangedListener(this);
        mSearch.setOnFocusChangeListener(this);
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

        Location lastLocation = mLocationClient.getLastLocation();
        mMapFragment.moveCamera(lastLocation);
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
                break;
            case R.id.menu_search:
                mMapFragment.doSearchQuery(mSearch.getText().toString().trim(), new MapFragment.onPoiSearchListener() {
                    @Override
                    public void onPoiSearch(String keyword , List<PoiItem> poiItems) {
                        if(mPoiView != null && mPoiView.isShowing()){
                            mPoiView.dismiss();
                        }
                        mMapFragment.addPoiOverlay(poiItems);
                    }

                    @Override
                    public void onFailure(int rCode) {
                        Log.d("Log_text", "MainActivity+onFailure+加载失败");
                    }
                });
                break;
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

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(TextUtils.isEmpty(s)){
            if(mPoiView != null && mPoiView.isShowing()){
                mPoiView.dismiss();
            }
            return;
        }
        mKeyword = s.toString().trim();
        mMapFragment.doSearchQuery(mKeyword,  new MapFragment.onPoiSearchListener() {
            @Override
            public void onPoiSearch(String keyword, final List<PoiItem> poiItems) {
                if(poiItems == null){
                    showToast("没有找到结果",android.R.drawable.sym_def_app_icon,Gravity.LEFT);
                    return ;
                }

                if(keyword != mKeyword){
                    return ;
                }

                if(mPoiView == null){
                    View menu = mInflater.inflate(R.layout.view_map_popupwindow, null, false);
                    mPoiLvContent = (ListView) menu.findViewById(R.id.popup_map_poi);
                    mPoiView = new PopupWindow(menu,mSearch.getMeasuredWidth(),800);
                    mPoiLvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            PoiItem item = (PoiItem) mMapAdapter.getItem(position);
                            mMapFragment.addPoiOverlay(item);
                            if(mPoiView != null && mPoiView.isShowing()){
                                mPoiView.dismiss();
                            }
                            mSearch.clearFocus();
                        }
                    });
                }

                mPoiView.showAsDropDown(mSearch);

                if(mMapAdapter == null){
                    mMapAdapter = new MapAdapter(poiItems, android.R.layout.simple_list_item_1);
                    mPoiLvContent.setAdapter(mMapAdapter);
                }else{
                    mMapAdapter.setData(poiItems);
                }

            }

            @Override
            public void onFailure(int rCode) {
                Log.d("Log_text", "MainActivity+onFailure" + rCode);
            }
        });

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(mSearch.isFocused() && !TextUtils.isEmpty(mSearch.getText().toString().trim())){
            mPoiView.showAsDropDown(mSearch);
        }else{
            WindowUtils.hideSoftKeyboard(mSearch);
        }
    }

    /**
     * 自定义infowinfow窗口
     */
    public void render(Marker marker, View view) {
        ((ImageView) view.findViewById(com.thirdparty.proxy.R.id.badge))
                .setImageResource(com.thirdparty.proxy.R.drawable.badge_sa);

        String title = marker.getTitle();
        TextView titleUi = ((TextView) view.findViewById(com.thirdparty.proxy.R.id.title));
        if (title != null) {
            SpannableString titleText = new SpannableString(title);
            titleText.setSpan(new ForegroundColorSpan(Color.BLACK), 0,
                    titleText.length(), 0);
            titleUi.setText(titleText);

        } else {
            titleUi.setText("");
        }
        String snippet = marker.getSnippet();
        TextView snippetUi = ((TextView) view.findViewById(com.thirdparty.proxy.R.id.snippet));
        if (snippet != null) {
            SpannableString snippetText = new SpannableString(snippet);
            snippetText.setSpan(new ForegroundColorSpan(Color.BLUE), 0,
                    snippetText.length(), 0);
            snippetUi.setText(snippetText);
        } else {
            snippetUi.setText("");
        }
    }

    public class MapAdapter extends BaseListViewAdapter<PoiItem>{

        private final List<PoiItem> mContent;

        public MapAdapter(List<PoiItem> content, int layoutId) {
            super(content, layoutId);
            mContent = content;
        }

        @Override
        public void setItemData(CommViewHolder holder, int position) {
            TextView tv = (TextView) holder.getViewById(android.R.id.text1);
            tv.setText(mContent.get(position).getTitle());
        }
    }
}
