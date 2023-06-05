package com.tesla.okhttp3test;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Arrays;

import androidx.annotation.NonNull;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * 拦截器 Android可以拦截 Frida就能 只是实现起来比较麻烦
 */
class LoggingInterceptor implements Interceptor {
    private static final String TAG = "LoggingInterceptor";

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Log.d(TAG, "request：" + request + "\n");
        Log.d(TAG, "request.headers：" + request.headers() + "\n");
        Log.d(TAG, "request.body：" + bodyToString(request.body()) + "\n");

        Response response = chain.proceed(request);
        Log.d(TAG, "response：" + response + "\n");
        Log.d(TAG, "response.headers：" + response.headers() + "\n");

        // resp_content
        ResponseBody rb = response.body();
        BufferedSource source = rb.source();
        source.request(Long.MAX_VALUE);
        Buffer bf = source.buffer();
        ResponseBody body_copy = ResponseBody.create(rb.contentType(), rb.contentLength(), bf.clone());
        ResponseBody body_copy2 = ResponseBody.create(rb.contentType(), rb.contentLength(), bf.clone());
        InputStream inputStream = body_copy.byteStream();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            // 按byte读取
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            byte[] responseBody = byteArrayOutputStream.toByteArray();
            Log.i(TAG, "response.body：" + Arrays.toString(responseBody) + "\n");
            Log.d(TAG, "response.body：" + body_copy2.string() + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            inputStream.close();
            byteArrayOutputStream.close();
        }

        return response;
    }

    public String bodyToString(RequestBody requestBody) {
        try {
            Buffer buffer = new Buffer();
            if (requestBody != null) {
                requestBody.writeTo(buffer);
            } else {
                return "";
            }
            Charset charset = StandardCharsets.UTF_8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                try {
                    charset = contentType.charset(StandardCharsets.UTF_8);
                } catch (UnsupportedCharsetException e) {
                    return "";
                }
            }
            return buffer.readString(charset);
        } catch (IOException e) {
            return "";
        }
    }
}