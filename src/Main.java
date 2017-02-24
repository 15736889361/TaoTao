/**
 * Created by fsy on 16-2-24.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;


public class Main {
    public static void main(String[] args) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfNowTime = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String qd_begin = sdf.format(new Date()) + " 00:00:00";
        String qd_end = sdf.format(new Date()) + " 08:30:00";
        String qt_begin = sdf.format(new Date()) + " 17:30:00";
        String qt_end = sdf.format((new Date())) + " 24:00:00";

        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdfNow.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String now = sdfNow.format(new Date());

        long nowMillis = sdfNow.parse(now).getTime();
        long qd_beginMillis = sdfNow.parse(qd_begin).getTime();
        long qd_endMillis = sdfNow.parse(qd_end).getTime();
        long qt_beginMillis = sdfNow.parse(qt_begin).getTime();
        long qt_endMillis = sdfNow.parse(qt_end).getTime();

        // 2017-02-21
        // 添加从文件中读取用户信息的功能
        // http://stackoverflow.com/questions/320542/how-to-get-the-path-of-a-running-jar-file
        String path = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        // 在 Linux下会包含 jar 程序的名称,所以去掉
        path = path.substring(0, path.lastIndexOf("/") + 1);

        List<String> userList = readTxtFile(path + "userlist.txt");
        int userCount = userList.size();
        Schemer[] ss = new Schemer[userCount];
        for(int i=0; i< userCount; i++){
            String[] userInfo = userList.get(i).split(",");
            ss[i] = new Schemer(userInfo[0],userInfo[1],userInfo[2]);
        }

        // 随机排列
        Schemer sTemp;
        int index;
        for(int i = 0; i < ss.length; i++){
            index = new Random().nextInt(ss.length);
            sTemp = ss[i];
            ss[i] = ss[index];
            ss[index] = sTemp;
        }

        if (nowMillis > qd_beginMillis && nowMillis < qd_endMillis)
        {
            System.out.println("日期:" + sdf.format(new Date()));
            for (Schemer s: ss)
            {
                int r = ThreadLocalRandom.current().nextInt(1, 100);
                TimeUnit.SECONDS.sleep(r);
                System.out.println(s.ClockIn() + ":" + s.getUsername() + " 时间:" + sdfNowTime.format(new Date()));
            }
            System.out.println();
        }
        else if (nowMillis > qt_beginMillis && nowMillis < qt_endMillis)
        {
            System.out.println("日期:" + sdf.format(new Date()));
            for (Schemer s: ss)
            {
                int r = ThreadLocalRandom.current().nextInt(1, 100);
                TimeUnit.SECONDS.sleep(r);
                System.out.println(s.ClockOut() + ":" + s.getUsername() + " 时间:" + sdfNowTime.format(new Date()));
            }
            System.out.println();
        }
        else
        {
            System.out.println("非签到/签退时间: " + now);
        }
    }

    /**
     * 功能：读取txt文件的内容
     * @param filePath
     * @return 返回文件内容,按行存放在 list 中.
     */
    public static List<String> readTxtFile(String filePath)
    {
        List<String> list = new ArrayList<String>();
        try
        {
            String encoding = "UTF-8";
            File file = new File(filePath);
            if (file.isFile() && file.exists())
            { // 判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;

                while ((lineTxt = bufferedReader.readLine()) != null)
                {
                    list.add(lineTxt);
                }
                bufferedReader.close();
                read.close();
            }
            else
            {
                System.out.println("找不到指定的文件");
            }
        }
        catch (Exception e)
        {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }

        return list;
    }
}


