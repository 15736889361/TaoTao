/**
 * Created by fsy on 3/2/16.
 */

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Common {

    public static String getMD5Str(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        return DatatypeConverter.printHexBinary(MessageDigest.getInstance("MD5").digest(str.getBytes("UTF-8"))).toLowerCase();
    }

    public static void addBasicParamForWoo(Map paramMap, String openId)
    {

        paramMap.put("ts", String.valueOf(System.currentTimeMillis()));
        paramMap.put("openId", openId);
    }

    public static Map<String, String> generateSecureMap(Map<String, String> paramMap) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        paramMap.put("sig", generateSecureURL(paramMap).get(1));
        return paramMap;
    }

    public static Map<String, String> generateSecureMapForWoo(Map<String, String> paramMap, String openId) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        addBasicParamForWoo(paramMap,openId);
        paramMap.put("sig", generateSecureURL(paramMap).get(1));
        return paramMap;
    }

    private static List<String> generateSecureURL(Map<String, String> paramMap) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        ArrayList localArrayList = new ArrayList();
        Object localObject = new ArrayList();
        ((List)localObject).addAll(paramMap.keySet());
        Collections.sort((List)localObject, new SpellComparator());
        StringBuilder localStringBuilder = new StringBuilder();
        localObject = ((List)localObject).iterator();
        while (((Iterator)localObject).hasNext())
        {
            String str = (String)((Iterator)localObject).next();
            localStringBuilder.append("&").append(str).append("=").append(URLEncoder.encode(String.valueOf(paramMap.get(str)),java.nio.charset.StandardCharsets.UTF_8.toString()));
        }
        if (localStringBuilder.length() > 0)
            localStringBuilder.deleteCharAt(0);
        localArrayList.add(localStringBuilder.toString());
        localArrayList.add(signature(paramMap));
        return (List<String>)localArrayList;
    }

    public static String getTokenKey()
    {
        return "1234abcD";
    }

    public static String signature(Map<String, ?> paramMap) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        Object localObject = new ArrayList();
        ((List)localObject).addAll(paramMap.keySet());
        Collections.sort((List)localObject, new SpellComparator());
        StringBuilder localStringBuilder = new StringBuilder();
        localObject = ((List)localObject).iterator();
        while (((Iterator)localObject).hasNext())
        {
            String str = (String)((Iterator)localObject).next();
            localStringBuilder.append("&").append(str).append("=").append(String.valueOf(paramMap.get(str)));
        }
        if (localStringBuilder.length() > 0)
            localStringBuilder.deleteCharAt(0);

        return getMD5Str(localStringBuilder.toString() + "(Ljava/lang/String;I)");
    }

    static class SpellComparator
            implements Comparator
    {
        public int compare(Object paramObject1, Object paramObject2)
        {
            try
            {
                int i = new String(paramObject1.toString().getBytes("GB2312"), "ISO-8859-1").compareTo(new String(paramObject2.toString().getBytes("GB2312"), "ISO-8859-1"));
                return i;
            }
            catch (Exception expSpellComparator)
            {
                expSpellComparator.printStackTrace();
            }
            return 0;
        }
    }
}
