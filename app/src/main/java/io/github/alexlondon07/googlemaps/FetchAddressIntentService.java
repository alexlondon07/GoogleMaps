package io.github.alexlondon07.googlemaps;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by alexlondon07 on 10/28/17.
 */

public class FetchAddressIntentService extends IntentService {

    private static final String TAG = FetchAddressIntentService.class.getSimpleName();
    private ResultReceiver resultReceiver;
    private String msj = "";

    public FetchAddressIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        resultReceiver = intent.getParcelableExtra(Constants.RECEIVER);
        if(resultReceiver == null){
            Log.e(TAG, "No puede obtener el receiver");
            return;
        }

        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
        if(location == null){
            msj = "Localización no proporcionada";
            deliverResultToReceiver(Constants.FAIL_RESULT, msj);
            return;
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;

        try{
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        }catch (IOException e){
            msj = "Servicio no disponible";
            deliverResultToReceiver(Constants.FAIL_RESULT, msj);
        }catch (Exception ex){
            Log.e(TAG, "Error: " + ex.getMessage());
            deliverResultToReceiver(Constants.FAIL_RESULT, ex.getMessage());
        }

        //Validar si obtuvimos direcciones
        if(addresses == null || addresses.size() == 0){
            msj = "Direccion no encontrada";
            deliverResultToReceiver(Constants.FAIL_RESULT, msj);
        }else{

            Address address = addresses.get(0);

            String addressLine = "";
            msj = "Direccion encontrada";
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++){
                addressLine += address.getAddressLine(i)+ "\n";
            }
            Log.e(TAG, msj + addressLine);

            deliverResultToReceiver(Constants.SUCCESS_RESULT, addressLine);
        }
    }

    private void deliverResultToReceiver(int codeResult, String response) {
        Bundle bundle  = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, response);
        resultReceiver.send(codeResult,bundle);
        Log.e(TAG, response);
    }
}
