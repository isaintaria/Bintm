package com.ng.socialtest.model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class NGJsonConverter {
    private static NGJsonConverter instance = null;

    private NGJsonConverter()
    {
    }

    public static NGJsonConverter GetInstance()
    {
        if( instance == null )
            instance = new NGJsonConverter();
        return instance;
    }

    public <T> T GetModelFromJson( String json, Class<T> tClass )
    {
        Gson gson;
        gson = new GsonBuilder().create();
        try {
            T ret = gson.fromJson(json,tClass);
            return ret;
        }
        catch(Exception e)
        {
            Log.d("TEST","Model 변환중 오류가 발생하였다");
            return null;
        }
    }


    public <T> String GetJsonFromModel(T model)
    {
        Gson gson;
        gson = new GsonBuilder().create();
        try
        {
            String ret = gson.toJson(model);
            return ret;
        }
        catch(Exception e)
        {
            Log.d("TEST","Json 변환중 오류가 발생하였다");
            return null;
        }
    }
}