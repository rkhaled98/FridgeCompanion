package com.fridgecompanion;

import android.util.Log;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class EdamamService {
    public static final String EDAMAM_URL = "https://api.edamam.com/api/food-database/v2/parser";
    public static final String APP_ID = "3aa6e8e4";
    public static final String APP_KEY = "af257dcd426a4c4d88139c27578523e3";


    public static void searchBarcode(String query, Callback callback){
        OkHttpClient client = new OkHttpClient.Builder().build();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(EDAMAM_URL).newBuilder();
        urlBuilder.addQueryParameter("upc",query);
        urlBuilder.addQueryParameter("app_id",APP_ID);
        urlBuilder.addQueryParameter("app_key",APP_KEY);
        String url = urlBuilder.build().toString();
        Log.d("url",url);
        Request request= new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static void searchName(String query, Callback callback){
        OkHttpClient client = new OkHttpClient.Builder().build();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(EDAMAM_URL).newBuilder();
        urlBuilder.addQueryParameter("ingr",query);
        urlBuilder.addQueryParameter("app_id",APP_ID);
        urlBuilder.addQueryParameter("app_key",APP_KEY);
        String url = urlBuilder.build().toString();
        Log.d("url",url);
        Request request= new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static Food processResults(Response response){
        Food item = new Food();
        try{
            String jsonData = response.body().string();
            if(response.isSuccessful()){
                JSONObject reader = new JSONObject(jsonData);
                JSONArray foodInfo = reader.getJSONArray("hints");
                JSONObject foodList = foodInfo.getJSONObject(0);
                JSONObject food = foodList.getJSONObject("food");
                item.setId(food.getString("foodId"));
                item.setFoodName(food.getString("label"));
                item.setImage(food.getString("image"));
                JSONObject nutrients = food.getJSONObject("nutrients");
                item.setCalories(nutrients.getDouble("ENERC_KCAL"));
                item.setNutrition("Per 100 grams: "+"Protein: "+String.format("%.2f",nutrients.getDouble("PROCNT"))+"g; "+"Fat: "+
                        String.format("%.2f",nutrients.getDouble("FAT"))+"g; "+"Cholest.: "+
                        String.format("%.2f",nutrients.getDouble("CHOCDF"))+"mg");
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return item;
    }
}
