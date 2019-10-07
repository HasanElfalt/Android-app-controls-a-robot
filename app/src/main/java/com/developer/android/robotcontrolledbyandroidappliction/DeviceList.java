package com.developer.android.robotcontrolledbyandroidappliction;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

import static android.widget.Toast.LENGTH_SHORT;

public class DeviceList extends AppCompatActivity {

    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            String info     = ((TextView)view).getText().toString();
            String address  = info.substring(info.length()-17);

            Intent intent = new Intent(DeviceList.this,MainActivity.class);
            intent.putExtra("device address", address);
            startActivity(intent);

        }

    };
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private BluetoothAdapter bluetoothAdapter;
    private int REQUEST_Enable_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

    }
    public void onResume() {
        super.onResume();
        checkBTState();

        mPairedDevicesArrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        ListView pairedListView    = findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // checking the paired devices
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if(pairedDevices.size() > 0){
            // there are paired devices
            findViewById(R.id.titled_paired_devices).setVisibility(View.VISIBLE);
            for(BluetoothDevice device : pairedDevices){
                // I assume you just paired to one device, the device you want to connect
                String deviceName            = device.getName();
                String deviceHardwareAddress = device.getAddress();     // Mac address
                mPairedDevicesArrayAdapter.add(deviceName + "\n" +deviceHardwareAddress);
            }
            return;
        }
        // if there is no paired devices
        mPairedDevicesArrayAdapter.add("there is no paired devices");

    }

    private void checkBTState(){

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(bluetoothAdapter == null){
            //phone does not support Bluetooth

            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Your phone does not support bluetooth")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            System.exit(0);
                        }
                    })
                    .show();
        }
        else if (!bluetoothAdapter.isEnabled()){
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent,REQUEST_Enable_BT);
            Toast.makeText(getApplicationContext(),"Bluetooth is On", LENGTH_SHORT).show();
        }


    }
}
