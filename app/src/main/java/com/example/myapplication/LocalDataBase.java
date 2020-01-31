package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class LocalDataBase {

    private SharedPreferences preferences;
    private ArrayList<Mark> list;

    private final String KEY_APP = "MyMapApp";
    private final String KEY_LIST = "list";

    public LocalDataBase(Context context) {
        preferences = context.getSharedPreferences(KEY_APP, Context.MODE_PRIVATE);
        loadMarks();
    }

    public ArrayList<Mark> getMarks() {
        return list;
    }

    private void loadMarks() {
        if (list == null) {
            Gson gson = new GsonBuilder().create();
            list = gson.fromJson(preferences.getString(KEY_LIST, null), new TypeToken<List<Mark>>() {
            }.getType());
            if (list == null)
                list = new ArrayList<>();
        }
    }

    public void saveMark(ArrayList<Mark> marks) {
        Gson gson = new GsonBuilder().create();
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(KEY_LIST, gson.toJson(marks, new TypeToken<List<Mark>>() {
        }.getType()));
        if (edit.commit())
            list = marks;
    }

    public void deletedCredentials() {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(KEY_LIST, null);
        if (edit.commit())
            list = null;
    }


}
