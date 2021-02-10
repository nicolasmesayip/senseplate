package com.example.senseplate;

// Import libraries used.
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


public class AddFoodActivity extends Activity {
// Declaring the global variables.
    JSONParser jsonParser = new JSONParser();
    String nameAdd, carbohydrateAdd, proteinAdd, fatAdd, user, theme;
    EditText inputName, Carbohydrates, Proteins, Fats;
    TextView errors, calories, add_item, protein_add, carbohydrate_add, fat_add, add, total_calories;
    Button createButton, caloriesButton, chartButton, clear;
    Button b1,b2,b3,b4;
    boolean checkAdd = false;
    int height, width;
    ScrollView sc;
    DecimalFormat f = new DecimalFormat("####.00");

    private static final String TAG_SUCCESS = "success";
    private static final String TAG = "AddFoodActivity";
    SharedPreferences sharedPref;

    // Method called when the Activity is created.
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_food);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        b1 = (Button) findViewById(R.id.button1);
        b2 = (Button) findViewById(R.id.button2);
        b3 = (Button) findViewById(R.id.button3);
        b4 = (Button) findViewById(R.id.button4);
        Log.d("ds", String.valueOf(1920/ ((float) getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT)));
        // Initializing the widgets.
        sc = (ScrollView) findViewById(R.id.sc);
        sc.getLayoutParams().height = height*8950/10000;

        inputName = (EditText) findViewById(R.id.add_item_name);
        Carbohydrates = (EditText) findViewById(R.id.carbohydrate_add_field);
        Proteins = (EditText) findViewById(R.id.protein_add_field);
        Fats = (EditText) findViewById(R.id.fat_add_field);
        errors = (TextView) findViewById(R.id.error);
        calories = (TextView) findViewById(R.id.calories_field);
        add = (TextView) findViewById(R.id.main);
        protein_add = (TextView) findViewById(R.id.protein_add);
        carbohydrate_add = (TextView) findViewById(R.id.carbohydrate_add);
        fat_add = (TextView) findViewById(R.id.fat_add);
        total_calories = (TextView) findViewById(R.id.total_calories);
        add_item = (TextView) findViewById(R.id.add_item);

        clear = (Button) findViewById(R.id.clear);
        createButton = (Button) findViewById(R.id.save);
        caloriesButton = (Button) findViewById(R.id.show_calories);
        chartButton = (Button) findViewById(R.id.chart);
        // Gets the username of the user logged in. In case none user is logged, set the value to "".
        sharedPref = getSharedPreferences("com.example.senseplate",Context.MODE_PRIVATE);
        user = sharedPref.getString(getString(R.string.username),"");
        theme = sharedPref.getString(getString(R.string.theme), "light");

        // Gets the values the used entered from the EditTexts.
        nameAdd = inputName.getText().toString();
        carbohydrateAdd = Carbohydrates.getText().toString();
        proteinAdd = Proteins.getText().toString();
        fatAdd = Fats.getText().toString();

        resize();

        // Sets the event for the adding food button.
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Checks that any field is empty.
                if (Carbohydrates.getText().toString().length() > 0 && Proteins.getText().toString()
                        .length() > 0 && Fats.getText().toString().length() > 0 && inputName
                        .getText().toString().length() > 0){
                    // Modifies the name of the food the user entered, changing the first letter to
                    // uppercase and adding the username between brackets at the end,
                    if (user.equals("admin")){
                            nameAdd = inputName.getText().toString().substring(0,1).toUpperCase() +
                                    inputName.getText().toString().substring(1);
                        } else {
                            nameAdd = inputName.getText().toString().substring(0,1).toUpperCase() +
                                    inputName.getText().toString().substring(1) + " (" + user + ")";
                        }
                    // Executes the AddFood class.
                    new AddFood().execute();
                    // If the error message is visible from a previous attempt, it will disappear.
                    if (errors.getVisibility() == View.VISIBLE) {
                        errors.setVisibility(View.GONE);}
                } else {
                    //If a field is empty, an error message is displayed.
                    errors.setText("Required field(s) are missing");
                    errors.setVisibility(View.VISIBLE);
                }
            }
        });
        // Sets the event for the calculation of calories.
        chartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Checks if any of the fields required for calculating calories es empty.
                if (Carbohydrates.getText().toString().length() > 0 && Proteins.getText().toString()
                        .length() > 0 && Fats.getText().toString().length() > 0 && inputName.getText().toString().length() > 0) {
                    // Calls the method that performs the operation.
                    Intent i = new Intent(getApplicationContext(), PieChart.class);
                    i.putExtra("name", inputName.getText().toString());
                    i.putExtra("carbo", Carbohydrates.getText().toString());
                    i.putExtra("prot", Proteins.getText().toString());
                    i.putExtra("fat", Fats.getText().toString());
                    i.putExtra("username", user);
                    i.putExtra("sender", "addfood");
                    i.putExtra("weight", "100");
                    i.putExtra("recipe", "No");
                    startActivity(i);
                    // If the error message is visible from a previous attempt, it will disappear.
                    if (errors.getVisibility() == View.VISIBLE) {
                        errors.setVisibility(View.GONE);
                    }
                } else{
                    //If a field is empty, an error message is displayed.
                    errors.setText("Required field(s) are missing");
                    errors.setVisibility(View.VISIBLE);
                }
            }
        });
        caloriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Checks if any of the fields required for calculating calories es empty.
                if (Carbohydrates.getText().toString().length() > 0 && Proteins.getText().toString()
                        .length() > 0 && Fats.getText().toString().length() > 0) {
                    // Calls the method that performs the operation.
                    calculateCalories();

                    // If the error message is visible from a previous attempt, it will disappear.
                    if (errors.getVisibility() == View.VISIBLE) {
                        errors.setVisibility(View.GONE);
                    }
                } else{
                    //If a field is empty, an error message is displayed.
                    errors.setText("Required field(s) are missing");
                    errors.setVisibility(View.VISIBLE);
                }
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputName.setText("");
                Proteins.setText("");
                Carbohydrates.setText("");
                Fats.setText("");
                calories.setText("");
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();

            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                Intent i = new Intent(getApplicationContext(), Settings.class);
                startActivity(i);

            }
        });
        detectDarkMode();
    }
    // Class that performs the connection to the database to add the food.
    // The class AsyncTask is the superclass that enables to perform tasks in the background.
    class AddFood extends AsyncTask<String, String, String> {
        // Method to set the variables that will be used in the connection.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            carbohydrateAdd = Carbohydrates.getText().toString();
            proteinAdd = Proteins.getText().toString();
            fatAdd = Fats.getText().toString();
        }
        // Method responsible of the connection to the database.
        @Override
        protected String doInBackground(String... args) {
            // Local variable that contains the path of the PHP file used for connection
            String url_create_product = "http://nam33.student.eda.kent.ac.uk/insert_food.php";
            // Creating an ArrayList and adding the variables that will be sent.
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name", nameAdd));
            params.add(new BasicNameValuePair("carbohydrate", carbohydrateAdd));
            params.add(new BasicNameValuePair("protein", proteinAdd));
            params.add(new BasicNameValuePair("fat", fatAdd));
            params.add(new BasicNameValuePair("idUser", user));
            // Calls the method that makes the connection passing the URL variables, the Method and
            // the ArrayList as parameters.
            JSONObject json = jsonParser.makeHttpRequest(url_create_product, "POST",
                    params);
            // Prints in the log the result of the operation.
            Log.d("Create Response", json.toString());

            try {
                // Checks if the operation was performed successfully.
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // If it was the case, it will run the Main Activity, and close all the previous
                    // activities.
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                } else {
                    // Variable condition that indicates that there was a problem during the
                    // operation.
                    checkAdd = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {

            if (checkAdd){
                // If an error occurs, call the method error.
                error();
            } else{
                // In case no error occurred, create Toast with the following message.
                Toast.makeText(getApplicationContext(),"Adding Food...", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }
        // Method called when an error occurred.
        public void error() {
            // Sets the error message, makes it visible and change the error variable back to
            // original value.
            errors.setText("Error, that name is already added");
            errors.setVisibility(View.VISIBLE);
            checkAdd = false;
        }
        // Method that calculates the calories when the Calories button is pressed.
        public void calculateCalories(){
            // Operation to calculate the calories using the Atwater system and displaying the
            // message.
            float caloriess = (Float.parseFloat(Carbohydrates.getText().toString())*4 + Float.
                    parseFloat(Proteins.getText().toString())*4 + Float.parseFloat(Fats.getText().
                    toString())*9);
            calories.setText(f.format(caloriess) + " kcal");
        }

        private void resize(){
            Proteins.setWidth(width*3576/10000);
            Fats.setWidth(width*3576/10000);
            Carbohydrates.setWidth(width*3576/10000);
            calories.setWidth(width*3576/10000);
            b1.setWidth(width*25/100);
            b2.setWidth(width*25/100);
            b3.setWidth(width*25/100);
            b4.setWidth(width*25/100);

            RelativeLayout.LayoutParams f1 = (RelativeLayout.LayoutParams) Fats.getLayoutParams();
            f1.setMargins(width*486/10000,0,0,0);
            add.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*33/411);
            add_item.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*21/411);
            fat_add.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*21/411);
            protein_add.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*21/411);
            carbohydrate_add.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*21/411);
            total_calories.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/ Resources.getSystem().getDisplayMetrics().density)*21/411);
            caloriesButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*12/411);
            chartButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*12/411);
            clear.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*12/411);
            createButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*12/411);
            inputName.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*14/411);


            caloriesButton.setPadding(width*73/1000,width*121/10000,width*73/1000,width*121/10000);
            chartButton.setPadding(width*73/1000,width*121/10000,width*73/1000,width*121/10000);
            clear.setPadding(width*73/1000,width*121/10000,width*73/1000,width*121/10000);
            createButton.setPadding(width*73/1000,width*121/10000,width*73/1000,width*121/10000);


            RelativeLayout.LayoutParams p1 = (RelativeLayout.LayoutParams) caloriesButton.getLayoutParams();
            p1.setMargins(width*486/10000,height*5689/100000,width*486/10000,0);
            RelativeLayout.LayoutParams c3 = (RelativeLayout.LayoutParams) chartButton.getLayoutParams();
            c3.setMargins(width*486/10000,height*5689/100000,width*486/10000,0);
            RelativeLayout.LayoutParams p2 = (RelativeLayout.LayoutParams) protein_add.getLayoutParams();
            p2.setMargins(0,height*4267/100000,0,0);
            RelativeLayout.LayoutParams f2 = (RelativeLayout.LayoutParams) fat_add.getLayoutParams();
            f2.setMargins(0,height*4267/100000,width*1216/10000,0);


            LinearLayout.LayoutParams c1 = (LinearLayout.LayoutParams) total_calories.getLayoutParams();
            c1.setMargins( 0,height*4267/100000,0,0);
            LinearLayout.LayoutParams c2 = (LinearLayout.LayoutParams) calories.getLayoutParams();
            c2.setMargins( 0,0,0,0);
            LinearLayout.LayoutParams i1 = (LinearLayout.LayoutParams) add_item.getLayoutParams();
            i1.setMargins(0,height*2844/100000,0,0);
            LinearLayout.LayoutParams c4 = (LinearLayout.LayoutParams) carbohydrate_add.getLayoutParams();
            c4.setMargins(0,height*4267/100000,0,0);

        }
    public void detectDarkMode(){
        theme = sharedPref.getString(getString(R.string.theme), "light");
        if (theme.equals("light")){
            sc.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            add.setTextColor(getResources().getColor(R.color.colorBlack));
            add_item.setTextColor(getResources().getColor(R.color.colorBlack));
            carbohydrate_add.setTextColor(getResources().getColor(R.color.colorBlack));
            protein_add.setTextColor(getResources().getColor(R.color.colorBlack));
            fat_add.setTextColor(getResources().getColor(R.color.colorBlack));
            total_calories.setTextColor(getResources().getColor(R.color.colorBlack));
            inputName.setBackgroundResource(R.drawable.search_field_rounded);
            Proteins.setBackgroundResource(R.drawable.search_field_rounded);
            Carbohydrates.setBackgroundResource(R.drawable.search_field_rounded);
            Fats.setBackgroundResource(R.drawable.search_field_rounded);
            calories.setBackgroundResource(R.drawable.search_field_rounded);
            inputName.setHintTextColor(getResources().getColor(R.color.colorGrey));
            inputName.setTextColor(getResources().getColor(R.color.colorBlack));
            Carbohydrates.setTextColor(getResources().getColor(R.color.colorBlack));
            Proteins.setTextColor(getResources().getColor(R.color.colorBlack));
            Fats.setTextColor(getResources().getColor(R.color.colorBlack));
            calories.setTextColor(getResources().getColor(R.color.colorBlack));
            //inputName.setTextCursorDrawable(R.drawable.black_cursor);

            b1.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            b2.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            b3.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            b4.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            b1.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_home_black_24dp),null,null);
            b2.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_library_add_green_24dp),null,null);
            b3.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_history_black_24dp),null,null);
            b4.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_settings_black_24dp),null,null);
            b1.setTextColor(getResources().getColor(R.color.darkThemeBackground));
            b2.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            b3.setTextColor(getResources().getColor(R.color.darkThemeBackground));
            b4.setTextColor(getResources().getColor(R.color.darkThemeBackground));
            clear.setBackgroundResource(R.drawable.clear_button_rounded);
            chartButton.setBackgroundResource(R.drawable.show_chart_rounded);
            caloriesButton.setBackgroundResource(R.drawable.edit_item_rounded);
            createButton.setBackgroundResource(R.drawable.save_entry_rounded);
            clear.setTextColor(getResources().getColor(R.color.colorWhite));
            chartButton.setTextColor(getResources().getColor(R.color.colorWhite));
            createButton.setTextColor(getResources().getColor(R.color.colorWhite));
            caloriesButton.setTextColor(getResources().getColor(R.color.colorWhite));

        } else if (theme.equals("dark")){
            sc.setBackgroundColor(getResources().getColor(R.color.darkThemeBackground));
            add.setTextColor(getResources().getColor(R.color.colorWhite));
            add_item.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            carbohydrate_add.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            protein_add.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            fat_add.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            total_calories.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            inputName.setBackgroundResource(R.drawable.search_field_rounded_dark);
            Proteins.setBackgroundResource(R.drawable.search_field_rounded_dark);
            Carbohydrates.setBackgroundResource(R.drawable.search_field_rounded_dark);
            Fats.setBackgroundResource(R.drawable.search_field_rounded_dark);
            calories.setBackgroundResource(R.drawable.search_field_rounded_dark);
            inputName.setHintTextColor(getResources().getColor(R.color.colorDarkThemeHintGrey));
            inputName.setTextColor(getResources().getColor(R.color.colorWhite));
            Carbohydrates.setTextColor(getResources().getColor(R.color.colorWhite));
            Proteins.setTextColor(getResources().getColor(R.color.colorWhite));
            Fats.setTextColor(getResources().getColor(R.color.colorWhite));
            calories.setTextColor(getResources().getColor(R.color.colorWhite));

            b1.setBackgroundColor(getResources().getColor(R.color.darkThemeBackground));
            b2.setBackgroundColor(getResources().getColor(R.color.darkThemeBackground));
            b3.setBackgroundColor(getResources().getColor(R.color.darkThemeBackground));
            b4.setBackgroundColor(getResources().getColor(R.color.darkThemeBackground));
            b1.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_home_white_24dp),null,null);
            b2.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_library_add_blue_24dp),null,null);
            b3.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_history_white_24dp),null,null);
            b4.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_settings_white_24dp),null,null);
            b1.setTextColor(getResources().getColor(R.color.colorWhite));
            b2.setTextColor(getResources().getColor(R.color.colorBlue));
            b3.setTextColor(getResources().getColor(R.color.colorWhite));
            b4.setTextColor(getResources().getColor(R.color.colorWhite));
            clear.setBackgroundResource(R.drawable.clear_button_rounded_dark);
            chartButton.setBackgroundResource(R.drawable.show_chart_rounded_dark);
            caloriesButton.setBackgroundResource(R.drawable.edit_item_rounded_dark);
            createButton.setBackgroundResource(R.drawable.save_entry_rounded_dark);
            clear.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            chartButton.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            createButton.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            caloriesButton.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
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









