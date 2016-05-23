package com.android.volley.toolbox;

import android.util.Log;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

public class GsonUtils {
    /**
     * @return String 返回类型
     * @Title: toJson
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @throws：
     */
    public static String toJson(Object bean) {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyyMMddhhmmss")
                .create();
        return gson.toJson(bean);
    }

    public static String toJson(Object bean, Type type) {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyyMMddhhmmss")
                .create();
        return gson.toJson(bean, type);
    }

    public static JSONObject toJsonObject(Object bean) throws JSONException {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyyMMddhhmmss")
                .create();
        return new JSONObject(gson.toJson(bean));
    }

    /**
     * @param json
     * @param type
     * @return T 返回类型
     * @Title: fromJson
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @throws：
     */
    public static Object fromJson(String json, Type type) {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyyMMddhhmmss")
                .create();
        return gson.fromJson(json, type);
    }

    /**
     * @param json
     * @param classOfT
     * @return T 返回类型
     * @Title: fromJson
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @throws：
     */
    public static <T> T fromJson(String json, Class<T> classOfT) {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyyMMddhhmmss")
                .create();
        return gson.fromJson(json, classOfT);
    }


    public static String toJson(Map<String, Object> map) {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyyMMddhhmmss")
                .create();
        return gson.toJson(map);
    }

    public static Map<String, Object> fromJson(String json){
        Gson gson = new GsonBuilder().create();
        Type type = new TypeToken() {}.getType();
        HashMap map = (HashMap)gson.fromJson(json, type);
        return map;
    }

}