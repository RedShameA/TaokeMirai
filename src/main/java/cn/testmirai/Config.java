package cn.testmirai;


import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class Config {
    MiraiLogger logger;

    String jsonString = "";
    JSONObject json = new JSONObject();
    String path = "";
    String repostJsonString = "";
    JSONObject repostJson = new JSONObject();
    String repostPath = "";


/*
repostJson设计
{
    "321被发送到的群": {
        "123123发送的商品id": "1231时间戳"
    }
}

*/



/*
Json 设计
{
    "other setting":""
    "repost": {
        "123": {
            "111": {
                "cd": 5,
                "at": true,
                "other config": "config"
            },
            "222": {
                "cd": 1
            },
            "333": {
                "cd": 0
            }
        },
        "213": {
            "111": {
                "cd": 5
            },
            "222": {
                "cd": 1
            },
            "333": {
                "cd": 0
            }
        },
        "321": {
            "111": {
                "cd": 5
            },
            "222": {
                "cd": 1
            },
            "333": {
                "cd": 0
            }
        }
    }
}


*/



    public Config(@NotNull Path ConfigFolderPath, MiraiLogger logger) {
        this.logger = logger;
        try {
            path = ConfigFolderPath.toString()+"\\config.json";
            repostPath = ConfigFolderPath.toString()+"\\repost.json";
            File file = new File(path);
            File repostFile = new File(repostPath);

            if (file.exists()){
                logger.info("[config文件加载]->config文件加载成功");
                BufferedReader reader = new BufferedReader(new FileReader(path));
                StringBuffer stringBuffer = new StringBuffer();
                String tmpStr = "";
                while ((tmpStr = reader.readLine()) != null){
                    stringBuffer.append(tmpStr);
                }
                reader.close();
                jsonString = stringBuffer.toString();
                json = new JSONObject(jsonString);
            }else{
                logger.info("[config文件加载]->未找到config文件");
            }
            if (repostFile.exists()){
                logger.info("[config文件加载]->repostConfig文件加载成功");
                BufferedReader reader = new BufferedReader(new FileReader(repostFile));
                StringBuffer stringBuffer = new StringBuffer();
                String tmpStr = "";
                while ((tmpStr = reader.readLine()) != null){
                    stringBuffer.append(tmpStr);
                }
                reader.close();
                repostJsonString = stringBuffer.toString();
                repostJson = new JSONObject(repostJsonString);
            }else{
                logger.info("[config文件加载]->未找到repostConfig文件");
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void saveConfig(){
        jsonString = json.toString();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            writer.write(jsonString);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void saveRepostConfig(){
        repostJsonString = repostJson.toString();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(repostPath));
            writer.write(repostJsonString);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void testsave(){
        json.put("testKey", "testValue");
        saveConfig();

    }
    public JSONObject testRet(){
        return repostJson;
    }
    public boolean needRepost(Long groupId){
        if (json.has("repost") && json.getJSONObject("repost").has(String.valueOf(groupId))){
            return true;
        }else {
            return false;
        }
    }
    public boolean needConvert(Long fatherGroupId, String id){
        String strFatherGroupId = String.valueOf(fatherGroupId);
        List<Long> repostGroupList = this.getAllRepostGroupList(fatherGroupId);
        for (Long sonGroupId: repostGroupList){
            if(this.getCd(fatherGroupId,sonGroupId,id)){
                //有一个就返回真
                return true;
            }
        }
        //都没有，返回假
        return false;
    }
    public List<Long> getNeedRepostGroupList(Long fatherGroupId, String id){
        //String strFatherGroupId = String.valueOf(fatherGroupId);
        List<Long> repostGroupList = this.getAllRepostGroupList(fatherGroupId);
        List<Long> ret = new ArrayList<>();
        for (Long sonGroupId: repostGroupList){
            if(this.getCd(fatherGroupId,sonGroupId,id)){
                //添加进list
                ret.add(sonGroupId);
            }
        }
        return ret;
    }
    public List<Long> getAllRepostGroupList(Long fatherGroupId){
        String strFatherGroupId = String.valueOf(fatherGroupId);
        List<Long> retList = new ArrayList<>();
        if(json.has("repost") && json.getJSONObject("repost").has(strFatherGroupId)){
            Iterator<String> iterator = json.getJSONObject("repost").getJSONObject(strFatherGroupId).keys();
            while (iterator.hasNext()){
                retList.add(Long.valueOf(iterator.next()));
            }
            return retList;
        }else{
            return retList;
        }

    }
    public boolean getCd(Long fatherGroupId, Long sonGroupId, String id){
        String strFatherGroupId = String.valueOf(fatherGroupId);
        String strSonGroupId = String.valueOf(sonGroupId);
        int cd = 0;
        if(json.has("repost") && json.getJSONObject("repost").has(strFatherGroupId) && json.getJSONObject("repost").getJSONObject(strFatherGroupId).has(strSonGroupId)){
            if (json.getJSONObject("repost").getJSONObject(strFatherGroupId).getJSONObject(strSonGroupId).has("cd")){
                cd = json.getJSONObject("repost").getJSONObject(strFatherGroupId).getJSONObject(strSonGroupId).getInt("cd");
            }else{
                cd = 0;
            }
        }

        boolean need = true;

        if (repostJson.has(strSonGroupId) && repostJson.getJSONObject(strSonGroupId).has(id)){
            Long timeStamp = Long.valueOf(repostJson.getJSONObject(strSonGroupId).getLong(id));
            Long now = new Date().getTime();
            Long cdTime = timeStamp + cd*60*60*1000L;
            if(now > cdTime){
                need = true;
            }else {
                need = false;
            }
        }
        return need;
    }
    public void saveRepostTime(Long sonGroupId, String id){
        String strSonGroupId = String.valueOf(sonGroupId);
        JSONObject binJson = new JSONObject();
        if (repostJson.has(strSonGroupId)){
            binJson = repostJson.getJSONObject(strSonGroupId);
        }
        binJson.put(id, String.valueOf(new Date().getTime()));

        repostJson.put(strSonGroupId,binJson);
        this.saveRepostConfig();
    }
    public boolean isAt(Long fatherGroupId, Long sonGroupId){
        String strFatherGroupId = String.valueOf(fatherGroupId);
        String strSonGroupId = String.valueOf(sonGroupId);
        boolean at = false;
        if(json.has("repost") && json.getJSONObject("repost").has(strFatherGroupId) && json.getJSONObject("repost").getJSONObject(strFatherGroupId).has(strSonGroupId) && json.getJSONObject("repost").getJSONObject(strFatherGroupId).getJSONObject(strSonGroupId).has("at")){
            at = json.getJSONObject("repost").getJSONObject(strFatherGroupId).getJSONObject(strSonGroupId).getBoolean("at");
        }
        return at;
    }
}
