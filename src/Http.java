/**
 * Created by fsy on 3/2/16.
 */

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

public class Http {
    /**
     * 绕过验证
     *
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        //SSLContext sc = SSLContext.getInstance("SSLv3");
        SSLContext sc = SSLContext.getInstance("TLSv1.2");
        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        // sc.init(null, new TrustManager[] { trustManager }, null);
        sc.init(null, new TrustManager[] { trustManager }, new java.security.SecureRandom());

        // in Java 1.8:
        // SSLContext sc = SSLContext.getInstance("TLSv1.2");
        // Init the SSLContext with a TrustManager[] and SecureRandom()
        // sc.init(null, trustCerts, new java.security.SecureRandom());

        // in Java 1.7:
        // SSLContext sc = SSLContext.getInstance("TLSv1");
        // Init the SSLContext with a TrustManager[] and SecureRandom()
        // sc.init(null, trustCerts, new java.security.SecureRandom());

        return sc;
    }
    
    /**
     * 模拟请求
     *
     * @param url       资源地址
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @throws IOException
     * @throws ClientProtocolException
     */

    public static String send(String url, String parameter) throws KeyManagementException, NoSuchAlgorithmException, ClientProtocolException, IOException {
        try {
            String body = "";
            // 采用绕过验证的方式处理https请求
            SSLContext sslcontext = createIgnoreVerifySSL();

            // 设置协议http和https对应的处理socket链接工厂的对象
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(sslcontext))
                    .build();
            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            HttpClients.custom().setConnectionManager(connManager);

            // 创建自定义的httpclient对象
            CloseableHttpClient client = HttpClients.custom().setConnectionManager(connManager).build();
            // CloseableHttpClient client = HttpClients.createDefault();
            // 创建post方式请求对象
            HttpPost httpPost = new HttpPost(url);

            String[] args = parameter.split("&");
            // 装填参数
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();

            for (int i=0; i<args.length; i++)
            {
                String[] parts = args[i].split("=");
                if (parts.length==2)
                    nvps.add(new BasicNameValuePair(parts[0], parts[1]));
                else
                    nvps.add(new BasicNameValuePair(parts[0],""));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));

            // System.out.println("请求地址："+url);
            // System.out.println("请求地址: " + httpPost.getRequestLine().getUri());
            // System.out.println("参数："+parameter);
            // System.out.println("请求参数："+nvps.toString());

            //设置header信息
            //指定报文头【Content-type】、【User-Agent】
            httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
            httpPost.setHeader("User-Agent", "TaoTao/4.0.1 (Google Nexus 6P - 6.0.0 - API 23 - 1440x2560; Android 23)");

            //执行请求操作，并拿到结果（同步阻塞）
            CloseableHttpResponse response = client.execute(httpPost);
            //获取结果实体
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                //按指定编码转换结果实体为String类型
                body = EntityUtils.toString(entity, "UTF-8");
                // System.out.println("----------------------------------------");
                // System.out.println(body);
                // System.out.println("----------------------------------------");
            }
            EntityUtils.consume(entity);
            //释放链接
            response.close();
            return body;
        }catch(Exception e1) {
            return "http_error";
        }

    }
}
