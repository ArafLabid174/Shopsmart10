package com.example.mobileshop.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ConnectivityHelper {
    private static final String TAG = "ConnectivityHelper";

    /**
     * Check if the device has network connectivity
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) 
            context.getSystemService(Context.CONNECTIVITY_SERVICE);
            
        if (connectivityManager == null) {
            return false;
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) {
                return false;
            }
            
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            return capabilities != null && (
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        } else {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    }
    
    /**
     * Check if a specific server is reachable
     */
    public static boolean isServerReachable(final String serverUrl, int timeout) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executor.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                try {
                    URL url = new URL(serverUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(timeout);
                    connection.setReadTimeout(timeout);
                    connection.setRequestMethod("HEAD");
                    int responseCode = connection.getResponseCode();
                    Log.d(TAG, "Server response code: " + responseCode);
                    return (200 <= responseCode && responseCode <= 399);
                } catch (IOException e) {
                    Log.e(TAG, "Server connectivity check failed: " + e.getMessage());
                    return false;
                }
            }
        });

        try {
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            Log.e(TAG, "Error checking server connectivity: " + e.getMessage());
            return false;
        } finally {
            executor.shutdown();
        }
    }
    
    /**
     * Check if the server IP is pingable (doesn't depend on HTTP)
     */
    public static boolean isHostReachable(String host, int timeout) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executor.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                try {
                    InetAddress address = InetAddress.getByName(host);
                    Log.d(TAG, "Trying to reach host: " + host);
                    return address.isReachable(timeout);
                } catch (IOException e) {
                    Log.e(TAG, "Host ping failed: " + e.getMessage());
                    return false;
                }
            }
        });

        try {
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            Log.e(TAG, "Error checking host reachability: " + e.getMessage());
            return false;
        } finally {
            executor.shutdown();
        }
    }
    
    /**
     * Get detailed network info for diagnostics
     */
    public static String getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        StringBuilder info = new StringBuilder();
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = cm.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
                info.append("Network capabilities:\n");
                
                if (capabilities != null) {
                    info.append("- WIFI: ").append(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)).append("\n");
                    info.append("- CELLULAR: ").append(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)).append("\n");
                    info.append("- ETHERNET: ").append(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)).append("\n");
                    info.append("- VPN: ").append(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)).append("\n");
                    info.append("- NOT METERED: ").append(capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)).append("\n");
                    info.append("- NOT RESTRICTED: ").append(capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)).append("\n");
                    info.append("- INTERNET: ").append(capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).append("\n");
                } else {
                    info.append("No capabilities found for active network\n");
                }
            } else {
                info.append("No active network\n");
            }
        } else {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            info.append("Active network info:\n");
            
            if (activeNetwork != null) {
                info.append("- TYPE: ").append(activeNetwork.getTypeName()).append("\n");
                info.append("- CONNECTED: ").append(activeNetwork.isConnected()).append("\n");
                info.append("- AVAILABLE: ").append(activeNetwork.isAvailable()).append("\n");
                info.append("- ROAMING: ").append(activeNetwork.isRoaming()).append("\n");
            } else {
                info.append("No active network info\n");
            }
        }
        
        return info.toString();
    }
}
