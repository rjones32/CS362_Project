package com.example.ryanjones.bluetoothdoorkeys;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
   private static final String TAG = "BTLog";
   private BTConnect nBTConnection;
   private BluetoothAdapter mBluetoothAdapter = null;
   Button onOffBtn;
   TextView sensorView,textView;
   Bluetooth_operations ops;
   String data;
   int inputData;
   boolean buttonClick;




    int doorValue = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        onOffBtn    = (Button)this.findViewById(R.id.button1);
        buttonClick = false;
        sensorView  = (TextView) findViewById(R.id.sensorView);


        Log.i(TAG, "In onCreate");

        checkBTState();
        nBTConnection = new BTConnect(mHandler);
        checkBTState();
        ops = new Bluetooth_operations();




        onOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClick =true;
                Log.i(TAG, "getting data");
                sensorView.setText("Data: " + ops.doorValue);

                    if(ops.isDoorLock()==true) {
                        Log.i(TAG, "About to open door");
                        Toast.makeText(getBaseContext(), "Open Door", Toast.LENGTH_SHORT).show();
                        data = "0";
                        byte[] send = data.getBytes();
                       nBTConnection.mConnectedThread.write(send);

                    }
                    else {
                        Log.i(TAG, "About to close door");
                        Toast.makeText(getBaseContext(), "Close Door", Toast.LENGTH_SHORT).show();
                        data = "1";
                        byte[] send = data.getBytes();
                        nBTConnection.mConnectedThread.write(send);
                    }




            }
        });



        }
   public void onResume(){
           super.onResume();

           if(nBTConnection!=null) {

                if(nBTConnection.getState()== BTConnect.STATE_NONE){
                    nBTConnection.getBondedDevices();

                }
           }


       }



    public void onDestroy(){
        super.onDestroy();
        if(nBTConnection!=null){
            Toast.makeText(getBaseContext(), "Close Door", Toast.LENGTH_SHORT).show();
            data = "1";
            byte[] send = data.getBytes();
            nBTConnection.mConnectedThread.write(send);
            nBTConnection.stop();

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void sendData(int inputData){
        String data;
        if(inputData==0 || inputData==1){
            data =   Integer.toString(inputData);
            byte[] send = data.getBytes();
            nBTConnection.write(send);
        }

    }

    private void checkBTState(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null){
            Toast.makeText(getBaseContext(),"Bluetooth not supported",Toast.LENGTH_SHORT).show();
        }
        else{
            if(mBluetoothAdapter.isEnabled()){
                Log.d(TAG,"BlueTooth On");

            }
            else{
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent,1);

            }
        }

    }

    public void getData(String newData){ data = newData;}


   private final Handler mHandler = new Handler(){

       public void handleMessage(Message msg){

       switch (msg.what) {
           case Constants.MESSAGE_WRITE:
               byte [] writeBuf = (byte[])msg.obj;
               String writeMessage = new String(writeBuf);


               break;
           case Constants.MESSAGE_READ:
               byte[] readBuf = (byte[]) msg.obj;
               String readMessage = new String(readBuf,0,msg.arg1);

               ops.isDoorLock(readMessage);
               sensorView.setText("Data: " + ops.doorValue);
               if(buttonClick==false){
                   if(ops.isDoorLock()==true) {
                       //Log.i(TAG, "About to open door");
                       //Toast.makeText(getBaseContext(), "Open Door", Toast.LENGTH_SHORT).show();
                       data = "0";
                       byte[] send = data.getBytes();
                       nBTConnection.mConnectedThread.write(send);
                   }

               }


               break;
       }
      }

    };



}
