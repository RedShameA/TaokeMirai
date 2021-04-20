package cn.testmirai.service;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.utils.MiraiLogger;

import java.util.List;



public class QQChecker extends Thread{
    MiraiLogger logger;
    public QQChecker(MiraiLogger logger){
        this.logger = logger;
    }
    @Override
    public void run() {
        while (true){
            logger.info("[线程任务]->开始检测QQ");
            List<Bot> bots = Bot.getInstances();
            int size = bots.size();
            if (size != 2 && size != 0 ){
                String strBotList = "";
                for (Bot bot:bots){
                    strBotList += String.valueOf(bot.getId())+"\n";
                }
                logger.info("[线程任务]->QQ在线列表出现问题,发送QQ信息提醒");
                try {
                    bots.get(0).getFriend(1573636056L).sendMessage("bot列表:\n"+strBotList+"请检查!");
                }catch (Exception e){
                    e.printStackTrace();
                }

            }else{
                logger.info("[线程任务]->QQ在线列表无问题");
            }
            try {
                Thread.sleep(60*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}

