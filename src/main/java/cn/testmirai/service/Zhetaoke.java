package cn.testmirai.service;

import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;

public class Zhetaoke {
    private String appkey;
    private String sid;
    private String pid;
    OkHttpClient client = new OkHttpClient();


    public Zhetaoke(String appkey, String sid, String pid) {
        this.appkey = appkey;
        this.sid = sid;
        this.pid = pid;
    }

    public String id2shop(String id){
        String retShop = "";
        String url = "https://api.zhetaoke.com:10001/api/open_gaoyongzhuanlian.ashx";

        RequestBody requestBody = new FormBody.Builder()
                .add("appkey",appkey)
                .add("sid",sid)
                .add("pid",pid)
                .add("num_iid",id)
                .add("signurl","4")
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            String ret = response.body().string();
            JSONObject retJson = new JSONObject(ret);

            if (retJson.has("tbk_privilege_get_response")){
                retShop = retJson
                        .getJSONObject("tbk_privilege_get_response")
                        .getJSONObject("result")
                        .getJSONObject("data")
                        .getString("nick");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return retShop;
    }

    public String id2bin(String id){
        String retStr = "";
        String url = "https://api.zhetaoke.com:10001/api/open_gaoyongzhuanlian.ashx";

        RequestBody requestBody = new FormBody.Builder()
                .add("appkey",appkey)
                .add("sid",sid)
                .add("pid",pid)
                .add("num_iid",id)
                .add("signurl","4")
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            String ret = response.body().string();

            retStr = ret;
        } catch (IOException e) {
            e.printStackTrace();
        }


        return retStr;
    }

    public String id2url(String id){
        String retUrl = "";
        String url = "https://api.zhetaoke.com:10001/api/open_gaoyongzhuanlian.ashx";

        RequestBody requestBody = new FormBody.Builder()
                .add("appkey",appkey)
                .add("sid",sid)
                .add("pid",pid)
                .add("num_iid",id)
                .add("signurl","4")
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            String ret = response.body().string();
            JSONObject retJson = new JSONObject(ret);

            if (retJson.has("tbk_privilege_get_response")){
                retUrl = retJson
                        .getJSONObject("tbk_privilege_get_response")
                        .getJSONObject("result")
                        .getJSONObject("data")
                        .getString("shorturl");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return retUrl;
    }

    public String id2tkl(String id){
        String tkl = "";
        String url = "https://api.zhetaoke.com:10001/api/open_gaoyongzhuanlian.ashx";

        RequestBody requestBody = new FormBody.Builder()
                .add("appkey",appkey)
                .add("sid",sid)
                .add("pid",pid)
                .add("num_iid",id)
                .add("signurl","4")
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String ret = response.body().string();
            JSONObject retJson = new JSONObject(ret);

            if (retJson.has("tbk_privilege_get_response")){
                tkl = retJson
                        .getJSONObject("tbk_privilege_get_response")
                        .getJSONObject("result")
                        .getJSONObject("data")
                        .getString("tkl");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return tkl;
    }

    public String tkl2id(String tkl){
        String id = "";
        String url = "https://api.zhetaoke.com:10001/api/open_shangpin_id.ashx";

        RequestBody requestBody = new FormBody.Builder()
                .add("appkey",appkey)
                .add("sid",sid)
                .add("content",tkl)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            String ret = response.body().string();
            JSONObject retJson = new JSONObject(ret);
            if (retJson.has("item_id")){
                id = retJson.getString("item_id");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return id;
    }
}
