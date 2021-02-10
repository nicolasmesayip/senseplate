package com.example.senseplate;

// Importing the libraries used.
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Auth extends Activity{
    // Declaring the global variables.
    EditText logName, logPassword, logEmail, confirmPassword, verification;
    Button btnSignIn, btnRegister, btnConfirm, btnresend, btnSendEmail, btnChangePassword;
    String name, password, email, cPassword, emailcontent, usernames, code, theme;
    JSONParser jsonParser = new JSONParser();
    TextView error, info, forgotpassword;
    ScrollView sc;
    int i = 0;
    int x = 0;
    boolean specialchar = false;
    boolean number = false;
    boolean uppercase = false;
    boolean lowercase = false;
    boolean eightchar = false;
    boolean verificate = false;
    boolean check = false;
    boolean check2 = false;
    boolean forgot = false;
    private static final String TAG_SUCCESS = "success";
    private static final String TAG = "Auth";
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    // Method called when the Activity is created.
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth);

        // Initializing the widgets.
        sc = (ScrollView) findViewById(R.id.sc);
        logName = (EditText) findViewById(R.id.logName);
        logPassword = (EditText) findViewById(R.id.logPassword);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);
        logEmail = (EditText) findViewById(R.id.logEmail);
        verification = (EditText) findViewById(R.id.verification);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnConfirm = (Button) findViewById(R.id.btnConfirm);
        btnresend = (Button) findViewById(R.id.btnresend);
        btnSendEmail = (Button) findViewById(R.id.btnSendEmail);
        btnChangePassword = (Button) findViewById(R.id.btnChangePassword);
        error = (TextView) findViewById(R.id.error);
        forgotpassword = (TextView) findViewById(R.id.forgotpassword);
        info = (TextView) findViewById(R.id.info);

        // Initializing the storage of username
        sharedPref = getSharedPreferences("com.example.senseplate", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        InitSharedPrefs();
        // Sets the event for the Sign in Button.
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Checks if the fields are empty
                if (logName.getText().toString().length() > 0 && logPassword.getText().toString()
                        .length() > 0) {
                    // If they are not, it will check the details.
                    Login attemptLogin = new Login();
                    attemptLogin.execute(logName.getText().toString(), logPassword.getText()
                            .toString());
                } else {
                    // Displays a message error in case the fields are empty.
                    setError("Invalid username/password");
                }
            }
        });

        // Sets the event for the Register button.
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean correctusername = false;
                // Checks the times the button has been pressed.
                if(i==0) {
                    // If the button has been pressed once, make visible the fields necessary for
                    // the register.
                    info.setText("Remember that the username must contain at least one letter and the password must contain at least one uppercase, one lowercase, one number, one special character and also must be 8 or more characters long.\n A verification code will be sent to your email!");
                    i = 1;
                    if (error.getVisibility() == View.VISIBLE){
                        error.setVisibility(View.GONE);
                    }
                    editor.putString(getString(R.string.verification_password), "");
                    editor.commit();
                    forgot = false;
                    forgotpassword.setVisibility(View.GONE);
                    confirmPassword.setVisibility(View.VISIBLE);
                    logEmail.setVisibility(View.VISIBLE);
                    btnSignIn.setVisibility(View.GONE);
                    btnRegister.setText("CREATE ACCOUNT");
                }
                else{
                    // If the button has been pressed a second time, check if the fields are empty.
                    if (logName.getText().toString().length() > 0 && logEmail.getText().toString()
                            .length() > 0 && isEmailValid(logEmail.getText().toString())) {
                        // Check if the password is valid. It MUST include at least one uppercase,
                        // one lowercase, a number, an special character and MUST be eight
                        // characters long.
                        for (int i = 0; i < logName.getText().toString().length(); i++){
                            if (Character.isLetter(logName.getText().toString().charAt(i))){
                                correctusername = true;
                            }
                        }
                        if (correctusername){
                            if (logPassword.getText().toString().equals(confirmPassword.getText().toString())){
                                    passwordCheck(logPassword.getText().toString());
                                if (specialchar && number && lowercase && uppercase && eightchar) {
                                    // Continue with the register.
                                    editor.putString(String.valueOf(R.string.getEmail),logEmail.getText().toString());
                                    editor.commit();
                                    Login attemptLogin = new Login();
                                    attemptLogin.execute(logName.getText().toString(), logPassword.getText()
                                            .toString(), logEmail.getText().toString());
                                }
                            } else {
                                setError("The password is not identical to the confirmation.");
                            }
                        } else {
                            setError("Username must contain at least one letter.");
                        }
                    } else {
                        setError("Invalid username/email");
                        specialchar = false;
                        number = false;
                        uppercase = false;
                        lowercase = false;
                        eightchar = false;
                    }
                }
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Auth.class);
                startActivity(i);
                setLog("");
                setLogs("");
                finish();
            }
        });
        btnresend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailVerification();
                displayConfirm();
                setLogs(String.valueOf(x));
                InitSharedPrefs();
                verification.setText("");
            }
        });
        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logPassword.setVisibility(View.GONE);
                logEmail.setVisibility(View.VISIBLE);
                forgotpassword.setVisibility(View.GONE);
                btnRegister.setVisibility(View.GONE);
                btnSignIn.setVisibility(View.GONE);
                btnSendEmail.setVisibility(View.VISIBLE);
                info.setText("A verification email will be sent.");
            }
        });
        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (logEmail.getText().toString().length() > 0 && logName.getText().toString().length() > 0){
                    editor.putString(String.valueOf(R.string.getEmail),logEmail.getText().toString());
                    editor.commit();
                    new ForgotPassword().execute();
                }
            }
        });
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (logPassword.getText().toString().length() > 0 && confirmPassword.getText().toString().length() > 0){
                    if (logPassword.getText().toString().equals(confirmPassword.getText().toString())) {
                        passwordCheck(logPassword.getText().toString());
                        if (specialchar && number && lowercase && uppercase && eightchar) {
                            // Continue with the register.
                            new UpdatePassword().execute();
                        }
                    } else {
                        setError("The passwords do not match");
                    }
                } else {
                    setError("Required fields are missing");
                }
            }
        });
        verification.addTextChangedListener(new VerificationTextWatcher());
        detectDarkMode();
    }

    public void setError(String errormessage){
        error.setText(errormessage);
        error.setVisibility(View.VISIBLE);
    }

    // Class that performs the connection to the database to continue with the process of
    // login/register.
    // The class AsyncTask is the superclass that enables to perform tasks in the background.
    private class Login extends AsyncTask<String, String, JSONObject>{
        // Method to set the variables that will be used in the connection.
        protected void onPreExecute(){
            super.onPreExecute();
             name = logName.getText().toString();
             password = logPassword.getText().toString();
             email = logEmail.getText().toString();
        }
        protected JSONObject doInBackground(String... args){
            String url = "http://nam33.student.eda.kent.ac.uk/auth.php";
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // Creating an ArrayList and adding the variables that will be sent.
            params.add(new BasicNameValuePair("username", name));
            params.add(new BasicNameValuePair("password", password));
            params.add(new BasicNameValuePair("email", email));
            // Calls the method that makes the connection passing the URL variables, the Method and
            // the ArrayList as parameters.
            JSONObject json = jsonParser.makeHttpRequest(url, "POST", params);

            try{
                // Checks if the operation was performed successfully.
                int success = json.getInt(TAG_SUCCESS);
                // Success Login
                if (success == 1) {
                    // Stores the username on the app storage to maintain the session open.
                    String username = name;
                    setLog(username);
                    setLogs("Yes");
                    InitSharedPrefs();
                // Username/email in use
                } else if (success == 2) {
                    editor.putString(getString(R.string.username), "");
                    editor.commit();
                    check2 = true;
                // Success Register
                } else if(success == 3) {
                    verificate = true;
                    String username = name;
                    setLog(username);
                // Success Login but Verification required
                }else if(success == 4){
                    email = json.getString("email");
                    verificate = true;
                    i = 1;
                    String username = name;
                    setLog(username);
                }else{
                    // If operation failed, store an empty string in the storage.
                    editor.putString(getString(R.string.username), "");
                    editor.commit();
                    check = true;

                }
            } catch (JSONException e){
                e.printStackTrace();
            }
            return json;
        }
        protected void onPostExecute(JSONObject log){
            try{
                if (log != null){
                    Toast.makeText(getApplicationContext(), log.getString("message"),
                            Toast.LENGTH_LONG).show();
                } else{
                    Toast.makeText(getApplicationContext(),"Unable to receive data",
                            Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
            // If operation failed, make visible the error message .
            if (check){
                setError("Invalid username/password");
            }
            if (check2){
                setError("Username or Email already in use");
            }
            if (verificate){
                error.setVisibility(View.GONE);
                emailVerification();
                displayConfirm();
                setLogs(String.valueOf(x));
                InitSharedPrefs();
            }
        }
    }
    // Method that checks if there is a username stored on the app.
    private void InitSharedPrefs(){
        usernames = sharedPref.getString(getString(R.string.username), "");
        code = sharedPref.getString(getString(R.string.verification), "");
        // If there is a username stored, run the Main Activity as soon as the app is executed.

        if (code.equals("Yes")){
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
        }
        try{
            if (sharedPref.getString(getString(R.string.verification_password), "").equals("forgot")){
                forgot = true;
            }
            Integer.parseInt(code);
            verificate = true;
            displayConfirm();
        } catch (Exception e){
        }

    }
    // Method that stores the username in the app.
    public void setLog(String username){
        editor.putString(getString(R.string.username), username);
        editor.commit();
    }
    public void setLogs(String code){
        editor.putString(getString(R.string.verification), code);
        editor.commit();
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
    // Method that checks if the email is valid.
    public static boolean isEmailValid(String email)
    {
        String emailValid = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailValid);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }



    public void emailVerification(){
        x = (int)(Math.random()*((999999-100000)+1))+100000;
        if (sharedPref.getString(getString(R.string.verification_password), "").equals("forgot")){
            emailcontent = "<table border=\"1\" width=\"100%\" style=\"background-color: #e1e3e6;\">\n" +
                    "\t\t<td>\n" +
                    "\t\t<table width=\"90%\" align=\"center\" style=\"border-collapse: collapse; background-color: #ffffff;\">\n" +
                    "\t\t\t<tr> <td align=\"center\" bgcolor=\"#70bbd9\" style=\"padding: 40px 0 30px 0;\"> <img src=\"http://nam33.student.eda.kent.ac.uk/Logo.png\" alt=\""+ x +"\" width=\"500\" height=\"230\" style=\"display: block;\" /></td></tr>\n" +
                    "\t\t\t<tr> <td align=\"center\" style=\"padding-top: 20px;font-size: 25px;\"> This is a verification code generated due to a request for changing the password <br> Your verification code is the following:</td></tr>\n" +
                    "\t\t\t<tr> <td align=\"center\" style=\"font-size: 40px; font-weight: bold; padding-top: 20px; padding-bottom: 20px;\" >"+ x +"</td></tr>\n" +
                    "\t\t\t<tr> <td align=\"center\" style=\"font-size: 25px; padding-bottom: 20px;\"> This code is valid for 10 minutes. After that time, you will need to request another code. <br><br>If you have not requested a change of password, please contact us through this email.</td></tr>\n" +
                    "\t\t</table>\n" +
                    "\t</td>\n" +
                    "\t</table>";
        } else {
            emailcontent = "<table border=\"1\" width=\"100%\" style=\"background-color: #e1e3e6;\">\n" +
                    "\t\t<td>\n" +
                    "\t\t<table width=\"90%\" align=\"center\" style=\"border-collapse: collapse; background-color: #ffffff;\">\n" +
                    "\t\t\t<tr> <td align=\"center\" bgcolor=\"#70bbd9\" style=\"padding: 40px 0 30px 0;\"> <img src=\"http://nam33.student.eda.kent.ac.uk/Logo.png\" alt=\""+ x +"\" width=\"500\" height=\"230\" style=\"display: block;\" /></td></tr>\n" +
                    "\t\t\t<tr> <td align=\"center\" style=\"padding-top: 20px;font-size: 25px;\"> Thank you for registering on the SensePlate app. <br> Your verification code is the following:</td></tr>\n" +
                    "\t\t\t<tr> <td align=\"center\" style=\"font-size: 40px; font-weight: bold; padding-top: 20px; padding-bottom: 20px;\" >"+ x +"</td></tr>\n" +
                    "\t\t\t<tr> <td align=\"center\" style=\"font-size: 25px; padding-bottom: 20px;\"> This code is valid for 30 minutes. After that time, you will need to request another code. <br><br>This is an automatic email, if you received this email by error, just ignore it.</td></tr>\n" +
                    "\t\t</table>\n" +
                    "\t</td>\n" +
                    "\t</table>";
        }
        email = sharedPref.getString(String.valueOf(R.string.getEmail), "");
        new SendMail(Auth.this).execute("nam33@kent.ac.uk",
                "#Golfista7", email, "SensePlate Verification", emailcontent);
        editor.putString(getString(R.string.time), String.valueOf(System.currentTimeMillis()));
        editor.commit();
        verificate = true;

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
                    if ((System.currentTimeMillis() - time) > 1800000){
                        verificate = false;
                    }
                } catch (Exception e){

                }

                // Checks if the verification code is correct.
                if (Integer.parseInt(verification.getText().toString()) == Integer.parseInt(code) && verificate) {
                    if (!forgot) {
                        new updateVerification().execute();
                    } else {
                        displayForgotPassword();
                    }
                } else if(!verificate){
                    error.setText("Timed out");
                    error.setVisibility(View.VISIBLE);
                } else {
                    error.setText("The verification code introduced is incorrect.");
                    error.setVisibility(View.VISIBLE);
                }
            } else if (error.getVisibility()== View.VISIBLE && s.toString().length() <= 5){
                error.setVisibility(View.GONE);
            }
        }
    }
    public void displayForgotPassword(){
        btnChangePassword.setVisibility(View.VISIBLE);
        logPassword.setVisibility(View.VISIBLE);
        confirmPassword.setVisibility(View.VISIBLE);
        forgotpassword.setVisibility(View.GONE);
        verification.setVisibility(View.GONE);
        btnresend.setVisibility(View.GONE);
        btnConfirm.setVisibility(View.GONE);
    }

    class updateVerification extends AsyncTask<String, String, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String url_update_user_verification =
                    "http://nam33.student.eda.kent.ac.uk/update_user_verification.php";
            // Creating an ArrayList and adding the variables that will be sent.
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("username", usernames));


            // Calls the method that makes the connection passing the URL variables, the Method and
            // the ArrayList as parameters.
            JSONObject json = jsonParser.makeHttpRequest(url_update_user_verification,
                    "POST", params);

            // Prints in the log the result of the operation.
            Log.d("Create Response", json.toString());

            try {
                // Checks if the operation was performed successfully.
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    setLogs("Yes");
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    // Displays an error message.
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    private void displayConfirm(){
        verification.setVisibility(View.VISIBLE);
        logName.setVisibility(View.GONE);
        logPassword.setVisibility(View.GONE);
        logEmail.setVisibility(View.GONE);
        confirmPassword.setVisibility(View.GONE);
        btnSignIn.setVisibility(View.GONE);
        btnRegister.setVisibility(View.GONE);
        btnConfirm.setVisibility(View.VISIBLE);
        info.setText("A 6-digit code has been sent to your email. Please introduce the code to continue.");
        btnresend.setVisibility(View.VISIBLE);
        btnSendEmail.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (i > 0){
            Intent i = new Intent(getApplicationContext(), Auth.class);
            startActivity(i);
            setLog("");
            setLogs("");
            finish();
        } else {
            finish();
            System.exit(0);
        }
    }
    class ForgotPassword extends AsyncTask<String, String, String>{
        boolean check = false;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            usernames = logName.getText().toString();
            email = logEmail.getText().toString();
        }

        @Override
        protected String doInBackground(String... strings) {
            String url_update_user_verification =
                    "http://nam33.student.eda.kent.ac.uk/forgotpass.php";
            // Creating an ArrayList and adding the variables that will be sent.
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("username", usernames));
            params.add(new BasicNameValuePair("email", email));


            // Calls the method that makes the connection passing the URL variables, the Method and
            // the ArrayList as parameters.
            JSONObject json = jsonParser.makeHttpRequest(url_update_user_verification,
                    "POST", params);

            // Prints in the log the result of the operation.
            Log.d("Create Response", json.toString());

            try {
                // Checks if the operation was performed successfully.
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    check = true;
                    forgot = true;
                } else {
                    // Displays an error message.
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (check){
                editor.putString(getString(R.string.verification_password), "forgot");
                editor.commit();
                emailVerification();
                displayConfirm();
                setLogs(String.valueOf(x));
                setLog(usernames);
                InitSharedPrefs();
            }
        }
    }


    private class UpdatePassword extends AsyncTask<String, String, String> {
        boolean check = false;
        // Method to set the variables that will be used in the connection.
        protected void onPreExecute(){
            super.onPreExecute();
            usernames = sharedPref.getString(getString(R.string.username), "");
            password = logPassword.getText().toString();
            Log.d("esa", usernames + " "+ password);

        }
        protected String doInBackground(String... args){
            String url = "http://nam33.student.eda.kent.ac.uk/update_password.php";
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // Creating an ArrayList and adding the variables that will be sent.
            params.add(new BasicNameValuePair("username", usernames));
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
                editor.putString(getString(R.string.verification), "Yes");
                editor.commit();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        }
    }

    private void detectDarkMode(){
        theme = sharedPref.getString(getString(R.string.theme), "light");
        if (theme.equals("light")){
            sc.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            info.setBackgroundColor(Color.rgb(207,255,220));
            info.setTextColor(getResources().getColor(R.color.colorBlack));
            logName.setTextColor(getResources().getColor(R.color.colorBlack));
            logName.setHintTextColor(getResources().getColor(R.color.colorGrey));
            logEmail.setTextColor(getResources().getColor(R.color.colorBlack));
            logEmail.setHintTextColor(getResources().getColor(R.color.colorGrey));
            logPassword.setTextColor(getResources().getColor(R.color.colorBlack));
            logPassword.setHintTextColor(getResources().getColor(R.color.colorGrey));
            confirmPassword.setTextColor(getResources().getColor(R.color.colorBlack));
            confirmPassword.setHintTextColor(getResources().getColor(R.color.colorGrey));
            verification.setTextColor(getResources().getColor(R.color.colorBlack));
            verification.setHintTextColor(getResources().getColor(R.color.colorGrey));
            btnChangePassword.setTextColor(getResources().getColor(R.color.colorWhite));
            btnSendEmail.setTextColor(getResources().getColor(R.color.colorWhite));
            btnresend.setTextColor(getResources().getColor(R.color.colorWhite));
            btnConfirm.setTextColor(getResources().getColor(R.color.colorWhite));
            btnRegister.setTextColor(getResources().getColor(R.color.colorWhite));
            btnSignIn.setTextColor(getResources().getColor(R.color.colorWhite));
            btnSignIn.setBackgroundResource(R.drawable.show_chart_rounded);
            btnRegister.setBackgroundResource(R.drawable.show_chart_rounded);
            btnConfirm.setBackgroundResource(R.drawable.clear_button_rounded);
            btnSendEmail.setBackgroundResource(R.drawable.show_chart_rounded);
            btnChangePassword.setBackgroundResource(R.drawable.show_chart_rounded);
            btnresend.setBackgroundResource(R.drawable.show_chart_rounded);
        } else if (theme.equals("dark")){
            sc.setBackgroundColor(getResources().getColor(R.color.darkThemeBackground));
            info.setBackgroundColor(getResources().getColor(R.color.darkThemeBackground));
            info.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            logName.setTextColor(getResources().getColor(R.color.colorWhite));
            logName.setHintTextColor(getResources().getColor(R.color.colorDarkThemeHintGrey));
            logEmail.setTextColor(getResources().getColor(R.color.colorWhite));
            logEmail.setHintTextColor(getResources().getColor(R.color.colorDarkThemeHintGrey));
            logPassword.setTextColor(getResources().getColor(R.color.colorWhite));
            logPassword.setHintTextColor(getResources().getColor(R.color.colorDarkThemeHintGrey));
            confirmPassword.setTextColor(getResources().getColor(R.color.colorWhite));
            confirmPassword.setHintTextColor(getResources().getColor(R.color.colorDarkThemeHintGrey));
            verification.setTextColor(getResources().getColor(R.color.colorWhite));
            verification.setHintTextColor(getResources().getColor(R.color.colorDarkThemeHintGrey));
            btnChangePassword.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            btnSendEmail.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            btnresend.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            btnConfirm.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            btnRegister.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            btnSignIn.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            btnSignIn.setBackgroundResource(R.drawable.show_chart_rounded_dark);
            btnRegister.setBackgroundResource(R.drawable.show_chart_rounded_dark);
            btnConfirm.setBackgroundResource(R.drawable.clear_button_rounded_dark);
            btnSendEmail.setBackgroundResource(R.drawable.show_chart_rounded_dark);
            btnChangePassword.setBackgroundResource(R.drawable.show_chart_rounded_dark);
            btnresend.setBackgroundResource(R.drawable.show_chart_rounded_dark);

        }
    }
}







