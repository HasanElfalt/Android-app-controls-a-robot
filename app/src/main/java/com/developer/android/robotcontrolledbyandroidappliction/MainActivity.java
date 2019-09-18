package com.developer.android.robotcontrolledbyandroidappliction;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private int REQUEST_Enable_BT = 10;
    private final UUID myUuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    public Handler mHandler;

    // the UUID of bluetooth module HC-06 is 00001101-0000-1000-8000-00805f9b34fb

    ImageButton Up, Right, Left, Down, Stop;

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice mDevice;
    BluetoothSocket mSocket;


    String deviceName, deviceHardwareAddress;

    ConnectedThread btt = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Up    = findViewById(R.id.Up);
        Right = findViewById(R.id.Right);
        Left  = findViewById(R.id.Left);
        Down  = findViewById(R.id.Down);
        Stop  = findViewById(R.id.Stop);

        Up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mSocket.isConnected() && btt != null){

                    String sendTxt = "UP";
                    btt.write(sendTxt.getBytes());
                    Toast.makeText(getApplicationContext(),"UP",Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(getApplicationContext(),"Something gets wrong",Toast.LENGTH_SHORT).show();
                }

            }
        });

        Right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mSocket.isConnected() && btt != null){

                    String sendTxt = "Right";
                    btt.write(sendTxt.getBytes());
                    Toast.makeText(getApplicationContext(),"Right",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(),"Something gets wrong",Toast.LENGTH_SHORT).show();
                }

            }
        });

        Left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(mSocket.isConnected() && btt != null){

                    String sendTxt = "Left";
                    btt.write(sendTxt.getBytes());
                    Toast.makeText(getApplicationContext(),"Left",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(),"Something gets wrong",Toast.LENGTH_SHORT).show();
                }

            }
        });

        Down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mSocket.isConnected() && btt != null){

                    String sendTxt = "Down";
                    btt.write(sendTxt.getBytes());
                    Toast.makeText(getApplicationContext(),"Down",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(),"Something gets wrong",Toast.LENGTH_SHORT).show();
                }
            }
        });

        Stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mSocket.isConnected() && btt != null){

                    String sendTxt = "Stop";
                    btt.write(sendTxt.getBytes());
                    Toast.makeText(getApplicationContext(),"Stop",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(),"Something gets wrong",Toast.LENGTH_SHORT).show();
                }
            }
        });


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

        if (!bluetoothAdapter.isEnabled()){
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent,REQUEST_Enable_BT);
            Toast.makeText(getApplicationContext(),"Bluetooth is On",Toast.LENGTH_SHORT).show();
        }

        // checking the paired devices
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if(pairedDevices.size() > 0){
            // there are paired devices
            for(BluetoothDevice device : pairedDevices){
                // I assume you just paired to one device, the device you want to connect
                deviceName            = device.getName();
                deviceHardwareAddress = device.getAddress();     // Mac address
            }
        }

        intiateBluetoothProcess();

    }


    public void intiateBluetoothProcess(){

        if(bluetoothAdapter.isEnabled()){
            //Attempt to connect the device

            BluetoothSocket tmp = null;

            mDevice = bluetoothAdapter.getRemoteDevice(deviceHardwareAddress);

            // create a socket
            try{

                tmp = mDevice.createRfcommSocketToServiceRecord(myUuid);
                mSocket = tmp;
                mSocket.connect();

                Log.i("[Bluetooth]","connected to"+mDevice.getName());
                Toast.makeText(getApplicationContext(),"Connected to" + mDevice.getName(),Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();

                try {
                    mSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                    return;
                }
            }
            Log.i("[Bluetooth]","Creating Handler");

            /*mHandler = new Handler(Looper.getMainLooper()){

                public void handleMessage(Message msg){

                    //super.handleMessage(msg);
                    if(msg.what == ConnectedThread.RESPONSE_MESSAGE){
                        String txt = (String) msg.obj;
                        response.append("\n"+txt);
                    }
                }
            };*/
            Log.i("[BLUETOOTH]", "Creating and running Thread");
            btt = new ConnectedThread(mSocket,mHandler);
            btt.start();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == REQUEST_Enable_BT){
            intiateBluetoothProcess();
        }
    }
}
