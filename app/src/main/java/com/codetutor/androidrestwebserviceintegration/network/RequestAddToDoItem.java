package com.codetutor.androidrestwebserviceintegration.network;

import android.util.Log;

import com.codetutor.androidrestwebserviceintegration.AppConfig;
import com.codetutor.androidrestwebserviceintegration.restbean.ToDoItem;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestAddToDoItem extends AppNetworkRequest{

    public static final String TAG = RequestRegisterAuthor.class.getSimpleName();

    String url = RestAPIs.getBaseUrl()+ToDoAppRestAPI.registerAuthor;

    Request request;

    RequestAddToDoItem(APICallListener apiCallListener, Object jsonRequestBody){

        super(apiCallListener);
        request =  new Request.Builder().url(url)
                .addHeader(AppNetworkRequest.CONTENT_TYPE,AppNetworkRequest.JSON_CONTENT_TYPE)
                .addHeader(AppNetworkRequest.TOKEN, AppConfig.getSessionTokenValue())
                .post(RequestBody.create(MediaType.parse(AppNetworkRequest.JSON_CONTENT_TYPE), jsonRequestBody.toString()))
                .build();
    }

    @Override
    public void makeBackEndRequest() {
        new Thread(this).start();
    }
    @Override
    public void run() {
        okHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        apiCallListener.onCallBackFailure(e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                try{
                    responseObject = new GsonBuilder().create().fromJson(response.body().string(), ToDoItem.class);
                }catch (IOException e){
                    Log.d(TAG,e.getMessage());
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        apiCallListener.onCallBackSuccess(responseObject);
                    }
                });

            }
        });
    }
}
