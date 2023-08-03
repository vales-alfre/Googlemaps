package com.example.googlemaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import WebService.Asynchtask;
import WebService.WebService;
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, Asynchtask {
GoogleMap Mapi;
    ArrayList<LatLng> marcar = new ArrayList(6);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Mapi=googleMap;

        // ya esta conectada la cmara
        Mapi.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        Mapi.getUiSettings().setZoomControlsEnabled(true);
        //mover el mapa a una ubicacion
       /* CameraUpdate camUpd1 =
                CameraUpdateFactory
                        .newLatLngZoom(new LatLng(40.689233100977454, -74.0446613360531), 15);
        Mapi.moveCamera(camUpd1);*/
        LatLng libertad = new LatLng(40.689233100977454, -74.0446613360531);
        CameraPosition camPos = new CameraPosition.Builder()
                .target(libertad)
                .zoom(19)
                .bearing(23) //noreste arriba
                .tilt(90) //punto de vista de la cámara 70 grados
                .build();
        CameraUpdate camUpd3 =
                CameraUpdateFactory.newCameraPosition(camPos);
        Mapi.animateCamera(camUpd3);
        Mapi.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng)  {
        LatLng punto = new LatLng(latLng.latitude,
                latLng.longitude);
        MarkerOptions macar= new MarkerOptions();
        macar.position(latLng);
        macar.title("punto");
        Mapi.addMarker(macar);
        marcar.add(punto);
        PolylineOptions lineas = new PolylineOptions();
        if (marcar.size()==6) {
        for (int i =0; i< marcar.size(); i++){
            lineas.add(marcar.get(i));

        }
        lineas.add(marcar.get(0));
        }
        lineas.width(8);
        lineas.color(Color.RED);
        Mapi.addPolyline(lineas);

        Map<String, String> datos = new HashMap<String, String>();
        datos.put("key","AIzaSyBdnZ0lGkW_vH2M6Au1SUqUkntR-mUvrNI");
        datos.put("destinations","");
        datos.put("origins","");
        datos.put("units","");
        WebService ws= new WebService("https://maps.googleapis.com/maps/api/distancematrix/json",
                datos, this,  MainActivity.this);
        ws.execute("POST");




    }

    @Override
    public void processFinish(String result) throws JSONException {

    }
}