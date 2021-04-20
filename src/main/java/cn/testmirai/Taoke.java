package cn.testmirai;

import cn.testmirai.service.Zhetaoke;
import cn.testmirai.Config;
import net.mamoe.mirai.console.util.MessageUtils;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.AtAll;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.utils.MiraiLogger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Taoke extends Zhetaoke {

    private Long time1;
    private Long time2;

    public Taoke(String appkey, String sid, String pid, MiraiLogger logger) {
        super(appkey, sid, pid);
        logger.info("[加载淘客类]->加载成功");
    }

    public void handleMessage(GroupMessageEvent event, Config config, MiraiLogger logger) {
        String pattern = "[^\\u4e00-\\u9fa5^\\w^\\s^@](\\w{10,12})[^\\u4e00-\\u9fa5^\\w^\\s^@](:)?(/)?(/)?";
        String pattern_url = "(http|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&:/~\\+#]*[\\w\\-\\@?^=%&/~\\+#])?";
        Pattern r = Pattern.compile(pattern);
        Pattern r_url = Pattern.compile(pattern_url);
        String content = event.getMessage().contentToString();
        String contentSerialize = event.getMessage().serializeToMiraiCode();
        Long groupId = event.getGroup().getId();

        if (config.needRepost(groupId)) {
            logger.info("[检测群]->群在config，开始转发步骤");

            Matcher m_url = r_url.matcher(content);
            logger.info("[消息处理]->去除URL(防止误识别为淘口令)");
            while(m_url.find()){
                logger.info("[消息处理]->删去"+m_url.group(0));
                content = content.replace(m_url.group(0),"");
                contentSerialize = contentSerialize.replace(new PlainText(m_url.group(0)).serializeToMiraiCode(),"");
            }
            logger.info("[消息处理]->去除@全体成员(防止浪费次数)");
            if (contentSerialize.contains(AtAll.INSTANCE.serializeToMiraiCode())){
                logger.info("[消息处理]->找到了@全体成员(开始去除)");
            }else{
                logger.info("[消息处理]->未找到@全体成员");
            }
            contentSerialize = contentSerialize.replace(AtAll.INSTANCE.serializeToMiraiCode(),"");

            Matcher m = r.matcher(content);

            int sum = 0;
            while (m.find()) {
                sum += 1;
            }
            m.reset();
            if (sum == 0) {
                //do nothing
                logger.info("[检测淘口令]->检测到0个淘口令(需要转发，但不是淘口令消息)");
            } else if (sum == 1) {
                logger.info("[检测淘口令]->检测到1个淘口令，开始转发");

                //need repost
                Long fatherGroupId = groupId;
                //event.getBot().getFriend(1573636056L).sendMessage(list.toString());

                //prepare message
                //event.getGroup().sendMessage(content);
                //event.getGroup().sendMessage(String.valueOf(m.groupCount()));
                String id = "";
                String tkl_old = "";
                m.reset();
                if (m.find()) {
                    tkl_old = m.group(0);
                    //time1 = new Date().getTime();
                    id = this.tkl2id(m.group(1));
                    logger.info("[转换淘口令到ID]->ID:" + id);
                    if (id.equals("")) {
                        logger.info("[转换淘口令到ID]->失败，跳过转发");
                        return;
                    }
                    //time2 = new Date().getTime();
                    //logger.info("转ID花了:" + String.valueOf(time2-time1));
                }
                if (config.needConvert(fatherGroupId, id)) {
                    logger.info("[检查是否需要转换ID到淘口令]->需要,继续");
                    String tkl_new = this.id2tkl(id) + "/";
                    logger.info("[转换ID到淘口令]->淘口令:" + tkl_new.replace("/", ""));
                    String url = this.id2url(id);
                    logger.info("[转换ID到淘链接]->淘链接:" + url);
                    String replace_old = new PlainText(tkl_old).serializeToMiraiCode();
                    String replace_new = new PlainText("淘口令:" + tkl_new + "\n淘链接:" + url).serializeToMiraiCode();
                    contentSerialize = contentSerialize.replace(replace_old, replace_new);
                    //to doing
                    //check start with
                    if (!Character.isDigit(contentSerialize.charAt(0))) {
                        contentSerialize = "5.0" + contentSerialize;
                    }
                    //添加店铺信息
                    contentSerialize = contentSerialize + new PlainText("\n---\n店铺:" + this.id2shop(id)).serializeToMiraiCode();
                } else {
                    logger.info("[检查是否需要转换ID到淘口令]->不需要,跳过");
                }

                MessageChain deSerializedChain = MiraiCode.deserializeMiraiCode(contentSerialize);
                List<Long> list = config.getNeedRepostGroupList(groupId, id);
                for (Long sonGroupId : list) {
                    //check cd
                    boolean cd = config.getCd(fatherGroupId, sonGroupId, id);
                    if (cd) {
                        //ready to repost
                        logger.info("[转发单条淘口令]->开始发送" + sonGroupId);
                        event.getBot().getGroup(sonGroupId).sendMessage(deSerializedChain);
                        //save cd
                        config.saveRepostTime(sonGroupId, id);
                        //最后再at全员，防止报错接下来就不执行 // 已用try解决
                        if (config.isAt(fatherGroupId, sonGroupId)) {
                            try {
                                event.getBot().getGroup(sonGroupId).sendMessage(AtAll.INSTANCE);
                            } catch (Exception e) {
                                logger.info("[转发单条淘口令]->无at全体成员次数");
                            }
                        }
                    } else {
                        logger.info("[检测CD]->CD未到");
                    }
                }


            } else {
                logger.info("[检测淘口令]->检测到多个淘口令(多条淘口令，直接转发)");
                //repost all
                Long fatherGroupId = groupId;
                List<Long> list = config.getAllRepostGroupList(fatherGroupId);
                //prepare message
                int count = 0;
                while (m.find()) {
                    count++;
                    String id = this.tkl2id(m.group(1));
                    logger.info("[转换淘口令到ID]->ID:" + id);
                    if (id.equals("")) {
                        logger.info("[转换淘口令到ID]->失败，跳过本次");
                        continue;
                    }
                    logger.info("[检查是否需要转换ID到淘口令]->多个淘口令需要转换,继续");
                    String tkl_old = m.group(0);
                    String tkl_new = this.id2tkl(id) + "/";
                    logger.info("[转换ID到淘口令]->淘口令:" + tkl_new.replace("/", ""));
                    String url = this.id2url(id);
                    logger.info("[转换ID到淘链接]->淘链接:" + url);
                    String replace_old = new PlainText(tkl_old).serializeToMiraiCode();
                    String replace_new = new PlainText("淘口令:" + tkl_new + "\n淘链接:" + url).serializeToMiraiCode();
                    contentSerialize = contentSerialize.replace(replace_old, replace_new);
                    //to doing
                    //check start with
                    if (!Character.isDigit(contentSerialize.charAt(0))) {
                        contentSerialize = "5.0" + contentSerialize;
                    }
                    //添加店铺信息
                    contentSerialize = contentSerialize + new PlainText("\n---\n" + count + "店铺:" + this.id2shop(id)).serializeToMiraiCode();
                }
                MessageChain deSerializedChain = MiraiCode.deserializeMiraiCode(contentSerialize);
                for (Long sonGroupId : list) {
                    //don't need to check cd
                    //so always ready to repost
                    logger.info("[转发多条淘口令]->开始发送" + sonGroupId);
                    event.getBot().getGroup(sonGroupId).sendMessage(deSerializedChain);
                    //won't save the id
                }
            }
        } else {
            logger.info("[检测群]->群不在config，跳过(不需要转发)");
        }


    }



    private String tranStr(String str) {

        String reg = "\\d+";
        String reg1 = "\\\\:";
        String reg2 = "\\\\+";
        String reg3 = "\\\\(";
        String reg4 = "\\\\)";
        String reg5 = "\\\\[";
        String reg6 = "\\\\]";
        String reg7 = "\\\\$";
        String reg8 = "\\\\*";
        String tmp = str.replaceAll("\\+", reg2);
        String tmp1 = tmp.replaceAll(reg, "\\\\d+");
        String tmp2 = tmp1.replaceAll("\\:", reg1);
        String tmp3 = tmp2.replaceAll("\\(", reg3);
        String tmp4 = tmp3.replaceAll("\\)", reg4);
        String tmp5 = tmp4.replaceAll("\\[", reg5);
        String tmp6 = tmp5.replaceAll("\\]", reg6);
        String tmp7 = tmp6.replaceAll("\\$", reg7);
        String tmp8 = tmp7.replaceAll("\\*", reg8);
        return tmp8;
    }

}
