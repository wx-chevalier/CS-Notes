package wx.network.httpclient.okhttp;

import com.squareup.okhttp.*;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by apple on 15/11/8.
 */
public class WxOkHttpClient {

    private static final OkHttpClient mOkHttpClient = new OkHttpClient();

    static {

        mOkHttpClient.setConnectTimeout(30, TimeUnit.SECONDS);

    }

    /**
     * 该不会开启异步线程。
     *
     * @param request
     * @return
     * @throws IOException
     */

    public static Response execute(Request request) throws IOException {

        return mOkHttpClient.newCall(request).execute();

    }

    /**
     * 开启异步线程访问网络
     *
     * @param request
     * @param responseCallback
     */

    public static void enqueue(Request request, Callback responseCallback) {

        mOkHttpClient.newCall(request).enqueue(responseCallback);

    }

    /**
     * 开启异步线程访问网络, 且不在意返回结果（实现空callback）
     *
     * @param request
     */

    public static void enqueue(Request request) {

        mOkHttpClient.newCall(request).enqueue(new Callback() {


            @Override
            public void onResponse(Response arg0) throws IOException {


            }


            @Override
            public void onFailure(Request arg0, IOException arg1) {


            }

        });

    }

    public static String getStringFromServer(String url) throws IOException {

        Request request = new Request.Builder().url(url).build();

        Response response = execute(request);

        if (response.isSuccessful()) {

            String responseUrl = response.body().string();

            return responseUrl;

        } else {

            throw new IOException("Unexpected code " + response);

        }

    }

    private static final String CHARSET_NAME = "UTF-8";


    public static void main(String args[]) throws IOException {

        String url = "https://login.weixin.qq.com/qrcode/o3LILjxA19nn_onvYFYCpwk7eoqU";

        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme("https")
                .host("login.weixin.qq.com")
                .addPathSegment("qrcode")
                .addPathSegment("o3LILjxA19nn_onvYFYCpwk7eoqU")
                .addQueryParameter("t","webwx")
                .addQueryParameter("_", String.valueOf(System.currentTimeMillis()))
                .build();

        System.out.println(httpUrl);

        System.setProperty("jsse.enableSNIExtension", "false");

        final Request request = new Request.Builder()
                .url(httpUrl)
                .build();

        Response response = WxOkHttpClient.execute(request);

        System.out.println(response.body().string());
    }
}
