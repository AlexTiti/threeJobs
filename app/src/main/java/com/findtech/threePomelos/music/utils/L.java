package com.findtech.threePomelos.music.utils;

import android.text.TextUtils;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by JRD on 2017/2/27.
 */
public class L {

    private  static boolean isDebug = true;
    private static String tags = "TAG";

  public static void e(String tag ,String msg){
      if (!isDebug)return;
      tag = getTag(tag);
      StackTraceElement stackTraceElement = getTargetStackTraceElement();
      Log.e(tag,"("+stackTraceElement.getFileName()+":"+stackTraceElement.getLineNumber()+")");
      Log.e(tag,msg);
  }

    public static void e(String msg){
        if (!isDebug)return;
        StackTraceElement stackTraceElement = getTargetStackTraceElement();
        Log.e(tags,"("+stackTraceElement.getFileName()+":"+stackTraceElement.getLineNumber()+")");
        Log.e(tags,msg);

    }


    public static void d(String tag ,String msg){
        if (!isDebug)return;
        tag = getTag(tag);
        StackTraceElement stackTraceElement = getTargetStackTraceElement();
        Log.d(tag,"("+stackTraceElement.getFileName()+":"+stackTraceElement.getLineNumber()+")");
        Log.d(tag,msg);

    }

    public static void i(String tag ,String msg){
        if (!isDebug)return;
        tag = getTag(tag);
        StackTraceElement stackTraceElement = getTargetStackTraceElement();
        Log.i(tag,"("+stackTraceElement.getFileName()+":"+stackTraceElement.getLineNumber()+")");
        Log.i(tag,msg);
    }

    private static String getTag(String tag){

        if (TextUtils.isEmpty(tag))
            return tags;
        return tag;
    }


    private static void eJSON(String tag, String json){
        tag = getTag(tag);
        StackTraceElement stackTraceElement = getTargetStackTraceElement();
        Log.e(tag,"("+stackTraceElement.getFileName()+":"+stackTraceElement.getLineNumber()+")");
        Log.e(tag,getJSON(json));

    }

    private static void dJSON(String tag, String json){
        tag = getTag(tag);
        StackTraceElement stackTraceElement = getTargetStackTraceElement();
        Log.e(tag,"("+stackTraceElement.getFileName()+":"+stackTraceElement.getLineNumber()+")");
        Log.e(tag,getJSON(json));

    }

    private static void iJSON(String tag, String json){
        tag = getTag(tag);
        StackTraceElement stackTraceElement = getTargetStackTraceElement();
        Log.e(tag,"("+stackTraceElement.getFileName()+":"+stackTraceElement.getLineNumber()+")");
        Log.e(tag,getJSON(json));

    }

    private static String getJSON(String json){
        json = json.trim();
        if (json.startsWith("{")){
            try {
                JSONObject jsonObject = new JSONObject(json);
                return jsonObject.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (json.startsWith("[")){
            try {
                JSONArray jsonArray = new JSONArray(json);
                return jsonArray.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return json;

    }

    private static StackTraceElement getTargetStackTraceElement() {
        // find the target invoked method
        StackTraceElement targetStackTrace = null;
        boolean shouldTrace = false;
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            boolean isLogMethod = stackTraceElement.getClassName().equals(L.class.getName());
            if (shouldTrace && !isLogMethod) {
                targetStackTrace = stackTraceElement;
                break;
            }
            shouldTrace = isLogMethod;
        }
        return targetStackTrace;
    }









}
