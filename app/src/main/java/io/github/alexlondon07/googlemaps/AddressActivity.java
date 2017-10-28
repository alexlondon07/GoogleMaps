package io.github.alexlondon07.googlemaps;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

public class AddressActivity extends AppCompatActivity {

    private Location myLocation;
    private TextView textViewAddress;
    private AddressResultReceiver  resultReceiver;
    private String addressOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        textViewAddress = (TextView) findViewById(R.id.textView_address);
        addressOutput = "";

        myLocation =  new Location("Casa");
        myLocation.setLatitude(6.2499157);
        myLocation.setLongitude(-75.59853420000002);

        resultReceiver =  new AddressResultReceiver(new Handler());
        startIntentService();
    }

    private void startIntentService(){
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, myLocation);
        startService(intent);
    }

    public class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            addressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            if(resultCode == Constants.FAIL_RESULT){
                Toast.makeText(AddressActivity.this, "Dirección no disponible", Toast.LENGTH_LONG).show();
            }else {
                displayAddress(addressOutput);
            }
        }
    }

    private void displayAddress(String address){
        textViewAddress.setText(address);
    }
}
