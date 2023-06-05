package com.tesla.okhttp3test;

import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Example {
    private static final String TAG = "Example";
    // 新建一个Okhttp客户端
    //OkHttpClient client = new OkHttpClient();
    public OkHttpClient client = new OkHttpClient.Builder()
            .addNetworkInterceptor(new LoggingInterceptor())
            .build();

    public void run(String url) throws IOException {
        // 构造request
        Request request = new Request.Builder()
                .url(url)
                .build();
        // 发起异步请求
        client.newCall(request).enqueue(new Callback() {
                                            @Override
                                            public void onFailure(Call call, IOException e) {
                                                call.cancel();
                                            }

                                            @Override
                                            public void onResponse(Call call, Response response) throws IOException {
                                                //打印输出
                                                Log.d(TAG, response.body().string());
                                            }
                                        }
        );
    }
}