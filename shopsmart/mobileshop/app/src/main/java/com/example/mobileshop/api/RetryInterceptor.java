package com.example.mobileshop.api;

import android.util.Log;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RetryInterceptor implements Interceptor {
    private static final String TAG = "RetryInterceptor";
    private final int maxRetries;
    
    public RetryInterceptor(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = null;
        IOException exception = null;
        
        int retryCount = 0;
        boolean responseOK = false;
        
        while (!responseOK && retryCount < maxRetries) {
            if (retryCount > 0) {
                Log.d(TAG, "Retry attempt " + retryCount + " for request: " + request.url());
            }
            
            try {
                response = chain.proceed(request);
                responseOK = response.isSuccessful();
            } catch (IOException e) {
                Log.e(TAG, "Request failed: " + e.getMessage());
                exception = e;
            }
            
            retryCount++;
            
            // If we've failed but still have retries left, wait before next attempt
            if (!responseOK && retryCount < maxRetries) {
                try {
                    long waitTime = 1000L * retryCount;
                    Log.d(TAG, "Waiting " + waitTime + "ms before next retry");
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    throw new IOException("Retry interrupted", e);
                }
            }
        }
        
        // If we still don't have a valid response after all retries, throw the last exception
        if (response == null && exception != null) {
            throw exception;
        }
        
        return response;
    }
}
