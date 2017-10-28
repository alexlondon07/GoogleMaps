package io.github.alexlondon07.googlemaps.services;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by alexlondon07 on 10/24/17.
 */

public class LocationServices extends Service implements LocationListener{

    private final Context context;
    private LocationManager locationManager;
    private boolean checkGPS = false;
    private boolean checkNetwork = false;
    private boolean canGetLocation= false;


    private static final long MIN_DISTANCE_FOR_UPDATE = 10;
    private  static  final long MIN_TIME_FOR_UPDATES = 1000 * 60 * 1;
    private Location location;
    private String TAG = "LocationServices";
    private double latitude;
    private double longitude;

    public LocationServices(Context context) {
        this.context = context;
        getLocation();
    }

    private void getLocation(){
        try {

            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            //Get state GPS
            checkGPS = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);

            //Get state internet
            checkNetwork = locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER);

            if(!checkGPS && !checkNetwork){
                //TODO add alert or toast notification
                canGetLocation = false;
            }else{
                canGetLocation = true;

                //check GPS Location
                if(checkGPS){
                    if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        //TODO Implements dialog with permission
                    }
                    locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, MIN_TIME_FOR_UPDATES, MIN_DISTANCE_FOR_UPDATE, this);

                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }

                //check Network
                if(checkNetwork){
                    if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        //TODO Implements dialog with permission
                    }
                    locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, MIN_TIME_FOR_UPDATES, MIN_DISTANCE_FOR_UPDATE, this);

                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if(location != null){
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }

                }
            }

        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean isCanGetLocation() {
        return canGetLocation;
    }


    //Permission settings
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("GPS no enabled");
        alertDialog.setMessage("You want to turn it on?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        alertDialog.show();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
