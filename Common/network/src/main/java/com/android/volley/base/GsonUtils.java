package com.android.volley.base;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

public class GsonUtils {
    /**
     * @Title: toJson
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @return String 返回类型
     * @throws：
     */
    public static String toJson(Object bean){
        Gson gson=new GsonBuilder()
                .setDateFormat("yyyyMMddhhmmss")
                .create();
        return gson.toJson(bean);
    }

    public static String toJson(Object bean,Type type){
        Gson gson=new GsonBuilder()
                .setDateFormat("yyyyMMddhhmmss")
                .create();
        return gson.toJson(bean, type);
    }

    public static JSONObject toJsonObject(Object bean) throws JSONException {
        Gson gson=new GsonBuilder()
                .setDateFormat("yyyyMMddhhmmss")
                .create();
        return new JSONObject(gson.toJson(bean));
    }

    /**
     * @Title: fromJson
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param json
     * @param type
     * @return T 返回类型
     * @throws：
     */
    public static Object fromJson(String json,Type type){
        Gson gson=new GsonBuilder()
                .setDateFormat("yyyyMMddhhmmss")
                .create();
        return gson.fromJson(json, type);
    }

    /**
     * @Title: fromJson
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param json
     * @param classOfT
     * @return T 返回类型
     * @throws：
     */
    public  static <T>T fromJson(String json,Class<T> classOfT){
        Gson gson=new GsonBuilder()
                .setDateFormat("yyyyMMddhhmmss")
                .create();
        return gson.fromJson(json, classOfT);
    }
}