package com.example.l.common.activity;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.l.common.R;
import com.example.l.common.widget.PullReFreshView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author:dongpo 创建时间: 5/30/2016
 * 描述:
 * 修改:
 */
public class PullActivity extends AppCompatActivity {

    @Bind(R.id.pull_refresh)
    PullReFreshView mPullReFreshView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pull_layout);
        ButterKnife.bind(this);

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            list.add(i+"");
        }

        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list));
        mPullReFreshView.setPullRefreshEnable(true);
        mPullReFreshView.setOnRefreshListener(new PullReFreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("Log_text", "PullActivity+onRefresh+下拉刷新了");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(3000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mPullReFreshView.complete();
                            }
                        });
                    }
                }).start();
            }
        });
    }

    public void refresh(View view){

    }
}
