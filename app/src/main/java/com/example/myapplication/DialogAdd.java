package com.example.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

public class DialogAdd extends Dialog {

    public DialogAdd(@NonNull final Context context, final LatLng ubicacion, final ControllerMapActivity controllerMapActivity) {
        super(context);
        setContentView(R.layout.dialog_add);
        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.ok) {
                    String name = ((TextView) findViewById(R.id.name)).getText().toString();
                    String description = ((TextView) findViewById(R.id.description)).getText().toString();
                    if (!name.equals("")) {
                        controllerMapActivity.addMarker(ubicacion.latitude, ubicacion.longitude, name, description);
                        dismiss();
                    }
                }
            }
        });
    }
}
