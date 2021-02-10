package com.example.senseplate;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class BluetoothScan extends Activity {

    private BluetoothAdapter mBluetoothAdapter;
    public static final int REQUEST_ENABLE_BT = 1;
    public static final int REQUEST_ACCESS_COARSE_LOCATION = 1;
    public  static final int REQUEST_ENABLE_BLUETOOTH = 11;
    private ListView devicesList;
    private Button button;
    private ArrayAdapter<String> listAdapter;
    private String info, address;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor editor;
    private ArrayList<BluetoothDevice> deviceList;
    private TextView bluetooth_scan_header, devices_available_header;
    private int width, height;
    private String theme;
    private RelativeLayout rl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_scan);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        deviceList = new ArrayList<>();
        rl = (RelativeLayout) findViewById(R.id.rl);
        bluetooth_scan_header = (TextView) findViewById(R.id.bluetooth_scan_header);
        devices_available_header = (TextView) findViewById(R.id.devices_available_header);
        devicesList = (ListView) findViewById(R.id.devicesList);
        button = (Button) findViewById(R.id.button);
        // Get an instance of the BluetoothAdapter class
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        sharedPrefs = getSharedPreferences("com.example.senseplate", Context.MODE_PRIVATE);
        editor = sharedPrefs.edit();
        theme = sharedPrefs.getString(getString(R.string.theme), "light");
        if (theme.equals("light")) {
            listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
            devicesList.setAdapter(listAdapter);
        } else if (theme.equals("dark")){
            listAdapter = new ArrayAdapter<>(this, R.layout.spinner_ingredient);
            devicesList.setAdapter(listAdapter);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBluetoothAdapter == null) {
                    // If the adapter is null it means that the device does not support Bluetooth
                } else {
                    if (!mBluetoothAdapter.isEnabled()) {
                        // We need to enable the Bluetooth, so we ask the user
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        // REQUEST_ENABLE_BT es un valor entero que vale 1
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                        if (checkCoarseLocationPermission()) {
                            listAdapter.clear();
                            mBluetoothAdapter.startDiscovery();
                        }
                    } else {
                        if (!mBluetoothAdapter.isDiscovering()){
                            button.setEnabled(true);
                        }
                        if (checkCoarseLocationPermission()) {
                            listAdapter.clear();
                            mBluetoothAdapter.startDiscovery();
                        }
                    }
                }
            }
        });

        devicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mBluetoothAdapter.cancelDiscovery();
                info = ((TextView) view).getText().toString();
                address = info.substring(info.length() - 17);

                deviceList.get(position).createBond();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e){}
                AppSaves.getInstance().prepareDialog(BluetoothScan.this);
                AppSaves.getInstance().setBluetoothConnection(address);
                editor.putString(getString(R.string.bluetoothData), info);
                editor.commit();
            }

        });
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            // REQUEST_ENABLE_BT es un valor entero que vale 1
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        checkCoarseLocationPermission();
        if (!listAdapter.isEmpty()){
            button.setEnabled(false);
        } else {
            button.setEnabled(true);
        }
        resize();
        detectDarkMode();
    }

    @Override
    protected void onPause(){
        super.onPause();
        unregisterReceiver(devicesFoundReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(devicesFoundReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(devicesFoundReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        registerReceiver(devicesFoundReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));

    }

    private boolean checkCoarseLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_COARSE_LOCATION);
            return false;
        } else {
            return true;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BLUETOOTH){
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                // REQUEST_ENABLE_BT es un valor entero que vale 1
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case REQUEST_ACCESS_COARSE_LOCATION : if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // Access Coarse Location allowed
                listAdapter.clear();
                mBluetoothAdapter.startDiscovery();
            } else {
                // Access Coarse Location forbidden
            }
            break;
        }
    }
    private final BroadcastReceiver devicesFoundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                deviceList.add(device);
                try {
                    device.getName().equals(null);
                    listAdapter.add(device.getName() + "\n" + device.getAddress());
                } catch (NullPointerException n){
                    listAdapter.add("Unnamed\n" + device.getAddress());
                }
                listAdapter.notifyDataSetChanged();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                button.setEnabled(true);
                button.setText("Scan");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
                button.setEnabled(false);
                button.setText("Scanning in progress ...");
            }
        }
    };

    private void resize(){
        bluetooth_scan_header.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/ Resources.getSystem().getDisplayMetrics().density)*33/411);
        devices_available_header.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*21/411);
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/ Resources.getSystem().getDisplayMetrics().density)*12/411);

        LinearLayout.LayoutParams i1 = (LinearLayout.LayoutParams) devicesList.getLayoutParams();
        i1.setMargins(0,0,0,height * 1244/10000);
        RelativeLayout.LayoutParams b1 = (RelativeLayout.LayoutParams) button.getLayoutParams();
        b1.setMargins(0,0,0,height * 2928/100000);
    }
    private void detectDarkMode(){
        if (theme.equals("light")){
            rl.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            bluetooth_scan_header.setTextColor(getResources().getColor(R.color.colorBlack));
            devices_available_header.setTextColor(getResources().getColor(R.color.colorBlack));
            button.setTextColor(getResources().getColor(R.color.colorWhite));
            button.setBackgroundResource(R.drawable.show_chart_rounded);
        } else if (theme.equals("dark")){
            rl.setBackgroundColor(getResources().getColor(R.color.darkThemeBackground));
            bluetooth_scan_header.setTextColor(getResources().getColor(R.color.colorWhite));
            devices_available_header.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            button.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            button.setBackgroundResource(R.drawable.show_chart_rounded_dark);

        }
    }
}
