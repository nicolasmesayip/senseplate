package com.example.senseplate;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class AppSaves extends Application {
    private BluetoothSocket btSocket;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    private boolean isBtConnected = false;
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private String address;
    private static AppSaves app;

    public AppSaves(){
        app = this;
        btSocket = null;
    }
    public static AppSaves getInstance(){
        return app;
    }

    public ProgressDialog setBluetoothConnection(String address){
        this.address = address;
        new ConnectBT().execute();
        return progress;
    }

    public boolean isBtConnected(){
        return isBtConnected;
    }

    public void disconect(){
        isBtConnected = false;
        try{
            btSocket.close();
            btSocket = null;
        } catch (IOException e){
        }
    }

    public BluetoothSocket getBtSocket(){
        return btSocket;
    }

    public void prepareDialog(Activity activity){
        progress = new ProgressDialog(activity);
        progress.setTitle("Connecting");
        progress.setMessage("Please Wait!");
        progress.setCancelable(true);
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected  void onPreExecute () {
            progress.show();
        }

        @Override
        protected Void doInBackground (Void... devices) {
            try {
                if ( btSocket==null || !isBtConnected ) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute (Void result) {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
            } else {
                msg("Connected");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }
    private void msg (String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}
