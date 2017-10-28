package io.github.alexlondon07.googlemaps;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import io.github.alexlondon07.googlemaps.services.LocationServices;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

    private static final int ALL_PERMISSION_RESULT = 101;
    private LocationServices locationServices;

    private ArrayList<String> permissionToRequest;
    private ArrayList<String> permissionToRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Permission
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        permissionToRequest = findUnAskedPermissions(permissions);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(permissionToRequest.size() > 0){
                requestPermissions(permissionToRequest.toArray(new String[permissionToRequest.size()]), ALL_PERMISSION_RESULT);
            }
        }

        locationServices = new LocationServices(MainActivity.this);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case ALL_PERMISSION_RESULT:
                for(String perms: permissionToRequest){
                    if(hasPermission(perms)){
                        permissionToRejected.add(perms);
                    }
                }
                if(permissionToRejected.size() > 0){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        if(shouldShowRequestPermissionRationale(permissionToRejected.get(0))){
                            showMessageOkCancel("This permission is required to run this application",
                                    new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                                requestPermissions(permissionToRejected.toArray( new String[permissionToRejected.size()]), ALL_PERMISSION_RESULT);
                                            }
                                        }
                                    });
                        }
                    }
                }
        }
    }

    private void showMessageOkCancel(String message, DialogInterface.OnClickListener onClickListener) {

        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", onClickListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> permissions) {
        ArrayList<String> results = new ArrayList<>();
        for (String wanted: permissions){
            if(!hasPermission(wanted)){
                results.add(wanted);
            }
        }
        return results;
    }

    private boolean hasPermission(String wanted) {
        if(canMakeMatch()){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                return (checkSelfPermission(wanted) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeMatch() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    public void launchMap(View view) {
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        startActivity(intent);
    }

    public void launchAddress(View view) {
        Intent intent = new Intent(MainActivity.this, AddressActivity.class);
        startActivity(intent);
    }


    public void getLocationDevice(View view){
        if(locationServices.isCanGetLocation()){
            double longitude = locationServices.getLongitude();
            double latitude = locationServices.getLatitude();

            Toast.makeText(MainActivity.this, "Longitude " + longitude + " Latitude " + latitude, Toast.LENGTH_LONG).show();

        }else{
            //TODO SHOW CONFIGURATION ALERT
            locationServices.showSettingsAlert();
        }
    }

}
