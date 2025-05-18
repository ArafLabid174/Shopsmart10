package com.example.mobileshop.utils;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Utility class for debugging product JSON data
 */
public class ProductJsonDebugger {
    private static final String TAG = "ProductJsonDebugger";

    /**
     * Analyze a JSON string representation of product data
     * @param jsonString The JSON string to analyze
     */
    public static void analyze(String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            Log.e(TAG, "JSON string is null or empty");
            return;
        }

        Log.d(TAG, "JSON string length: " + jsonString.length());
        Log.d(TAG, "JSON preview: " + jsonString.substring(0, Math.min(100, jsonString.length())) + "...");

        try {
            JSONObject json = new JSONObject(jsonString);
            analyzeJsonObject(json, "");
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON string", e);
        }
    }

    /**
     * Recursively analyze a JSONObject and its contents
     * @param json The JSONObject to analyze
     * @param prefix The prefix for log messages, used for indentation in recursive calls
     */
    private static void analyzeJsonObject(JSONObject json, String prefix) {
        if (json == null) {
            Log.e(TAG, prefix + "JSONObject is null");
            return;
        }

        Log.d(TAG, prefix + "JSONObject with " + json.length() + " entries");
        
        // Check for common API response fields
        if (json.has("success")) {
            try {
                boolean success = json.getBoolean("success");
                Log.d(TAG, prefix + "success: " + success);
            } catch (JSONException e) {
                Log.e(TAG, prefix + "Error getting success field", e);
            }
        }
        
        if (json.has("message")) {
            try {
                String message = json.getString("message");
                Log.d(TAG, prefix + "message: " + message);
            } catch (JSONException e) {
                Log.e(TAG, prefix + "Error getting message field", e);
            }
        }
        
        if (json.has("data")) {
            try {
                Object data = json.get("data");
                if (data instanceof JSONObject) {
                    Log.d(TAG, prefix + "data is JSONObject");
                    analyzeJsonObject((JSONObject) data, prefix + "  ");
                } else if (data instanceof JSONArray) {
                    Log.d(TAG, prefix + "data is JSONArray");
                    analyzeJsonArray((JSONArray) data, prefix + "  ");
                } else {
                    Log.d(TAG, prefix + "data is " + data.getClass().getSimpleName());
                }
            } catch (JSONException e) {
                Log.e(TAG, prefix + "Error getting data field", e);
            }
        }
        
        // List all keys
        JSONArray names = json.names();
        if (names != null) {
            for (int i = 0; i < names.length(); i++) {
                try {
                    String key = names.getString(i);
                    if (!key.equals("success") && !key.equals("message") && !key.equals("data")) {
                        try {
                            Object value = json.get(key);
                            if (value instanceof JSONObject) {
                                Log.d(TAG, prefix + key + " is JSONObject");
                                analyzeJsonObject((JSONObject) value, prefix + "  ");
                            } else if (value instanceof JSONArray) {
                                Log.d(TAG, prefix + key + " is JSONArray with length " + ((JSONArray) value).length());
                                if (key.equals("items")) {
                                    analyzeJsonArray((JSONArray) value, prefix + "  ");
                                }
                            } else {
                                Log.d(TAG, prefix + key + ": " + value.toString());
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, prefix + "Error getting value for key " + key, e);
                        }
                    }
                } catch (JSONException e) {
                    Log.e(TAG, prefix + "Error getting key at index " + i, e);
                }
            }
        }
    }

    /**
     * Recursively analyze a JSONArray and its contents
     * @param array The JSONArray to analyze
     * @param prefix The prefix for log messages, used for indentation in recursive calls
     */
    private static void analyzeJsonArray(JSONArray array, String prefix) {
        if (array == null) {
            Log.e(TAG, prefix + "JSONArray is null");
            return;
        }

        Log.d(TAG, prefix + "JSONArray with " + array.length() + " items");
        
        if (array.length() > 0) {
            // Only analyze the first item in detail
            try {
                Object item = array.get(0);
                if (item instanceof JSONObject) {
                    Log.d(TAG, prefix + "First item is JSONObject");
                    analyzeJsonObject((JSONObject) item, prefix + "  ");
                } else if (item instanceof JSONArray) {
                    Log.d(TAG, prefix + "First item is JSONArray");
                    analyzeJsonArray((JSONArray) item, prefix + "  ");
                } else {
                    Log.d(TAG, prefix + "First item: " + item.toString());
                }
                
                // Log the types of the remaining items
                if (array.length() > 1) {
                    Log.d(TAG, prefix + "Array has " + (array.length() - 1) + " more items");
                }
            } catch (JSONException e) {
                Log.e(TAG, prefix + "Error getting first item in array", e);
            }
        }
    }
}