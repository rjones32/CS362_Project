package com.example.ryanjones.bluetoothdoorkeys;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


/**
 * Created by Ryan Jones on 4/9/2015.
 */
public class BTConnect {

    //Debugging
    private static final String TAG = "BTLog";

    //Unique UUID for this app
    private static final UUID MY_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");


    //Member fields
    private final BluetoothAdapter BTOps;
    private final Handler mHandler;
    ConnectThread mConnectThread = null;
    public ConnectedThread mConnectedThread = null;
    private int mState;

    //Constants that indicate the current connection state
    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;
    private static String deviceAddress = "20:15:03:03:04:05";

    /*Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            byte[] writeBuf = (byte[]) msg.obj;
            int begin = (int) msg.arg1;
            int end = (int) msg.arg2;

            switch (msg.what) {
                case 1:
                    String writeMessage = new String(writeBuf);
                    writeMessage = writeMessage.substring(begin, end);
                    break;

            }

        }

    };*/

    public BTConnect(Handler handler ){
        BTOps = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
    }

    /**
     * Set the current state of the chat connection
     *
     * @param state An integer defining the current connection state
     */
    private synchronized void setState(int state) {
        Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    public boolean setup(){
        if(BTOps==null){
            return false;


        }

        if(!BTOps.isEnabled()){
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //startActivityForResult(enableBTIntent,1);

        }

        return true;

    }

       /*public byte recieve(){
           byte[] data;
           ConnectedThread r;
           r = mConnectedThread;




       }*/

        public void write(byte[] out){

            ConnectedThread r;

            r = mConnectedThread;

            r.write(out);

        }

    //Return the current connection state
    public synchronized int getState() { return mState; }



    public ConnectedThread getBondedDevices() {




        //code from: http://developer.android.com/guide/topics/connectivity/bluetooth.html#FindingDevices
        Set<BluetoothDevice> bondedDevices = BTOps.getBondedDevices();
        Log.i(TAG,"In getBondedDevices");
        // If there are paired devices
        if (bondedDevices.size() > 0) {
            // Loop through paired devices
            Log.i(TAG,"In loop through");
            for (BluetoothDevice device : bondedDevices) {
                String address = device.getAddress();
                if (address.equals(deviceAddress)) {
                    mConnectThread = new ConnectThread(device);
                    Log.i(TAG,"About to set up a connection");
                    mConnectThread.start();
                    return mConnectedThread;


                }

            }
            Log.i(TAG,"Device has not been found");
        }
        return mConnectedThread;
    }

    public void connectionLost(){


        BTConnect.this.getBondedDevices();


    }
//Code from: http://developer.android.com/guide/topics/connectivity/bluetooth.html#ManagingAConnection

    public synchronized void stop(){
        if(mConnectThread != null){
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if(mConnectedThread !=null){
            mConnectedThread.cancel();
            mConnectedThread = null;

        }

        setState(STATE_NONE);
    }


    private class ConnectThread extends Thread{

        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;


        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;
            Log.i(TAG,"In ConnectThread");
            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) { }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            BTOps.cancelDiscovery();
            Log.i(TAG,"In run of connect thread");
            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            mConnectedThread = new ConnectedThread(mmSocket);
            mConnectedThread.start();
            setState(STATE_CONNECTED);


        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }


    }

    public class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.i(TAG,"create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG,"Begin mConnectedThread");
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);

                    mHandler.obtainMessage(Constants.MESSAGE_READ,bytes,-1,buffer)
                            .sendToTarget();

                } catch (IOException e) {
                    connectionLost();

                    BTConnect.this.getBondedDevices();
                    break;
                }
            }
        }



        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {

               Log.e(TAG,"Exception during write",e);
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    public BluetoothAdapter bluetoothAdapter(){ return BTOps;}

}


