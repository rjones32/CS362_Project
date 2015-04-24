package com.example.ryanjones.bluetoothdoorkeys;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.Set;

/**
 * Created by Ryan Jones on 4/9/2015.
 */
public class Bluetooth_operations {
    protected int doorValue;
    protected Boolean isDoorLock;
    String TAG = "BTLog";

    protected Bluetooth_operations(){
        isDoorLock = false;
    }

    protected Boolean isDoorLock(){ return isDoorLock;

    }

    protected void isDoorLock(String datapacket){


        if(datapacket.equals("1")) {
            Log.i(TAG, "DoorValue: "+doorValue);

            isDoorLock = true;
            doorValue  = 1;
        }

        else if(datapacket.equals("0")){
            Log.i(TAG, "DoorValue: "+doorValue);
            isDoorLock = false;
            doorValue  = 0;
        }

    }


    protected void recieveData(int data){



    }

}
