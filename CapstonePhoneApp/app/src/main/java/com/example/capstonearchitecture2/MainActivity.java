package com.example.capstonephoneapp;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice = null;

    final byte delimiter = 33;
    int readBufferPosition = 0;

//    final UUID uuid = UUID.fromString("asdf");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_ConsoleLog = findViewById(R.id.btn_consoleLog);
        Button btn_beginPairing = findViewById(R.id.btn_beginPairing);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        final Handler handler = new Handler();
        final class workerThread implements Runnable{
            private String btMsg;

            public workerThread(String msg){
                btMsg = msg;
            }

            public void run(){
                sendBtMsg(btMsg); // sends message
                while(!Thread.currentThread().isInterrupted()){
                    int bytesAvailable;
                    boolean workDone = false;
                    try{
                        final InputStream mmInputStream;
                        mmInputStream = mmSocket.getInputStream();
                        bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0){
                            byte[] packetBytes = new byte[bytesAvailable];
                            byte[] readBuffer = new byte[1024]; // create a buffer
                            mmInputStream.read(packetBytes);

                            for(int i = 0; i < bytesAvailable; i++){
                                byte b = packetBytes[i];
                                if(b == delimiter){
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable()
                                    {
                                        public void run(){
                                            Log.e(data, "received data");
                                        }
                                    });

                                    workDone = true;
                                    break;
                                }else{
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                            if(workDone == true) {
                                mmSocket.close();
                                break;
                            }
                        }
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }


        btn_ConsoleLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // begin classification
                MainActivity.this.startActivity(new Intent(MainActivity.this, com.example.capstonephoneapp.SliderParent.class));
            }
        });

        btn_beginPairing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // required: device address and service UUID
                // device address: (ex: 00:72:02:97:33:2C) can be obtained from paired devices (or by allowing for discovery), see Android example app for more on that.
                // Runnable thread that sends and receives info via bluetooth RFCOMM
                (new Thread(new workerThread("hello pi"))).start();
            }
        });
    }

    // sends information to Pi, android acts as client
    public void sendBtMsg(String msgToSend){
//        try{
//            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
//            if(!mmSocket.isConnected()){
//                mmSocket.connect();
//            }
//
//            String msg = msgToSend;
//            OutputStream mmOutputStream = mmSocket.getOutputStream();
//            mmOutputStream.write(msg.getBytes()); // pushes message through socket
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}