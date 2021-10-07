package sereinfish.bot.utils;

import okhttp3.*;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OkHttpUtil {
    public int READ_TIMEOUT = 100;
    public int CONNECT_TIMEOUT = 60;
    public int WRITE_TIMEOUT = 60;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient mOkHttpClient;
    public HashMap<String, String> header = new HashMap();
    public static final String defsult_user_agent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36";

    public OkHttpUtil useDefault_User_Agent() {
        this.header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36");
        return this;
    }

    public OkHttpUtil setUserAgent(String userAgent) {
        this.header.put("User-Agent", userAgent);
        return this;
    }

    public OkHttpUtil() {
        this.init();
    }

    public Response getData(String url) {
        this.init();
        Request.Builder builder = new Request.Builder();
        Iterator iterator = this.header.keySet().iterator();

        while(iterator.hasNext()) {
            String key = ((String)iterator.next());
            String v = this.header.get(key);
            builder.addHeader(key, v);
        }

        Request request = builder.get().url(url).build();
        Call call = this.mOkHttpClient.newCall(request);
        Response response = null;

        try {
            response = call.execute();
        } catch (IOException var9) {
            var9.printStackTrace();
        }

        return response;
    }

    public void getDataAsyn(String url, final OkHttpUtil.NetCall netCall) {
        Request.Builder builder = new Request.Builder();
        Iterator<String> iterator = this.header.keySet().iterator();
        String key = null;

        while(iterator.hasNext()) {
            key = iterator.next();
            String v = this.header.get(key);
            builder.addHeader(key, v);
        }

        Request request = builder.get().url(url).build();
        Call call = this.mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                netCall.failed(call, e);
            }

            public void onResponse(Call call, Response response) throws IOException {
                netCall.success(call, response);
            }
        });
    }

    public void postDataAsyn(String url, Map<String, String> bodyParams, final OkHttpUtil.NetCall netCall) {
        RequestBody body = this.setRequestBody(bodyParams);
        Request.Builder requestBuilder = new Request.Builder();
        Iterator<String> iterator = this.header.keySet().iterator();
        String key = null;

        while(iterator.hasNext()) {
            key = iterator.next();
            String v = this.header.get(key);
            requestBuilder.addHeader(key, v);
        }

        Request request = requestBuilder.post(body).url(url).build();
        Call call = this.mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                netCall.failed(call, e);
            }

            public void onResponse(Call call, Response response) throws IOException {
                netCall.success(call, response);
            }
        });
    }

    public void postDataAsyn(String url, String data, final OkHttpUtil.NetCall netCall) {
        RequestBody body = RequestBody.create(MediaType.parse("text/html;charset=utf-8"), data);
        Request.Builder requestBuilder = new Request.Builder();
        Iterator<String> iterator = this.header.keySet().iterator();
        String key = null;

        while(iterator.hasNext()) {
            key = iterator.next();
            String v = this.header.get(key);
            requestBuilder.addHeader(key, v);
        }

        Request request = requestBuilder.post(body).url(url).build();
        Call call = this.mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                netCall.failed(call, e);
            }

            public void onResponse(Call call, Response response) throws IOException {
                netCall.success(call, response);
            }
        });
    }

    public Response postJson(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request.Builder requestBuilder = new Request.Builder();
        Iterator<String> iterator = this.header.keySet().iterator();
        String key = null;

        while(iterator.hasNext()) {
            key = iterator.next();
            String v = this.header.get(key);
            requestBuilder.addHeader(key, v);
        }

        Request request = requestBuilder.post(body).url(url).build();
        Response response = this.mOkHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            return response;
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    public void postJsonAsyn(String url, String json, final OkHttpUtil.NetCall netCall) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request.Builder requestBuilder = new Request.Builder();
        Iterator<String> iterator = this.header.keySet().iterator();
        String key = null;

        while(iterator.hasNext()) {
            key = iterator.next();
            String v = this.header.get(key);
            requestBuilder.addHeader(key, v);
        }

        Request request = requestBuilder.post(body).url(url).build();
        Call call = this.mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                netCall.failed(call, e);
            }

            public void onResponse(Call call, Response response) throws IOException {
                netCall.success(call, response);
            }
        });
    }

    public Response postData(String url, Map<String, String> bodyParams) {
        this.init();
        RequestBody body = this.setRequestBody(bodyParams);
        Request.Builder requestBuilder = new Request.Builder();
        Iterator<String> iterator = this.header.keySet().iterator();
        String key = null;

        while(iterator.hasNext()) {
            key = iterator.next();
            String v = this.header.get(key);
            requestBuilder.addHeader(key, v);
        }

        Request request = requestBuilder.post(body).url(url).build();
        Call call = this.mOkHttpClient.newCall(request);
        Response response = null;

        try {
            response = call.execute();
        } catch (IOException var11) {
            var11.printStackTrace();
        }

        return response;
    }

    public Response postData(String url, String data) {
        this.init();
        RequestBody body = RequestBody.create(MediaType.parse("text/html;charset=utf-8"), data);
        Request.Builder requestBuilder = new Request.Builder();
        Iterator<String> iterator = this.header.keySet().iterator();
        String key = null;

        while(iterator.hasNext()) {
            key = iterator.next();
            String v = this.header.get(key);
            requestBuilder.addHeader(key, v);
        }

        Request request = requestBuilder.post(body).url(url).build();
        Call call = this.mOkHttpClient.newCall(request);
        Response response = null;

        try {
            response = call.execute();
        } catch (IOException var11) {
            var11.printStackTrace();
        }

        return response;
    }

    private RequestBody setRequestBody(Map<String, String> BodyParams) {
        RequestBody body = null;
        okhttp3.FormBody.Builder formEncodingBuilder = new okhttp3.FormBody.Builder();
        if (BodyParams != null) {
            Iterator<String> iterator = BodyParams.keySet().iterator();
            String key = "";

            while(iterator.hasNext()) {
                key = iterator.next();
                formEncodingBuilder.add(key, BodyParams.get(key));
            }
        }

        body = formEncodingBuilder.build();
        return body;
    }

    public OkHttpUtil init() {
        okhttp3.OkHttpClient.Builder ClientBuilder = new okhttp3.OkHttpClient.Builder();
        ClientBuilder.readTimeout(this.READ_TIMEOUT, TimeUnit.SECONDS);
        ClientBuilder.connectTimeout(this.CONNECT_TIMEOUT, TimeUnit.SECONDS);
        ClientBuilder.writeTimeout(this.WRITE_TIMEOUT, TimeUnit.SECONDS);
        ClientBuilder.sslSocketFactory(this.createSSLSocketFactory());
        ClientBuilder.hostnameVerifier(new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        this.mOkHttpClient = ClientBuilder.build();
        return this;
    }

    private SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new OkHttpUtil.TrustAllCerts()}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception var3) {
        }

        return ssfFactory;
    }

    public OkHttpUtil setCONNECT_TIMEOUT(int CONNECT_TIMEOUT) {
        this.CONNECT_TIMEOUT = CONNECT_TIMEOUT;
        return this;
    }

    public OkHttpUtil setREAD_TIMEOUT(int READ_TIMEOUT) {
        this.READ_TIMEOUT = READ_TIMEOUT;
        return this;
    }

    public OkHttpUtil setWRITE_TIMEOUT(int WRITE_TIMEOUT) {
        this.WRITE_TIMEOUT = WRITE_TIMEOUT;
        return this;
    }

    public OkHttpUtil addHeader(String key, String value) {
        this.header.put(key, value);
        return this;
    }

    public HashMap<String, String> getHeader() {
        return this.header;
    }

    public OkHttpUtil delHeader(String key) {
        this.header.remove(key);
        return this;
    }

    class TrustAllCerts implements X509TrustManager {
        TrustAllCerts() {
        }

        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    public interface NetCall {
        void success(Call var1, Response var2) throws IOException;

        void failed(Call var1, IOException var2);
    }
}
