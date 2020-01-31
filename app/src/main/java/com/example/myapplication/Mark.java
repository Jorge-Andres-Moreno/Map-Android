package com.example.myapplication;

import java.io.Serializable;

public class Mark implements Serializable {

    public String name;
    public String description;
    public double lat;
    public double lng;

    public String toString() {
        return "name=" + name + "\ndescripcion=" + description + "\nlatitud=" + lat + "\nlongitud=" + lng;
    }
}
