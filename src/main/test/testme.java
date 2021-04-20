import cn.testmirai.service.Zhetaoke;
import net.mamoe.mirai.message.data.AtAll;
import net.mamoe.mirai.message.data.PlainText;
import org.json.JSONObject;
import org.junit.Test;

import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.*;
import cn.testmirai.Config;

public class testme {

    @Test
    public void queuePreTest(){
//        死循环中sleep(1)以降低cpu占用

        while(true){
            if (new Date().after(new Date(1918924508000L))){
                break;
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("跳出来了！！！");
    }

    @Test
    public void test09(){
        System.out.println("[mirai:atall]");
        System.out.println(AtAll.INSTANCE.serializeToMiraiCode());
        System.out.println(new PlainText("￥kqfFXcDc2Tp￥http://biadusakd.cc").serializeToMiraiCode());
        String pattern = "(http|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&:/~\\+#]*[\\w\\-\\@?^=%&/~\\+#])?";
        Pattern r = Pattern.compile(pattern);
        String line = "5.0测试，请无视本条消息\n" +
                "淘口令:￥kqfFXcDc2Tp￥/\n" +
                "淘链接:https://s.click.taobao.com/D06yIpu \n" +
                "---\n" +
                "店铺:kdv食品旗舰店";
        Matcher m = r.matcher(line);
        while (m.find()){
            System.out.println(m.group(0));
            //line = line.replace(m.group(0),"");
        }
        //System.out.println("----------"+line);
    }
    @Test
    public void test08(){
        List<Long> list = new ArrayList<>();
        list.add(123L);
        list.add(321L);
        System.out.println(list.toString());
    }

    @Test
    public void test07(){
        String s = "{\"930547859\":{\"639414477757\":\"1618385461097\"}}";
        JSONObject json = new JSONObject(s);
        JSONObject bin = json.getJSONObject("930547859");

        bin.put("123123123","321312312");
        json.put("930547859",bin);
        System.out.println(json);
    }

    @Test
    public void test06(){
        String a = "123";
        a.replace("1","2");
        System.out.println(a);

        System.out.println(AtAll.INSTANCE.serializeToMiraiCode());

        String s1 = new PlainText("淘口令:(ngCZXcQKrKO)/").serializeToMiraiCode();
        System.out.println(s1);
    }

    @Test
    public void test05(){
        /*Config config = new Config(Paths.get("C:\\Users\\15736\\Desktop\\Mirai\\config\\cn.testmirai.plugin"));
        List<Long> list = config.getRepostGroupList(123L);
        config.getRepostGroupList(123L);
        System.out.println(list);*/
    }

    @Test
    public void test04(){
        System.out.println(String.valueOf(1234567890L).getClass());
    }

    @Test
    public void test03(){
        String str = "{\"repost\":{\"123\":{\"111\":{\"cd\":5,\"at\":true,\"other config\":\"config\"},\"222\":{\"cd\":1},\"333\":{\"cd\":0}},\"213\":{\"111\":{\"cd\":5},\"222\":{\"cd\":1},\"333\":{\"cd\":0}},\"321\":{\"111\":{\"cd\":5},\"222\":{\"cd\":1},\"333\":{\"cd\":0}}}}";

        JSONObject json = new JSONObject(str);
        ArrayList<String> list = new ArrayList<>();
        JSONObject binJson = new JSONObject();

        Iterator<String> iterator = json.getJSONObject("repost").keys();
        while (iterator.hasNext()){
            System.out.println(iterator.next());
        }


       /* list.add("123");
        list.add("321");
        //list.contains();
        json.put("list",list);*/


    }

    @Test
    public void test02(){
        String pattern = "[^\\u4e00-\\u9fa5^\\w^\\s^@](\\w{10,12})[^\\u4e00-\\u9fa5^\\w^\\s^@](:)?(/)?(/)?";
        Pattern r = Pattern.compile(pattern);

        String line = "7.0\n" +
                "淘口令:(HGQOXcMXF9T)/\n" +
                "淘链接:https://s.click.taobao.com/aeBOGpu\n" +
                "93.99充100话费直接拍\n" +
                "---\n" +
                "店铺:话费中心之家\n";

        Matcher m = r.matcher(line);

        //System.out.println(m.toMatchResult());
        while (m.find()){
            System.out.println(m.groupCount());
        }
        m.reset();
        MatchResult matchResult = m.toMatchResult();
        m.reset();
        m.reset();
        if(m.find()) {
            System.out.println("0" + m.group());
            System.out.println("0" + m.group(0));
            System.out.println("1" + m.group(1).toString());
            System.out.println("2" + m.group(2));
            System.out.println("3" + m.group(3));
        }

    }

    @Test
    public void test01(){
        Zhetaoke zhetaoke = new Zhetaoke("2a1d258421bc49c0ba5e43523af083cc","27725", "mm_126672624_42842926_264750076");

        //System.out.println(zhetaoke.tkl2id("(27zxXcK0xDV)"));
        //System.out.println(zhetaoke.id2tkl("639414477757"));
        //System.out.println(zhetaoke.id2url("639414477757"));
        //System.out.println(zhetaoke.id2shop("639414477757"));
        int n =123;
        String bin = "(asdasd)\n" +
                "(asdasd)\n" +
                "123";
        System.out.println(n+"123");


        System.out.println(bin.replaceFirst(tranStr("(asdasd)"), "\n(33333)"));
    }

    public String tranStr(String str){

        String reg = "\\d+";
        String reg1 = "\\\\:";
        String reg2 = "\\\\+";
        String reg3 = "\\\\(";
        String reg4 = "\\\\)";
        String reg5 = "\\\\[";
        String reg6 = "\\\\]";
        String reg7 = "\\\\$";
        String reg8 = "\\\\*";
        String tmp = str.replaceAll("\\+",reg2);
        String tmp1 = tmp.replaceAll(reg,"\\\\d+");
        String tmp2 = tmp1.replaceAll("\\:",reg1);
        String tmp3 = tmp2.replaceAll("\\(",reg3);
        String tmp4 = tmp3.replaceAll("\\)",reg4);
        String tmp5 = tmp4.replaceAll("\\[",reg5);
        String tmp6 = tmp5.replaceAll("\\]",reg6);
        String tmp7 = tmp6.replaceAll("\\$",reg7);
        String tmp8 = tmp7.replaceAll("\\*",reg8);
        return tmp8;
    }
}
