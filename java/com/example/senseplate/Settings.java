package com.example.senseplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

public class Settings extends Activity {

    Button btnLogout, b1,b2,b3,b4, btnChangePassword, btnBluetooth;
    TextView settings, username_display, line, line2, line3, line4;
    int height, width;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    String usernames, theme, autosavingString;
    RelativeLayout ry;
    LinearLayout ly;
    Switch darkmode, autosaving;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);


        // Initializes the widgets.
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        sharedPref = getSharedPreferences("com.example.senseplate", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        theme = sharedPref.getString(getString(R.string.theme), "light");
        autosavingString = sharedPref.getString(getString(R.string.autosaving), "yes");

        ry = (RelativeLayout) findViewById(R.id.ry);
        ly = (LinearLayout) findViewById(R.id.ly);

        settings = (TextView) findViewById(R.id.settings_header);
        username_display = (TextView) findViewById(R.id.username_display);
        line = (TextView) findViewById(R.id.line);
        line2 = (TextView) findViewById(R.id.line2);
        line3 = (TextView) findViewById(R.id.line3);
        line4 = (TextView) findViewById(R.id.line4);

        darkmode = (Switch) findViewById(R.id.darkmode);
        autosaving = (Switch) findViewById(R.id.autosaving);

        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnChangePassword = (Button) findViewById(R.id.btnChangePassword);
        btnBluetooth = (Button) findViewById(R.id.bluetooth);
        b1 = (Button) findViewById(R.id.button1);
        b2 = (Button) findViewById(R.id.button2);
        b3 = (Button) findViewById(R.id.button3);
        b4 = (Button) findViewById(R.id.button4);
        InitSharedPrefs();
        RelativeLayout.LayoutParams i1 = (RelativeLayout.LayoutParams) btnLogout.getLayoutParams();
        i1.setMargins(0,0,0,height*8784/100000);
        b1.setWidth(width*25/100);
        b2.setWidth(width*25/100);
        b3.setWidth(width*25/100);
        b4.setWidth(width*25/100);
        username_display.setText("@"+usernames);
        settings.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/ Resources.getSystem().getDisplayMetrics().density)*33/411);
        username_display.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/ Resources.getSystem().getDisplayMetrics().density)*24/411);
        btnLogout.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/ Resources.getSystem().getDisplayMetrics().density)*12/411);

        darkmode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    editor.putString(getString(R.string.theme), "dark");
                    editor.commit();
                    detectDarkMode();
                } else {
                    editor.putString(getString(R.string.theme), "light");
                    editor.commit();
                    detectDarkMode();
                }
            }
        });
        autosaving.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    editor.putString(getString(R.string.autosaving), "yes");
                    editor.commit();
                    detectDarkMode();
                } else {
                    editor.putString(getString(R.string.autosaving), "no");
                    editor.commit();
                    detectDarkMode();
                }
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Sets the event for the button to Log Out.
                editor.putString(getString(R.string.username), "");
                editor.putString(getString(R.string.verification), "");
                editor.putString(getString(R.string.lastSearch), "");
                editor.putString(getString(R.string.bluetoothData), "null");
                editor.commit();
                Intent i = new Intent(getApplicationContext(), Auth.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ChangePassword.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });
        btnBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), BluetoothConnection.class);
                startActivity(i);
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();

            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AddFoodActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), History.class);
                startActivity(i);
            }
        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        if (theme.equals("dark")) {
            darkmode.setChecked(true);
        } else if(theme.equals("light")){
            darkmode.setChecked(false);
        }
        if (autosavingString.equals("yes")){
            autosaving.setChecked(true);
        } else {
            autosaving.setChecked(false);
        }
        detectDarkMode();
    }
    private void InitSharedPrefs(){
        usernames = sharedPref.getString(getString(R.string.username), " ");

    }
    public void detectDarkMode(){
        theme = sharedPref.getString(getString(R.string.theme), "light");
        if (theme.equals("light")){
            ry.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            settings.setTextColor(getResources().getColor(R.color.colorBlack));
            b1.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            b2.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            b3.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            b4.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            b1.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_home_black_24dp),null,null);
            b2.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_library_add_black_24dp),null,null);
            b3.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_history_black_24dp),null,null);
            b4.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_settings_green_24dp),null,null);
            b1.setTextColor(getResources().getColor(R.color.darkThemeBackground));
            b2.setTextColor(getResources().getColor(R.color.darkThemeBackground));
            b3.setTextColor(getResources().getColor(R.color.darkThemeBackground));
            b4.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            btnLogout.setBackgroundResource(R.drawable.clear_button_rounded);
            btnLogout.setTextColor(getResources().getColor(R.color.colorWhite));
            darkmode.setTextColor(getResources().getColor(R.color.colorBlack));
            autosaving.setTextColor(getResources().getColor(R.color.colorBlack));

            //btnChangePassword.setBackgroundResource(R.drawable.topbottomborder);
            btnChangePassword.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            btnChangePassword.setTextColor(getResources().getColor(R.color.colorBlack));
            username_display.setTextColor(getResources().getColor(R.color.colorBlack));
            //username_display.setBackgroundResource(R.drawable.topbottom);
            line.setBackgroundColor(getResources().getColor(R.color.colorDarkGrey));
            line2.setBackgroundColor(getResources().getColor(R.color.colorDarkGrey));
            line3.setBackgroundColor(getResources().getColor(R.color.colorDarkGrey));
            line4.setBackgroundColor(getResources().getColor(R.color.colorDarkGrey));

            btnBluetooth.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            btnBluetooth.setTextColor(getResources().getColor(R.color.colorBlack));


        } else if (theme.equals("dark")){
            ry.setBackgroundColor(getResources().getColor(R.color.darkThemeBackground));
            settings.setTextColor(getResources().getColor(R.color.colorWhite));
            b1.setBackgroundColor(getResources().getColor(R.color.darkThemeBackground));
            b2.setBackgroundColor(getResources().getColor(R.color.darkThemeBackground));
            b3.setBackgroundColor(getResources().getColor(R.color.darkThemeBackground));
            b4.setBackgroundColor(getResources().getColor(R.color.darkThemeBackground));
            b1.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_home_white_24dp),null,null);
            b2.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_library_add_white_24dp),null,null);
            b3.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_history_white_24dp),null,null);
            b4.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_settings_blue_24dp),null,null);
            b1.setTextColor(getResources().getColor(R.color.colorWhite));
            b2.setTextColor(getResources().getColor(R.color.colorWhite));
            b3.setTextColor(getResources().getColor(R.color.colorWhite));
            b4.setTextColor(getResources().getColor(R.color.colorBlue));
            btnLogout.setBackgroundResource(R.drawable.clear_button_rounded_dark);
            btnLogout.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            darkmode.setTextColor(getResources().getColor(R.color.colorBlue));
            autosaving.setTextColor(getResources().getColor(R.color.colorBlue));

            //btnChangePassword.setBackgroundResource(R.drawable.topbottomborder_dark);
            btnChangePassword.setBackgroundColor(getResources().getColor(R.color.darkThemeBackground));
            btnChangePassword.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            username_display.setTextColor(getResources().getColor(R.color.colorWhite));
            //username_display.setBackgroundResource(R.drawable.topbottom_dark);
            line.setBackgroundColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            line2.setBackgroundColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            line3.setBackgroundColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            line4.setBackgroundColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            btnBluetooth.setBackgroundColor(getResources().getColor(R.color.darkThemeBackground));
            btnBluetooth.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
    }
}
