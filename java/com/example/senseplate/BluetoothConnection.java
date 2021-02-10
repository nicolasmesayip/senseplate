package com.example.senseplate;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Parcelable;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothConnection extends Activity {

    RelativeLayout rl;
    Button btnDisconnect, btnMode;
    ListView devicelist;
    TextView connection, bluetooth_header, paired_list_header;
    private  String info, address, theme;
    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    private ProgressDialog progress;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    private  View views;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static String EXTRA_ADDRESS = "device_address";
    private SharedPreferences sharedpref;
    private SharedPreferences.Editor editor;
    private int height, width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_list);
        // Initializes the widgets.
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        rl = (RelativeLayout) findViewById(R.id.rl);
        paired_list_header = (TextView) findViewById(R.id.paired_list_header);
        bluetooth_header = (TextView) findViewById(R.id.bluetooth_header);
        connection = (TextView) findViewById(R.id.connection);
        devicelist = (ListView) findViewById(R.id.listView);
        btnMode = (Button) findViewById(R.id.mode);
        btnDisconnect = (Button) findViewById(R.id.disconnect);
        sharedpref = getSharedPreferences("com.example.senseplate", Context.MODE_PRIVATE);
        theme = sharedpref.getString(getString(R.string.theme), "light");
        editor = sharedpref.edit();
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if ( myBluetooth==null ) {
            Toast.makeText(getApplicationContext(), "Bluetooth device not available", Toast.LENGTH_LONG).show();
            finish();
        } else if ( !myBluetooth.isEnabled() ) {
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon, 1);
        }

        devicelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                info = ((TextView) view).getText().toString();
                address = info.substring(info.length()-17);

                AppSaves.getInstance().prepareDialog(BluetoothConnection.this);
                ProgressDialog progress = AppSaves.getInstance().setBluetoothConnection(address);
                progress.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (AppSaves.getInstance().isBtConnected()){
                            connection.setVisibility(View.VISIBLE);
                            connection.setText("Device Conected: "+ info.substring(0, info.length()-17));
                            editor.putString(getString(R.string.bluetoothData), info);
                            editor.commit();
                            views = view;
                        }
                    }
                });
            }
        });

        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedpref.getString(getString(R.string.bluetoothData),"null").equals("null")){
                    return;
                } else {
                    AppSaves.getInstance().disconect();
                    connection.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Device Unpaired", Toast.LENGTH_LONG).show();
                    editor.putString(getString(R.string.bluetoothData), "null");
                    editor.commit();
                }
            }
        });
        btnMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), BluetoothScan.class);
                startActivity(i);
                pairedDevicesList();
            }
        });
        pairedDevicesList();
        resize();
        detectDarkMode();
        String data = sharedpref.getString(getString(R.string.bluetoothData), "null");
        if (data.equals("null")){
            connection.setVisibility(View.GONE);
        } else {
            try {
                if (AppSaves.getInstance().isBtConnected()) {
                    connection.setVisibility(View.VISIBLE);
                    connection.setText("Device Conected: " + data.substring(0, data.length() - 17));
                } else{
                    connection.setVisibility(View.GONE);
                }
            } catch (Exception e){}
        }

    }
    private void pairedDevicesList () {
        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();

        if ( pairedDevices.size() > 0 ) {
            for (BluetoothDevice bt : pairedDevices) {
                list.add(bt.getName() + "\n" + bt.getAddress());
            }
        } else {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }

        if (theme.equals("light")) {
            final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
            devicelist.setAdapter(adapter);
        } else if (theme.equals("dark")){
            final ArrayAdapter adapter = new ArrayAdapter(this, R.layout.spinner_ingredient, list);
            devicelist.setAdapter(adapter);
        }

    }

    private void resize(){
        bluetooth_header.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/ Resources.getSystem().getDisplayMetrics().density)*33/411);
        paired_list_header.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*21/411);
        connection.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*21/411);
        btnDisconnect.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/ Resources.getSystem().getDisplayMetrics().density)*12/411);
        btnMode.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/ Resources.getSystem().getDisplayMetrics().density)*12/411);
    }

    private void detectDarkMode(){
        if (theme.equals("light")){
            rl.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            bluetooth_header.setTextColor(getResources().getColor(R.color.colorBlack));
            paired_list_header.setTextColor(getResources().getColor(R.color.colorBlack));
            connection.setTextColor(getResources().getColor(R.color.colorBlack));
            btnDisconnect.setTextColor(getResources().getColor(R.color.colorWhite));
            btnDisconnect.setBackgroundResource(R.drawable.clear_button_rounded);
        } else if (theme.equals("dark")){
            rl.setBackgroundColor(getResources().getColor(R.color.darkThemeBackground));
            bluetooth_header.setTextColor(getResources().getColor(R.color.colorWhite));
            paired_list_header.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            connection.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            btnDisconnect.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            btnDisconnect.setBackgroundResource(R.drawable.clear_button_rounded_dark);
        }
    }


}