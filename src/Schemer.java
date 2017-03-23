/**
 * Created by fsy on 3/2/16.
 */

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Schemer {
    private String username;
    private String password;
    private String imei;

    public Schemer(String username, String password, String imei) {
        this.username = username;
        this.password = password;
        this.imei = imei;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public  String getImei() { return imei; }

    public void setImei(String imei) { this.imei = imei; }

    public String Login() throws IOException, NoSuchAlgorithmException, KeyManagementException, InterruptedException {
        String strLogin = "";

        // 2017-02-21
        // 调用 http 请求有时候会发生异常,重试3次
        int count = 3;
        do {
            // String strLogin = Http.send("https://121.42.207.18:6443/tt/app/appLogin",getLoginParameter(this.getUsername(), this.getPassword()));
            strLogin = Http.send("https://www.globetoc.com:6442/tt/app/appLogin", getLoginParameter(this.getUsername(), this.getPassword()));

            if (strLogin.equals("http_error"))
                TimeUnit.SECONDS.sleep(8);

        } while (strLogin.equals("http_error") && count-- > 0);

        JSONObject obj = new JSONObject(strLogin);
        String currentOpenId = obj.getJSONObject("data").getString("currentOpenId");
        String isLoginSuccess = obj.getString("msg");

        //if (isLoginSuccess.equals("登陆成功")){
        if(isLoginSuccess.indexOf("登陆成功")!=-1){
            return currentOpenId;
        } else {
            return "登录失败";
        }
    }

    public String ClockIn() throws ParseException, IOException, NoSuchAlgorithmException, KeyManagementException, InterruptedException {
        String currentOpenId = Login();
        String strQD = "";
        int count = 3;
        if (!currentOpenId.equals("登录失败")){
            do {
                strQD = Http.send("https://www.globetoc.com:6442/tt/app/ms/signin/doSignInV2", getQDParameter(currentOpenId, this.getImei()));

                if (strQD.equals("http_error"))
                    TimeUnit.SECONDS.sleep(8);

            } while (strQD.equals("http_error") && count-- > 0);

            JSONObject jsonQD = new JSONObject(strQD);
            String isQDSuccess = jsonQD.getString("msg");

            if(isQDSuccess.indexOf("签到成功")!=-1){
                return "签到成功";
            }
            else{
                return "签到失败: " + isQDSuccess;
            }
        } else {
            return "登录失败..无法签到";
        }
    }

    public String ClockOut() throws ParseException, IOException, NoSuchAlgorithmException, KeyManagementException, InterruptedException {
        String currentOpenId = Login();
        String strQT = "";
        int count = 3;
        if (!currentOpenId.equals("登录失败")){
            do {
                // String strQT =  Http.send("https://www.globetoc.com:6442/tt/app/ms/signin/doSignIn",getQTParameter(currentOpenId, this.getImei()));
                strQT = Http.send("https://www.globetoc.com:6442/tt/app/ms/signin/doSignInV2", getQTParameter(currentOpenId, this.getImei()));

                if (strQT.equals("http_error"))
                    TimeUnit.SECONDS.sleep(8);

            } while (strQT.equals("http_error") && count-- > 0);
            JSONObject jsonQT = new JSONObject(strQT);
            String isQTSuccess = jsonQT.getString("msg");

            if(isQTSuccess.indexOf("签退成功")!=-1){
                return "签退成功";
            }
            else{
                return "签退失败: " + isQTSuccess;
            }
        } else {
            return "登录失败..无法签退";
        }

    }

    public String getLoginParameter(String UserName, String Password) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        HashMap localHashMap = new HashMap();
        StringBuilder localStringBuilder = new StringBuilder();

        localHashMap.put("openId",UserName);
        localHashMap.put("pw",Common.getMD5Str(Password));
        localHashMap.put("ts",String.valueOf(System.currentTimeMillis()));
        localHashMap.put("un",UserName);

        Map tempHashMap = Common.generateSecureMap(localHashMap);

        Object localObject = new ArrayList();
        ((List)localObject).addAll(tempHashMap.keySet());
        Collections.sort((List)localObject, new Common.SpellComparator());
        localObject = ((List)localObject).iterator();
        while (((Iterator)localObject).hasNext())
        {
            String str = (String)((Iterator)localObject).next();
            localStringBuilder.append("&").append(str).append("=").append(String.valueOf(tempHashMap.get(str)));
        }
        if (localStringBuilder.length() > 0)
            localStringBuilder.deleteCharAt(0);

        return localStringBuilder.toString();
    }

    public String getQDParameter(String openId, String imei) throws ParseException, UnsupportedEncodingException, NoSuchAlgorithmException {
        HashMap localHashMap = new HashMap();
        StringBuilder localStringBuilder = new StringBuilder();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String dt = sdf.format(new Date()) + " 07:00:00";
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf1.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        long millis = sdf1.parse(dt).getTime();

        localHashMap.put("addrId_","65b4bfefae054543bccf1226fc779051");
        localHashMap.put("hf_qd_site_","117.258997,39.117814");
        localHashMap.put("hf_qd_time_","08:30");
        localHashMap.put("imei_",imei);
        localHashMap.put("orgId","51a1ba42ca47473ea2551d2eb290fc19");
        localHashMap.put("record_site_","117.260074,39.118503");
        localHashMap.put("record_time_",sdf1.format(millis));
        localHashMap.put("signFlag","qd");
        localHashMap.put("tip_","");
        localHashMap.put("ts",String.valueOf(millis));
        localHashMap.put("openId",openId);
        // doSignInV2需要的新参数 2016-11-26 by fsy
        localHashMap.put("kqDefId","65b4bfefae054543bccf1226fc779051");
        localHashMap.put("record_type_","qd");
        localHashMap.put("vt",sdf.format(new Date()));

        Map tempHashMap = Common.generateSecureMap(localHashMap);

        Object localObject = new ArrayList();
        ((List)localObject).addAll(tempHashMap.keySet());
        Collections.sort((List)localObject, new Common.SpellComparator());
        localObject = ((List)localObject).iterator();
        while (((Iterator)localObject).hasNext())
        {
            String str = (String)((Iterator)localObject).next();
            localStringBuilder.append("&").append(str).append("=").append(String.valueOf(tempHashMap.get(str)));
        }
        if (localStringBuilder.length() > 0)
            localStringBuilder.deleteCharAt(0);

        return localStringBuilder.toString();
    }

    public String getQTParameter(String openId, String imei) throws ParseException, UnsupportedEncodingException, NoSuchAlgorithmException {
        HashMap localHashMap = new HashMap();
        StringBuilder localStringBuilder = new StringBuilder();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String dt = sdf.format(new Date()) + " 19:00:00";
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf1.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        long millis = sdf1.parse(dt).getTime();

        localHashMap.put("addrId_","65b4bfefae054543bccf1226fc779051");
        localHashMap.put("hf_qt_site_","117.258997,39.117814");
        localHashMap.put("hf_qt_time_","17:30");
        localHashMap.put("imei_",imei);
        localHashMap.put("orgId","51a1ba42ca47473ea2551d2eb290fc19");
        localHashMap.put("record_site_","117.260074,39.118503");
        localHashMap.put("record_time_",sdf1.format(millis));
        localHashMap.put("signFlag","qt");
        localHashMap.put("tip_","");
        localHashMap.put("ts",String.valueOf(millis));
        localHashMap.put("openId",openId);
        // doSignInV2需要的新参数 2016-11-26 by fsy
        localHashMap.put("kqDefId","65b4bfefae054543bccf1226fc779051");
        localHashMap.put("record_type_","qt");
        localHashMap.put("vt",sdf.format(new Date()));

        Map tempHashMap = Common.generateSecureMap(localHashMap);

        Object localObject = new ArrayList();
        ((List)localObject).addAll(tempHashMap.keySet());
        Collections.sort((List)localObject, new Common.SpellComparator());
        localObject = ((List)localObject).iterator();
        while (((Iterator)localObject).hasNext())
        {
            String str = (String)((Iterator)localObject).next();
            localStringBuilder.append("&").append(str).append("=").append(String.valueOf(tempHashMap.get(str)));
        }
        if (localStringBuilder.length() > 0)
            localStringBuilder.deleteCharAt(0);

        return localStringBuilder.toString();
    }
}
