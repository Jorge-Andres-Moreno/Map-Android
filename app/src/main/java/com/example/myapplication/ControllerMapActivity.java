package com.example.myapplication;

import android.location.Location;

import java.util.ArrayList;

public class ControllerMapActivity {

    private MapsActivity mapsActivity;
    private LocalDataBase db;
    private ArrayList<Mark> marks;
    private Mark nearest;
    private double nearestDistance;

    public ControllerMapActivity(MapsActivity context) {
        this.mapsActivity = context;
        db = new LocalDataBase(context);
        nearestDistance = Double.MAX_VALUE;
        loadData();
    }

    private void loadData() {
        marks = db.getMarks();
    }

    public void initMarkes() {
        nearestDistance = Double.MAX_VALUE;
        Location loc = mapsActivity.mLastKnownLocation;
        for (int i = 0; i < marks.size(); i++) {
            if (marks.get(i) != null) {
                double dis = distanciaCoord(loc.getLatitude(), loc.getLongitude(), marks.get(i).lat, marks.get(i).lng) * 1000;
                String descripcion = "" + marks.get(i).name + " se encuentra a " + dis + " metros";
                mapsActivity.addMark(marks.get(i), descripcion);
                if (nearest == null || dis < nearestDistance) {
                    nearest = marks.get(i);
                    nearestDistance = dis;
                }
            }
        }
        if (nearest != null) {
            mapsActivity.textView.setText("La ubicación más cercana es " + nearest.name);
        }
    }

    public void addMarker(double lat, double lng, String name, String description) {

        Mark m = new Mark();
        m.lat = lat;
        m.lng = lng;
        m.name = name;
        m.description = description;

        marks.add(m);
        db.saveMark(marks);
        Location loc = mapsActivity.mLastKnownLocation;
        String descri = "" + m.name + " se encuentra a " + distanciaCoord(loc.getLatitude(), loc.getLongitude(), lat, lng) * 1000 + " metros";

        mapsActivity.addMark(m, descri);
        mapsActivity.chanceAdd();

        double dis = distanciaCoord(loc.getLatitude(), loc.getLongitude(), lat, lng) * 1000;
        if (dis < nearestDistance) {
            nearestDistance = dis;
            nearest = m;
            mapsActivity.textView.setText("La ubicación más cercana es " + nearest.name);
        }

    }

    public double distanciaCoord(double lat1, double lng1, double lat2, double lng2) {
        //double radioTierra = 3958.75;//en millas
        double radioTierra = 6371;//en kilómetros
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double va1 = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double va2 = 2 * Math.atan2(Math.sqrt(va1), Math.sqrt(1 - va1));
        double distancia = radioTierra * va2;

        return distancia;
    }

}
