package com.example.senseplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ChangePassword extends Activity {
    String username, code, emailString, emailcontent, password, theme;
    TextView title, line, info, error, forgotpassword;
    EditText currentpassword, newpassword, confirmpassword, email, verification;
    Button btnCheckIdentity, btnSendEmail, btnUpdateForgot;
    LinearLayout ly;
    int width, height, verification_number;
    boolean specialchar = false;
    boolean number = false;
    boolean uppercase = false;
    boolean lowercase = false;
    boolean eightchar = false;
    boolean verificate = false;
    boolean forgotPasswordPressed = false;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_EMAIL = "email";

    JSONParser jsonParser = new JSONParser();

    // Method called when the Activity is created.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        // Initializes the widgets.
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        sharedPref = getSharedPreferences("com.example.senseplate", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        InitSharedPrefs();

        ly = (LinearLayout) findViewById(R.id.ly);

        title = (TextView) findViewById(R.id.title);
        line = (TextView) findViewById(R.id.line);
        info = (TextView) findViewById(R.id.info);
        error = (TextView) findViewById(R.id.error);
        forgotpassword = (TextView) findViewById(R.id.forgotpassword);

        currentpassword = (EditText) findViewById(R.id.currentpassword);
        newpassword = (EditText) findViewById(R.id.newpassword);
        confirmpassword = (EditText) findViewById(R.id.confirmpassword);
        email = (EditText) findViewById(R.id.email);
        verification = (EditText) findViewById(R.id.verification);

        btnCheckIdentity = (Button) findViewById(R.id.btnCheckIdentity);
        btnSendEmail = (Button) findViewById(R.id.btnSendEmail);
        btnUpdateForgot = (Button) findViewById(R.id.btnUpdateForgot);

        btnCheckIdentity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentpassword.getText().toString().length() > 0 && newpassword.getText().toString().length() > 0 && confirmpassword.getText().toString().length() > 0){
                    if (newpassword.getText().toString().equals(confirmpassword.getText().toString())){
                        passwordCheck(newpassword.getText().toString());
                        if (specialchar && number && uppercase && lowercase && eightchar) {
                            new GetEmail().execute();
                        }
                    } else {
                        setError("The passwords do not match");
                    }
                } else {
                    setError("Required fields are empty");
                }

            }
        });
        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verification.getVisibility() == View.VISIBLE){
                    emailVerification();
                    InitSharedPrefs();
                } else {
                    new GetEmail().execute();
                }
            }
        });
        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPasswordPressed = true;
                currentpassword.setVisibility(View.GONE);
                newpassword.setVisibility(View.GONE);
                confirmpassword.setVisibility(View.GONE);
                forgotpassword.setVisibility(View.GONE);
                email.setVisibility(View.VISIBLE);
                btnCheckIdentity.setVisibility(View.GONE);
                btnSendEmail.setVisibility(View.VISIBLE);
            }
        });
        btnUpdateForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newpassword.getText().toString().length() > 0 && confirmpassword.getText().toString().length() > 0){
                    if (newpassword.getText().toString().equals(confirmpassword.getText().toString())){
                        passwordCheck(newpassword.getText().toString());
                        if (specialchar && number && uppercase && lowercase && eightchar) {
                            new UpdatePassword().execute();
                        }
                    } else {
                        setError("The passwords do not match");
                    }
                } else {
                    setError("Required fields are empty");
                }
            }
        });
        verification.addTextChangedListener(new VerificationTextWatcher());
        resize();
        detectDarkMode();
    }
    public void setError(String message){
        error.setText(message);
        error.setVisibility(View.VISIBLE);
    }
    // Method that validates the password
    public void passwordCheck(String password){
        // Contains an special character.
        Pattern regex = Pattern.compile("[!@£$%^&*()_+={}'\\\\|;:/?.>,<#€`~]");
        if (regex.matcher(password).find()){
            specialchar = true;
        } else{
            setError("The password must contain an special character");
        }
        // Contains a number
        Pattern regex1 = Pattern.compile("[0-9]");
        if (regex1.matcher(password).find()){
            number = true;
        }else{
            setError("The password must contain a number");
        }
        // Contains a lowercase character.
        Pattern regex2 = Pattern.compile("[a-z]");
        if (regex2.matcher(password).find()){
            lowercase = true;
        }else{
            setError("The password must contain a lowercase letter");
        }
        // Contains an uppercase character.
        Pattern regex3 = Pattern.compile("[A-Z]");
        if (regex3.matcher(password).find()){
            uppercase = true;
        }else{
            setError("The password must contain an uppercase letter");
        }
        // Length is greater than eight characters.
        if (password.length() > 8){
            eightchar = true;
        }else{
            setError("The password must be 8 characters long or more");
        }
    }

    // Class that performs the connection to the database to continue with the process of
    // login/register.
    // The class AsyncTask is the superclass that enables to perform tasks in the background.
    private class GetEmail extends AsyncTask<String, String, String> {
        boolean check = false;
        // Method to set the variables that will be used in the connection.
        protected void onPreExecute(){
            super.onPreExecute();
            if (email.getVisibility() == View.VISIBLE){
                password = "a";
            } else {
                password = currentpassword.getText().toString();
            }
        }
        protected String doInBackground(String... args){
            String url = "http://nam33.student.eda.kent.ac.uk/getEmail.php";
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // Creating an ArrayList and adding the variables that will be sent.
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("password", password));

            // Calls the method that makes the connection passing the URL variables, the Method and
            // the ArrayList as parameters.
            JSONObject json = jsonParser.makeHttpRequest(url, "GET", params);

            try{
                // Checks if the operation was performed successfully.
                int success = json.getInt(TAG_SUCCESS);
                // Success Login
                if (success == 1) {
                    emailString = json.getString("email");
                } else {
                    check = true;
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (!check){
                if (email.getVisibility()==View.GONE) {
                    emailVerification();
                    email.setVisibility(View.GONE);
                    currentpassword.setVisibility(View.GONE);
                    newpassword.setVisibility(View.GONE);
                    confirmpassword.setVisibility(View.GONE);
                    forgotpassword.setVisibility(View.GONE);
                    btnCheckIdentity.setVisibility(View.GONE);
                    verification.setVisibility(View.VISIBLE);
                    btnSendEmail.setText("RE-SEND EMAIL");
                    btnSendEmail.setVisibility(View.VISIBLE);
                    error.setVisibility(View.INVISIBLE);
                    InitSharedPrefs();
                } else {
                    if (emailString.equals(email.getText().toString())){
                        emailVerification();
                        email.setVisibility(View.GONE);
                        error.setVisibility(View.INVISIBLE);
                        verification.setVisibility(View.VISIBLE);
                        btnSendEmail.setText("RE-SEND EMAIL");
                        InitSharedPrefs();
                    }else {
                        setError("Invalid Email");
                    }
                }
            } else {
                setError("Wrong password");
            }
        }
    }
    public void emailVerification(){
        verification_number = (int)(Math.random()*((999999-100000)+1))+100000;

        emailcontent = "<table border=\"1\" width=\"100%\" style=\"background-color: #e1e3e6;\">\n" +
                "\t\t<td>\n" +
                "\t\t<table width=\"90%\" align=\"center\" style=\"border-collapse: collapse; background-color: #ffffff;\">\n" +
                "\t\t\t<tr> <td align=\"center\" bgcolor=\"#70bbd9\" style=\"padding: 40px 0 30px 0;\"> <img src=\"http://nam33.student.eda.kent.ac.uk/Logo.png\" alt=\""+ verification_number +"\" width=\"500\" height=\"230\" style=\"display: block;\" /></td></tr>\n" +
                "\t\t\t<tr> <td align=\"center\" style=\"padding-top: 20px;font-size: 25px;\"> This is a verification code generated due to a request for changing the password <br> Your verification code is the following:</td></tr>\n" +
                "\t\t\t<tr> <td align=\"center\" style=\"font-size: 40px; font-weight: bold; padding-top: 20px; padding-bottom: 20px;\" >"+ verification_number +"</td></tr>\n" +
                "\t\t\t<tr> <td align=\"center\" style=\"font-size: 25px; padding-bottom: 20px;\"> This code is valid for 10 minutes. After that time, you will need to request another code. <br><br>If you have not requested a change of password, please contact us through this email.</td></tr>\n" +
                "\t\t</table>\n" +
                "\t</td>\n" +
                "\t</table>";

        new SendMail(ChangePassword.this).execute("nam33@kent.ac.uk",
                "#Golfista7", emailString, "Change Password Verification", emailcontent);
        editor.putString(getString(R.string.time), String.valueOf(System.currentTimeMillis()));
        editor.putString(getString(R.string.verification_password), String.valueOf(verification_number));
        editor.commit();
        verificate = true;
    }
    private void InitSharedPrefs(){
        username = sharedPref.getString(getString(R.string.username), "");
        code = sharedPref.getString(getString(R.string.verification_password), "");
    }

    private class VerificationTextWatcher implements TextWatcher {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().length() == 6 ){
                try {
                    long time = Long.parseLong(sharedPref.getString(getString(R.string.time), ""));
                    if ((System.currentTimeMillis() - time) > 600000){
                        verificate = false;
                    }
                } catch (Exception e){

                }

                // Checks if the verification code is correct.
                if (Integer.parseInt(verification.getText().toString()) == Integer.parseInt(code) && verificate) {
                    if (forgotPasswordPressed){
                        newpassword.setVisibility(View.VISIBLE);
                        confirmpassword.setVisibility(View.VISIBLE);
                        btnUpdateForgot.setVisibility(View.VISIBLE);
                        verification.setVisibility(View.GONE);
                        btnSendEmail.setVisibility(View.GONE);
                        RelativeLayout.LayoutParams r2 = (RelativeLayout.LayoutParams) newpassword.getLayoutParams();
                        r2.setMargins(width*1459/10000,0,width*1459/10000,0);
                        RelativeLayout.LayoutParams r3 = (RelativeLayout.LayoutParams) confirmpassword.getLayoutParams();
                        r3.setMargins(width*1459/10000,width*1095/10000,width*1459/10000,0);
                    } else {
                        new UpdatePassword().execute();
                    }
                } else if(!verificate){
                    setError("Timed out");
                } else {
                    setError("The verification code introduced is incorrect.");
                }
            } else if (error.getVisibility()== View.VISIBLE && s.toString().length() <= 5){
                error.setVisibility(View.INVISIBLE);
            }
        }
    }


    private class UpdatePassword extends AsyncTask<String, String, String> {
        boolean check = false;
        // Method to set the variables that will be used in the connection.
        protected void onPreExecute(){
            super.onPreExecute();
            password = newpassword.getText().toString();
        }
        protected String doInBackground(String... args){
            String url = "http://nam33.student.eda.kent.ac.uk/update_password.php";
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // Creating an ArrayList and adding the variables that will be sent.
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("password", password));

            // Calls the method that makes the connection passing the URL variables, the Method and
            // the ArrayList as parameters.
            JSONObject json = jsonParser.makeHttpRequest(url, "POST", params);

            try{
                // Checks if the operation was performed successfully.
                int success = json.getInt(TAG_SUCCESS);
                // Success Login
                if (success == 1) {
                    check = true;
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (check){
                Toast.makeText(getApplicationContext(), "Password updated...",Toast.LENGTH_LONG).show();
                editor.putString(getString(R.string.verification_password), code);
                editor.commit();
                Intent i = new Intent(getApplicationContext(), ChangePassword.class);
                startActivity(i);
            }
        }
    }
    public void resize(){
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/ Resources.getSystem().getDisplayMetrics().density)*20/411);
        LinearLayout.LayoutParams t1 = (LinearLayout.LayoutParams) info.getLayoutParams();
        t1.setMargins(width*4866/100000,width*3649/100000,width*4866/100000,0);
        info.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/ Resources.getSystem().getDisplayMetrics().density)*15/411);
        RelativeLayout.LayoutParams r1 = (RelativeLayout.LayoutParams) currentpassword.getLayoutParams();
        r1.setMargins(width*1459/10000,0,width*1459/10000,0);
        RelativeLayout.LayoutParams r2 = (RelativeLayout.LayoutParams) newpassword.getLayoutParams();
        r2.setMargins(width*1459/10000,width*1946/10000,width*1459/10000,0);
        RelativeLayout.LayoutParams r3 = (RelativeLayout.LayoutParams) confirmpassword.getLayoutParams();
        r3.setMargins(width*1459/10000,width*3041/10000,width*1459/10000,0);
        currentpassword.setHeight(width*7299/100000);
        newpassword.setHeight(width*7299/100000);
        confirmpassword.setHeight(width*7299/100000);
        currentpassword.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/ Resources.getSystem().getDisplayMetrics().density)*14/411);
        newpassword.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/ Resources.getSystem().getDisplayMetrics().density)*14/411);
        confirmpassword.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/ Resources.getSystem().getDisplayMetrics().density)*14/411);
        forgotpassword.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/ Resources.getSystem().getDisplayMetrics().density)*14/411);
        RelativeLayout.LayoutParams r4 = (RelativeLayout.LayoutParams) btnCheckIdentity.getLayoutParams();
        r4.setMargins(0,width*4379/10000,width*1459/10000,0);
        btnCheckIdentity.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/ Resources.getSystem().getDisplayMetrics().density)*12/411);
        email.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/ Resources.getSystem().getDisplayMetrics().density)*14/411);
        verification.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/ Resources.getSystem().getDisplayMetrics().density)*14/411);
        btnSendEmail.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/ Resources.getSystem().getDisplayMetrics().density)*12/411);
        RelativeLayout.LayoutParams r5 = (RelativeLayout.LayoutParams) btnSendEmail.getLayoutParams();
        r5.setMargins(0,width*1459/10000,width*1459/10000,0);
        RelativeLayout.LayoutParams r8 = (RelativeLayout.LayoutParams) btnUpdateForgot.getLayoutParams();
        r8.setMargins(0,width*2200/10000,width*1459/10000,0);
        RelativeLayout.LayoutParams r6 = (RelativeLayout.LayoutParams) email.getLayoutParams();
        r6.setMargins(width*1459/10000,0,width*1459/10000,0);
        RelativeLayout.LayoutParams r7 = (RelativeLayout.LayoutParams) verification.getLayoutParams();
        r7.setMargins(width*3600/10000,0,width*3600/10000,0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (verification.getVisibility() == View.VISIBLE || email.getVisibility() == View.VISIBLE){
            Intent i = new Intent(getApplicationContext(), ChangePassword.class);
            startActivity(i);
        } else if (btnCheckIdentity.getVisibility() == View.VISIBLE){
            Intent i = new Intent(getApplicationContext(), Settings.class);
            startActivity(i);
        }
    }

    public void detectDarkMode(){
        theme = sharedPref.getString(getString(R.string.theme), "light");
        if (theme.equals("light")){
            ly.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            title.setTextColor(getResources().getColor(R.color.colorBlack));
            title.setBackgroundColor(getResources().getColor(R.color.colorGreyLight));
            line.setBackgroundColor(getResources().getColor(R.color.colorBlack));
            info.setTextColor(getResources().getColor(R.color.colorBlack));
            currentpassword.setBackgroundResource(R.drawable.search_field_rounded);
            newpassword.setBackgroundResource(R.drawable.search_field_rounded);
            confirmpassword.setBackgroundResource(R.drawable.search_field_rounded);
            email.setBackgroundResource(R.drawable.search_field_rounded);
            verification.setBackgroundResource(R.drawable.search_field_rounded);
            currentpassword.setHintTextColor(getResources().getColor(R.color.colorGrey));
            newpassword.setHintTextColor(getResources().getColor(R.color.colorGrey));
            confirmpassword.setHintTextColor(getResources().getColor(R.color.colorGrey));
            email.setHintTextColor(getResources().getColor(R.color.colorGrey));
            verification.setHintTextColor(getResources().getColor(R.color.colorGrey));
            currentpassword.setTextColor(getResources().getColor(R.color.colorBlack));
            newpassword.setTextColor(getResources().getColor(R.color.colorBlack));
            confirmpassword.setTextColor(getResources().getColor(R.color.colorBlack));
            email.setTextColor(getResources().getColor(R.color.colorBlack));
            verification.setTextColor(getResources().getColor(R.color.colorBlack));
            btnUpdateForgot.setBackgroundResource(R.drawable.go_search_rounded);
            btnSendEmail.setBackgroundResource(R.drawable.go_search_rounded);
            btnCheckIdentity.setBackgroundResource(R.drawable.go_search_rounded);
            btnCheckIdentity.setTextColor(getResources().getColor(R.color.colorWhite));
            btnSendEmail.setTextColor(getResources().getColor(R.color.colorWhite));
            btnUpdateForgot.setTextColor(getResources().getColor(R.color.colorWhite));
        } else if (theme.equals("dark")){
            ly.setBackgroundColor(getResources().getColor(R.color.darkThemeBackground));
            title.setTextColor(getResources().getColor(R.color.colorWhite));
            title.setBackgroundColor(getResources().getColor(R.color.darkThemeBackground));
            line.setBackgroundColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            info.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            currentpassword.setBackgroundResource(R.drawable.search_field_rounded_dark);
            newpassword.setBackgroundResource(R.drawable.search_field_rounded_dark);
            confirmpassword.setBackgroundResource(R.drawable.search_field_rounded_dark);
            email.setBackgroundResource(R.drawable.search_field_rounded_dark);
            verification.setBackgroundResource(R.drawable.search_field_rounded_dark);
            currentpassword.setHintTextColor(getResources().getColor(R.color.colorDarkThemeHintGrey));
            newpassword.setHintTextColor(getResources().getColor(R.color.colorDarkThemeHintGrey));
            confirmpassword.setHintTextColor(getResources().getColor(R.color.colorDarkThemeHintGrey));
            email.setHintTextColor(getResources().getColor(R.color.colorDarkThemeHintGrey));
            verification.setHintTextColor(getResources().getColor(R.color.colorDarkThemeHintGrey));
            currentpassword.setTextColor(getResources().getColor(R.color.colorWhite));
            newpassword.setTextColor(getResources().getColor(R.color.colorWhite));
            confirmpassword.setTextColor(getResources().getColor(R.color.colorWhite));
            email.setTextColor(getResources().getColor(R.color.colorWhite));
            verification.setTextColor(getResources().getColor(R.color.colorWhite));
            btnUpdateForgot.setBackgroundResource(R.drawable.go_search_rounded_dark);
            btnSendEmail.setBackgroundResource(R.drawable.go_search_rounded_dark);
            btnCheckIdentity.setBackgroundResource(R.drawable.go_search_rounded_dark);
            btnCheckIdentity.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            btnSendEmail.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            btnUpdateForgot.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));


        }
    }
}
