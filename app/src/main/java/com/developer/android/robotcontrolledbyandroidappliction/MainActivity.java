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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import static android.widget.Toast.LENGTH_SHORT;


public class MainActivity extends AppCompatActivity {


    private class ConnectedThread extends Thread {

        private final InputStream mInStream;
        private final OutputStream mOutStream;
        Handler mHandler;

        public ConnectedThread(BluetoothSocket mSocket, Handler uih) {

            InputStream tmpIn   = null;
            OutputStream tmpOut = null;
            mHandler = uih;

            Log.i("[THREAD]","Creating thread");

            try {
                tmpIn  = mSocket.getInputStream();
                tmpOut = mSocket.getOutputStream();

            } catch (IOException e) {
                Log.e("[THREAD]","Error:"+e.getMessage());
            }
            this.mInStream  = tmpIn;
            this.mOutStream = tmpOut;

        }

        public void run(){

            byte[] buffer = new byte[256];

            while (true) {
                try {

                    int bytes = this.mInStream.read(buffer);
                    mHandler.obtainMessage(0,bytes,-1,
                            new  String(buffer,0,bytes)).sendToTarget();

                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }

            }
        }
        public void write(String input){

            try {
                mOutStream.write(input.getBytes());
            } catch (IOException e) {
                e.printStackTrace();

            }
        }

    }


    private int REQUEST_Enable_BT = 10;
    private static final UUID myUuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public Handler mHandler;
    private String address;
    TextView connectedDevice;

    // the UUID of bluetooth module HC-06 is 00001101-0000-1000-8000-00805f9b34fb
    // 00001101-0000-1000-8000-00805F9B34FB

    ImageButton Up, Right, Left, Down, Stop;
    Button Connect;

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice  mDevice;
    BluetoothSocket  mSocket;

    ConnectedThread btt ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Up    = findViewById(R.id.Up);
        Right = findViewById(R.id.Right);
        Left  = findViewById(R.id.Left);
        Down  = findViewById(R.id.Down);
        Stop  = findViewById(R.id.Stop);
        Connect = findViewById(R.id.Connect);
        connectedDevice = findViewById(R.id.ConnectedDevice);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();

        Connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent deviceList = new Intent(MainActivity.this,DeviceList.class);
                startActivity(deviceList);
            }
        });

        Up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(address != null) {
                    String sendTxt = "UP";
                    btt.write(sendTxt);
                    Toast.makeText(getApplicationContext(), "UP", LENGTH_SHORT).show();
                }
            }
        });

        Right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(address != null) {
                    String sendTxt = "Right";
                    btt.write(sendTxt);
                    Toast.makeText(getApplicationContext(), "Right", LENGTH_SHORT).show();
                }
            }
        });

        Left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(address != null) {
                    String sendTxt = "Left";
                    btt.write(sendTxt);
                    Toast.makeText(getApplicationContext(), "Left", LENGTH_SHORT).show();
                }
            }
        });

        Down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (address != null) {
                    String sendTxt = "Down";
                    btt.write(sendTxt);
                    Toast.makeText(getApplicationContext(), "Down", LENGTH_SHORT).show();
                }
            }
        });

        Stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (address != null) {
                    String sendTxt = "Stop";
                    btt.write(sendTxt);
                    Toast.makeText(getApplicationContext(), "Stop", LENGTH_SHORT).show();
                }
            }

        });

    }
    public void onResume(){
        super.onResume();

        address = getIntent().getStringExtra("device address");

        if(address != null) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            mDevice = bluetoothAdapter.getRemoteDevice(address);
            connectedDevice.setText("Connected to " + mDevice.getName());
            connectedDevice.setTextColor(getResources().getColor(R.color.Green));
            try {
                mSocket = mDevice.createRfcommSocketToServiceRecord(myUuid);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
            }

            try {
                mSocket.connect();
            } catch (IOException e) {
                try {
                    mSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
            btt = new ConnectedThread(mSocket, mHandler);
            btt.start();

        }
    }
    public void onPause(){
        super.onPause();

        /*try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
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
