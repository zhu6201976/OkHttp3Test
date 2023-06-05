package com.tesla.okhttp3test;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {
    private EditText et_url;
    private TextView tv_source;
    private final String TAG = "MainActivity";
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_url = (EditText) findViewById(R.id.et_url);
        tv_source = (TextView) findViewById(R.id.tv_source);

        // 全局只使用这一个拦截器
        client = new OkHttpClient.Builder()
//                .addNetworkInterceptor(new Okhttp3Logging())
                .build();
    }

    /**
     * 异步 GET
     */
    public void SEND(View view) {
        String url = et_url.getText().toString().trim();
        if (url.isEmpty()) {
            Toast.makeText(MainActivity.this, "网址不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
                                            @Override
                                            public void onFailure(Call call, IOException e) {
                                                Log.d(TAG, "onFailure");
                                                call.cancel();
                                            }

                                            @Override
                                            public void onResponse(Call call, Response response) throws IOException {
                                                String resp_text = response.body().string();
                                                // 主线程更新ui
                                                updateUi(resp_text);
                                            }
                                        }
        );
    }

    /**
     * 同步 GET
     */
    public void GET(View view) {
        // 请求网络,子线程进行
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = et_url.getText().toString().trim();
                if (url.isEmpty()) {
                    Toast.makeText(MainActivity.this, "网址不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    Request request = new Request.Builder()
                            .url(url)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    // 主线程更新ui
                    updateUi(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 同步 POST
     */
    public void POST(View view) {
        // 请求网络,子线程进行
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = et_url.getText().toString().trim();
                if (url.isEmpty()) {
                    Toast.makeText(MainActivity.this, "网址不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    RequestBody requestBody = new FormBody.Builder()
                            .add("username", "tesla")
                            .add("password", "123456")
                            .build();
                    Request request = new Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    // 主线程更新ui
                    updateUi(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 主线程更新ui
     *
     * @param result 网络源码
     */
    private void updateUi(final String result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_source.setText(result);
            }
        });
    }
}
