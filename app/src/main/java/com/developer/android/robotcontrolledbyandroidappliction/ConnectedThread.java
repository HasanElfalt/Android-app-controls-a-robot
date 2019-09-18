package com.developer.android.robotcontrolledbyandroidappliction;

import android.bluetooth.BluetoothSocket;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import android.os.Handler;

public class ConnectedThread extends Thread {

    private final BluetoothSocket mSocket;
    private InputStream mInStream;
    private OutputStream mOutStream;
    public static final int RESPONSE_MESSAGE = 20;

    Handler uih;

    public ConnectedThread(BluetoothSocket mSocket, Handler uih) {
        this.mSocket = mSocket;
        this.uih     = uih;
        InputStream tmpIn   = null;
        OutputStream tmpOut = null;

        Log.i("[THREAD]","Creating thread");

        try {
            mInStream  = mSocket.getInputStream();
            mOutStream = mSocket.getOutputStream();

        } catch (IOException e) {
            Log.e("[THREAD]","Error:"+e.getMessage());
        }
        mInStream  = tmpIn;
        mOutStream = tmpOut;

        try {
            mOutStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    public void run(){

        BufferedReader br = new BufferedReader(new InputStreamReader(mInStream));

        while (true) {
            try {
                String resp = br.readLine();
                Message msg = new Message();
                msg.what = RESPONSE_MESSAGE;
                msg.obj = resp;
                uih.sendMessage(msg);

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

        }
    }
    public void write(byte[] bytes){

        try {
            mOutStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void cancel(){
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
