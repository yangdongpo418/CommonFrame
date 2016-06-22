package com.example.l.common.business.text.activity;

import android.util.Log;

import com.example.l.common.R;
import com.example.l.common.base.BaseActivity;

import java.lang.reflect.Field;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author:dongpo 创建时间: 6/17/2016
 * 描述:
 * 修改:
 */
public class RxActivity extends BaseActivity {

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
//        Observable 被观察
//        Observer 观察者
        final Subscriber<StringBuffer> observer = new Subscriber<StringBuffer>() {
            @Override
            public void onCompleted() {
                Log.d("Log_text", "RxActivity+onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.d("Log_text", "RxActivity+onError");
            }

            @Override
            public void onNext(StringBuffer s) {
                String name = Thread.currentThread().getName();
                Log.d("Log_text", "RxActivity+call" + name);
            }

            @Override
            public void onStart() {
                String name = Thread.currentThread().getName();
                Log.d("Log_text", "RxActivity+onStart" + name);
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                Observable.create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        Log.d("Log_text", "RxActivity+OnSubscribe" + subscriber.getClass().getName());
                        String name = Thread.currentThread().getName();
                        Log.d("Log_text", "RxActivity+OnSubscribe" + name);
                        subscriber.onStart();
                        subscriber.onNext("测试结果");
                        subscriber.onCompleted();
                    }
                }).subscribeOn(Schedulers.io())
                        .doOnSubscribe(new Action0() {
                            @Override
                            public void call() {
                                String name = Thread.currentThread().getName();
                                Log.d("Log_text", "RxActivity+doOnSubscribe1" + name);
                            }
                        })
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(new Action0() {
                            @Override
                            public void call() {
                                String name = Thread.currentThread().getName();
                                Log.d("Log_text", "RxActivity+doOnSubscribe2" + name);
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .map(new Func1<String, StringBuffer>() {
                            @Override
                            public StringBuffer call(String s) {
                                Log.d("Log_text", "RxActivity+map" + Thread.currentThread().getName());
                                return new StringBuffer(s + "测试通过");
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(observer);
            }
        }).start();
    }


    public String getFieldName(Observable observable) {
        try {
            Class<?> aClass = observable.getClass();
            Field[] onSubscribe = aClass.getDeclaredFields();
            for (Field field : onSubscribe) {
                if (field.getName().equals("onSubscribe")) {
                    Observable.OnSubscribe o = (Observable.OnSubscribe) field.get(observable);
                    return o.getClass().getSimpleName();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_rx;
    }
}
