package cn.testmirai;

import cn.testmirai.Config;
import cn.testmirai.service.QQChecker;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.Mirai;
import net.mamoe.mirai.console.MiraiConsole;
import net.mamoe.mirai.console.extension.PluginComponentStorage;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.contact.Stranger;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.*;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.*;

public final class Plugin extends JavaPlugin {
    public static final Plugin INSTANCE = new Plugin();
    Config config;
    Taoke taoke;
    Long zfqq = 2655642212L;

    private Plugin() {
        super(new JvmPluginDescriptionBuilder(
                "cn.redsa.tkhfcj",
                "1.0-SNAPSHOT")
                .author("reda")
                .info("just for test")
                .name("淘客话费采集")
                .build()
        );
    }


    @Override
    public void onEnable() {
        getLogger().info("[插件加载]->插件加载成功");
//        GlobalEventChannel.INSTANCE.subscribeAlways(MemberJoinRequestEvent.class, (MemberJoinRequestEvent event) -> {
//            event.g
//
//        });
//        GlobalEventChannel.INSTANCE.subscribeAlways(NewFriendRequestEvent.class, event -> {
//            event.g
//
//        });

        GlobalEventChannel.INSTANCE.subscribeAlways(FriendMessageEvent.class, (FriendMessageEvent event)->{
            if (event.getFriend().getId() == 1573636056L){
                String message = event.getMessage().contentToString().trim();
                if (message.equals("test")){
                    event.getSender().sendMessage(config.testRet().toString());
                }else if (message.startsWith("lv")){
                    Long qid = Long.valueOf(message.replace("lv",""));
                    int level = Mirai.getInstance().queryProfile(event.getBot(), qid).getQLevel();
                    event.getSender().sendMessage("level:"+level);
                }else if(message.equals("list")){
                    String strBotList = "";
                    List<Bot> botList = new ArrayList<>();
                    botList = Bot.getInstances();
                    for (Bot bot:botList){
                        strBotList += String.valueOf(bot.getId()) + "\n";
                    }
                    event.getSender().sendMessage(strBotList);
                }


            }
        });
        GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, (GroupMessageEvent event) -> {
            if (event.getBot().getId() != zfqq){
                taoke.handleMessage(event,config,getLogger());
            }else{
                //是转发qq收到消息
                if (event.getGroup().getId() == 429619359L){
                    //来自那个群聊
                    getLogger().info("[007]->007转发");
                    //发送到007
                    //去掉@全体成员 防止转发失败
                    event.getBot().getGroup(724387953L).sendMessage(MiraiCode.deserializeMiraiCode(event.getMessage().serializeToMiraiCode().replace(AtAll.INSTANCE.serializeToMiraiCode(),"")));
                }
            }



            //if (event.getGroup().getId() == 930547859L && event.getSender().getId()==1573636056L){
            //event.getGroup().sendMessage(content);
            //event.getGroup().sendMessage(contentSerialize);
            //event.getGroup().sendMessage("@me了");
            //MessageChain deSerializedChain = MiraiCode.deserializeMiraiCode(contentSerialize);
            //event.getGroup().sendMessage(deSerializedChain);
            //getLogger().info(contentSerialize);
            //};

        });
    }

    @Override
    public void onDisable() {
        super.onDisable();
        getLogger().info("[插件加载]->插件被禁用");

    }

    @Override
    public void onLoad(@NotNull PluginComponentStorage $this$onLoad) {
        super.onLoad($this$onLoad);
        getLogger().info("[插件加载]->插件正在加载");
        config = new Config(getConfigFolderPath(),getLogger());
        taoke = new Taoke("2a1d258421bc49c0ba5e43523af083cc","27725","mm_126672624_42842926_264750076",getLogger());
        QQChecker qqChecker = new QQChecker(getLogger());
        qqChecker.start();
        getLogger().info("[启动线程]->线程ID:"+qqChecker.getId());
        //config.testsave();
        //getLogger().info("testing 插件文件夹");
    }


}