package com.example.googlemaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import WebService.Asynchtask;
import WebService.WebService;
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener{
GoogleMap Mapi;
    ArrayList<LatLng> marcar = new ArrayList(6);
    private Double Total = 0.00;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textView=findViewById(R.id.textView);
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
Log.i("marcar",marcar.toString());

        StringBuilder destinations = new StringBuilder();
        for (LatLng coordinate : marcar) {
            destinations.append(coordinate.latitude).append(",").append(coordinate.longitude).append("|");
        }
        // Remove the trailing "|"
        if (destinations.length() > 0) {
            destinations.setLength(destinations.length() - 1);
        }

        LatLng firstMarker = marcar.get(0);
        String origins = firstMarker.latitude + "," + firstMarker.longitude;

        StringBuilder urlBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/distancematrix/json");
        urlBuilder.append("?key=").append(Uri.encode("AIzaSyBdnZ0lGkW_vH2M6Au1SUqUkntR-mUvrNI"));
        urlBuilder.append("&destinations=").append(Uri.encode(destinations.toString()));
        urlBuilder.append("&origins=").append(Uri.encode(origins));
        urlBuilder.append("&units=meter");

        String url = urlBuilder.toString();

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("resul",response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray rows = jsonResponse.getJSONArray("rows");
                            JSONObject row = rows.getJSONObject(0);
                            JSONArray elements = row.getJSONArray("elements");
                            for (int i = 0; i < elements.length(); i++) {
                                JSONObject element = elements.getJSONObject(i);
                                String status = element.getString("status");
                                if (status.equals("OK")) {
                                    JSONObject distance = element.getJSONObject("distance");
                                    double distanceInMeters = +distance.getDouble("value");
                                    Toast.makeText(MainActivity.this, "Distancia para el destino " +distanceInMeters+ ": " + distanceInMeters + " metros", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Si el status no es "OK", significa que hubo un error en la solicitud para este destino
                                    Log.e("Error", "No se pudo obtener la distancia para el destino " + (i + 1));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", "That didn't work!");

            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    }

