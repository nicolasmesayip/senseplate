package com.example.senseplate;

// Importing the libraries used.
import android.app.ProgressDialog;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    // Declaring the global variables
    Button btnEditFood, btnSearch, btnRecipe, btnChart, b1,b2,b3,b4, btnHistory, info;
    EditText showweight;
    TextView main, showcarbohydrate, showprotein, showfat, showname,
            weightcarbo, weightprot, weightfat, calories, wcalories, text, unit, searchitem, error;
    String search, name, carbohydrate, protein, fat, wcarbohydrate, wprotein,
            wfat,weight, usernames, recipes, theme, last_search, autosaving;
    AutoCompleteTextView searchText;
    ScrollView sc;
    RelativeLayout weight_background, display;
    int height,width;
    boolean check = false;
    ArrayList<String> items;
    private BluetoothSocket btSocket = null;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    JSONParser jsonParser = new JSONParser();
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_FOOD = "food";
    private static final String TAG_HISTORY = "history";
    private static final String TAG_NAME = "name";
    private static final String TAG_CARBOHYDRATE = "carbohydrate";
    private static final String TAG_PROTEIN = "protein";
    private static final String TAG_FAT = "fat";
    private static final String TAG_WEIGHT = "weight";
    private static final String TAG_RECIPE = "recipe";
    private  TextView daily_calories;

    ProgressDialog progress;

    DecimalFormat f = new DecimalFormat("####.00");

    // Method called when the Activity is created.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        // Initializes the widgets.
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        sc = (ScrollView) findViewById(R.id.sc);
        sc.getLayoutParams().height = height*8950/10000;
        info = (Button) findViewById(R.id.info);
        b1 = (Button) findViewById(R.id.button1);
        b2 = (Button) findViewById(R.id.button2);
        b3 = (Button) findViewById(R.id.button3);
        b4 = (Button) findViewById(R.id.button4);
        btnChart = (Button) findViewById(R.id.chart);
        btnEditFood = (Button) findViewById(R.id.btnEditFood);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnHistory = (Button) findViewById(R.id.btnHistory);
        btnRecipe = (Button) findViewById(R.id.recipe);
        searchText = (AutoCompleteTextView) findViewById(R.id.search);
        showweight = (EditText) findViewById(R.id.showweight);
        error = (TextView) findViewById(R.id.error);
        daily_calories = (TextView) findViewById(R.id.daily_calories);
        showcarbohydrate = (TextView) findViewById(R.id.showcarbohydrate);
        showprotein = (TextView) findViewById(R.id.showprotein);
        showfat = (TextView) findViewById(R.id.showfat);
        weightcarbo = (TextView) findViewById(R.id.weightcarbo);
        weightprot = (TextView) findViewById(R.id.weightprot);
        weightfat = (TextView) findViewById(R.id.weightfat);
        showname = (TextView) findViewById(R.id.srchname);
        calories = (TextView) findViewById(R.id.calories_field);
        wcalories = (TextView) findViewById(R.id.wcalories);
        text = (TextView) findViewById(R.id.text);
        unit = (TextView) findViewById(R.id.unit);
        searchitem = (TextView) findViewById(R.id.searchitem);
        main = (TextView) findViewById(R.id.main);
        weight_background = (RelativeLayout) findViewById(R.id.weight_background);
        display = (RelativeLayout) findViewById(R.id.display);
        weight = showweight.getText().toString();
        resizeMain();


        // Gets the username of the user logged in. In case none user is logged, set the value to "".
        sharedPref = getSharedPreferences("com.example.senseplate", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        InitSharedPrefs();

        info.getPaint().setColor(Color.WHITE);
        // Sets the event for the button that runs the EditFood Activity.
        btnEditFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent b = new Intent(getApplicationContext(), EditFoodActivity.class);
                startActivity(b);
            }
        });
        // Sets the event for the button that searches the food item in the database.
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchText.getText().toString().length() > 0 && showweight.getText().toString().length() > 0){
                    error.setVisibility(View.GONE);
                    search = searchText.getText().toString();
                    weight = showweight.getText().toString();
                    new Search().execute();
                    if (!check){
                        Toast.makeText(getApplicationContext(),
                                "Looking for product...", Toast.LENGTH_LONG).show();
                        if (autosaving.equals("yes")){
                            new History().execute();
                        }
                    }
                } else {
                    setErrors("Required field(s) are missing.");
                }
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonShowPopupWindowClick(v);
            }
        });

        // Sets the event for the button that runs the CreateRecipe Activity.
        btnRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RecipeActivity.class);
                startActivity(i);
            }
        });
        // Sets the event for the button that runs the PieChart Activity.
        btnChart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Checks if the searched fields are empty.
                if (showname.getText().toString().length() > 0 && weightcarbo.getText().toString().length() > 0 && weightprot.getText().toString().length() > 0 && weightfat.getText().toString().length() > 0 && showweight.getText().toString().length() > 0 && !((showname.getText().toString()).equals("Preview"))) {
                    // Starts the PieChart activity and passing several values to it.
                    error.setVisibility(View.GONE);
                    Intent i = new Intent(getApplicationContext(), PieChart.class);
                    i.putExtra("name", showname.getText().toString());
                    i.putExtra("weight", weight);
                    i.putExtra("carbo", String.valueOf(wcarbohydrate));
                    i.putExtra("prot", String.valueOf(wprotein));
                    i.putExtra("fat", String.valueOf(wfat));
                    i.putExtra("username", usernames);
                    i.putExtra("sender", "main");
                    i.putExtra("recipe", recipes);
                    startActivity(i);
                } else {
                    setErrors("You must search a food item/recipe before displaying the chart.");
                }
            }
        });
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchText.getText().toString().length() > 0 && showweight.getText().toString().length() > 0 && weightcarbo.getText().toString().length() > 0 && weightprot.getText().toString().length() > 0 && weightfat.getText().toString().length() > 0){
                    new History().execute();
                    error.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),
                            "Search saved in History",Toast.LENGTH_LONG).show();
                } else {
                    setErrors("Required field(s) are missing.");
                }
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AddFoodActivity.class);
                startActivity(i);
                finish();
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), com.example.senseplate.History.class);
                startActivity(i);
                finish();
            }
        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Settings.class);
                startActivity(i);
                finish();
            }
        });
        detectDarkMode();
        new CompleteText().execute();
        bluetoothConnection();
        new GetDailyHistory().execute();
    }
    // Method that checks if there is a username stored on the app.
    private void InitSharedPrefs(){
        usernames = sharedPref.getString(getString(R.string.username), " ");
        last_search = sharedPref.getString(getString(R.string.lastSearch), "");
        autosaving = sharedPref.getString(getString(R.string.autosaving), "yes");
        lastSearchInfo();
        autoSaving();
    }

    private void lastSearchInfo(){
        if (!last_search.equals("")){
            for (int i = 0; i < last_search.length(); i++){
                char r = last_search.charAt(i);
                if (r == 0x003A){
                    search = last_search.substring(0,i);
                    try {
                        weight = last_search.substring(i + 1);
                        Integer.parseInt(weight);
                    } catch (NumberFormatException e) {
                        weight = "100";
                        showweight.setText(weight);
                    }
                    new Search().execute();
                }
            }
        }
    }
    private void autoSaving(){
        if (autosaving.equals("yes")){
            btnHistory.setVisibility(View.GONE);
            btnChart.setWidth(width);
        }
    }

    private void onButtonShowPopupWindowClick(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.display_product, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);


        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        popupWindow.setWidth(this.width*3649/10000);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }
    // Class that performs the connection to the database to search the food item and calculate
    // the weighted nutritional values.
    // The class AsyncTask is the superclass that enables to perform tasks in the background.
    class Search extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // Method responsible of the connection to the database.
        protected String doInBackground(String... args) {
            // Local variable that contains the path of the PHP file used for connection
            String url_search_food = "http://nam33.student.eda.kent.ac.uk/search_food.php";
            // Creating an ArrayList and adding the ingredient variables that will be sent.
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name", search));
            params.add(new BasicNameValuePair("weight", weight));
            params.add(new BasicNameValuePair("idUser", usernames));
            // Calls the method that makes the connection passing the URL variables,
            // the Method and the ArrayList as parameters.
            JSONObject json = jsonParser.makeHttpRequest(url_search_food, "GET", params);

            try {
                // Checks if the operation was performed successfully.
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1){
                    // Retrieve the nutritional values per 100grams and the weighted nutritional values.
                    JSONArray food = json.getJSONArray(TAG_FOOD);
                    JSONObject c = food.getJSONObject(0);
                    name = c.getString(TAG_NAME);
                    carbohydrate = c.getString(TAG_CARBOHYDRATE);
                    protein = c.getString(TAG_PROTEIN);
                    fat = c.getString(TAG_FAT);
                    weight = c.getString(TAG_WEIGHT);
                    wcarbohydrate = String.valueOf(Float.parseFloat(carbohydrate)*Float.parseFloat(weight)/100);
                    wprotein = String.valueOf(Float.parseFloat(protein)*Float.parseFloat(weight)/100);
                    wfat = String.valueOf(Float.parseFloat(fat)*Float.parseFloat(weight)/100);
                    recipes = c.getString(TAG_RECIPE);

                } else {
                    // If there is not a item with the specified name, the AddFood activity will
                    // be runned and a message displayed.
                    check = true;
                    Intent i = new Intent(getApplicationContext(), AddFoodActivity.class);

                    startActivity(i);
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(String file_url){
            // Depending on the result display a message.
            if (check){
                Toast.makeText(getApplicationContext(),
                        "Product not found",Toast.LENGTH_LONG).show();
                check = false;
            } else {

                // Displays the values in case the name was found.
                showname.setText(name);
                if (Float.parseFloat(wprotein) > 1){
                    weightprot.setText(f.format(Float.parseFloat(wprotein)) + " g");
                } else {
                    weightprot.setText("0" + f.format(Float.parseFloat(wprotein)) + " g");
                }
                if (Float.parseFloat(wfat) > 1){
                    weightfat.setText(f.format(Float.parseFloat(wfat)) + " g");
                } else {
                    weightfat.setText("0" + f.format(Float.parseFloat(wfat)) + " g");
                }
                if (Float.parseFloat(wcarbohydrate) > 1){
                    weightcarbo.setText(f.format(Float.parseFloat(wcarbohydrate)) + " g");
                } else {
                    weightcarbo.setText("0" + f.format(Float.parseFloat(wcarbohydrate)) + " g");
                }
                editor.putString(getString(R.string.lastSearch),name+":"+showweight.getText().toString());
                editor.commit();
                // Calls the function that performs the calculation of calories.
                calculateCalories();
                // Calls the class that adds the search to the history database.
            }
        }
    }

    private void calculateCalories(){
        // Calculates the weighted total calories.
        float caloriesss = (Float.parseFloat(wcarbohydrate)*4 +
                Float.parseFloat(wprotein)*4 + Float.parseFloat(wfat)*9);
        wcalories.setText(f.format(caloriesss) + "kcal");
    }

    // Class that performs the connection to the database to search all the food item to display
    // them in the AutoCompleteTextView.
    // The class AsyncTask is the superclass that enables to perform tasks in the background.
    class CompleteText extends AsyncTask<String, String, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            search = searchText.getText().toString();
        }

        // Method responsible of the connection to the database.
        @Override
        protected String doInBackground(String... strings) {
            // Local variable that contains the path of the PHP file used for connection
            String url_search_all_food = "http://nam33.student.eda.kent.ac.uk/search_all_food.php";
            // Creating an ArrayList and adding the ingredient variables that will be sent.
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name", search));
            params.add(new BasicNameValuePair("idUser", usernames));

            // Calls the method that makes the connection passing the URL variables,
            // the Method and the ArrayList as parameters.
            JSONObject json = jsonParser.makeHttpRequest(url_search_all_food, "GET", params);

            try {
                // Checks if the operation was performed successfully.
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // Adds the names to an ArrayList.
                    items = new ArrayList<>();
                    JSONArray food = json.getJSONArray(TAG_NAME);
                    for (int i = 0; i< food.length();i++){
                        String c = food.getString(i);
                        items.add(c);

                    }

                }

            } catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // Sets the ArrayList as an adapter for the AutoCompleteTextView.
            if (theme.equals("light")) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                        android.R.layout.simple_list_item_1, items);
                searchText.setThreshold(1);
                searchText.setAdapter(adapter);
            } else if (theme.equals("dark")){
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                        R.layout.spinner_ingredient, items);
                searchText.setThreshold(1);
                searchText.setAdapter(adapter);
            }
        }
    }

    // Class that performs the connection to the database to post the weighted results.
    // The class AsyncTask is the superclass that enables to perform tasks in the background.
    class History extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            if (recipes.equals("Yes")){
                name = name.substring(0, name.length()-6) + ")";
            }
            Log.d("Weight", weight);
        }

        // Method responsible of the connection to the database.
        protected String doInBackground(String... args) {
            // Local variable that contains the path of the PHP file used for connection
            String url_history_product = "http://nam33.student.eda.kent.ac.uk/history.php";
            // Creating an ArrayList and adding the ingredient variables that will be sent.
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("weight", weight));
            params.add(new BasicNameValuePair("carbohydrate", wcarbohydrate));
            params.add(new BasicNameValuePair("protein", wprotein));
            params.add(new BasicNameValuePair("fat", wfat));
            params.add(new BasicNameValuePair("idUser", usernames));

            // Calls the method that makes the connection passing the URL variables,
            // the Method and the ArrayList as parameters.
            JSONObject jsons = jsonParser.makeHttpRequest(url_history_product,
                    "POST", params);
            try {
                // Checks if the operation was performed successfully.
                int success = jsons.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("Create Response", jsons.toString());
                }

            } catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            new GetDailyHistory().execute();
        }
    }

    private void setErrors(String message){
        error.setText(message);
        error.setVisibility(View.VISIBLE);
    }

    private void bluetoothConnection(){
        String data = sharedPref.getString(getString(R.string.bluetoothData), "null");
        if (data.equals("null")){

        } else {
            try {
                btSocket = AppSaves.getInstance().getBtSocket();
                if (btSocket.isConnected()) {
                    Log.d("cc", "connected ");
                    Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
                    Timer timer = new Timer();
                    timer.schedule(new ReceivingData(), 0, 5000);
                }
            } catch (Exception e){
                String address = data.substring(data.length() - 17);
                String name = data.substring(0, (data.length() - 18));
                AppSaves.getInstance().prepareDialog(MainActivity.this);
                progress = AppSaves.getInstance().setBluetoothConnection(address);
                progress.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (AppSaves.getInstance().isBtConnected()){
                            btSocket = AppSaves.getInstance().getBtSocket();
                            Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
                            Timer timer = new Timer();
                            timer.schedule(new ReceivingData(), 10000, 5000);
                        }
                    }
                });
            }
        }

    }

    class ReceivingData extends TimerTask {
        public void run(){
            receiveSignal();
        }
    }

    private void receiveSignal(){
        try {
            InputStream inputStream = btSocket.getInputStream();
            int byteCount = inputStream.available();
            if (byteCount > 0){
                byte[] bytes = new byte[byteCount];
                inputStream.read(bytes);
                final String string = new String(bytes,"UTF-8");

                showweight.post(new Runnable() {
                    @Override
                    public void run() {
                        showweight.setText(string);
                    }
                });
                sendSignal("Y");

            }
        } catch (IOException e){

        }

    }


    private void sendSignal ( String number ) {
            try {
                btSocket.getOutputStream().write(number.getBytes());
            } catch (IOException e) {
            }
        }


    class GetDailyHistory extends AsyncTask<String, String, String> {
        boolean check = false;
        Float calories = Float.parseFloat("0");
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            // Local variable that contains the path of the PHP file used for connection
            String url_history_product = "http://nam33.student.eda.kent.ac.uk/getDailyHistory.php";
            // Creating an ArrayList and adding the ingredient variables that will be sent.
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", usernames));

            // Calls the method that makes the connection passing the URL variables,
            // the Method and the ArrayList as parameters.
            JSONObject jsons = jsonParser.makeHttpRequest(url_history_product,
                    "GET", params);
            try {
                // Checks if the operation was performed successfully.
                int success = jsons.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Float carb = Float.parseFloat("0");
                    Float prot = Float.parseFloat("0");
                    Float fat = Float.parseFloat("0");
                    JSONArray history = jsons.getJSONArray(TAG_HISTORY);
                    for (int i = 0; i < history.length(); i++) {
                        JSONObject c = history.getJSONObject(i);
                        String icarbo = c.getString(TAG_CARBOHYDRATE);
                        String iprot = c.getString(TAG_PROTEIN);
                        String ifat = c.getString(TAG_FAT);
                        carb += Float.parseFloat(icarbo);
                        prot += Float.parseFloat(iprot);
                        fat += Float.parseFloat(ifat);
                    }
                    calories = carb*4 + prot*4 + fat*9;
                }

            } catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
                daily_calories.setText("Today's calories: "+f.format(calories)+"kcal");
        }
    }
    public void resizeMain(){

        b1.setWidth(width*25/100);
        b2.setWidth(width*25/100);
        b3.setWidth(width*25/100);
        b4.setWidth(width*25/100);
        btnEditFood.setWidth(width*4379/10000);
        btnHistory.setWidth(width*4379/10000);
        btnChart.setWidth(width*4379/10000);
        btnRecipe.setWidth(width*4379/10000);
        searchText.setDropDownWidth(width);
        main.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/ Resources.getSystem().getDisplayMetrics().density)*33/411);
        searchitem.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*21/411);
        LinearLayout.LayoutParams i1 = (LinearLayout.LayoutParams) searchitem.getLayoutParams();
        i1.setMargins(0,height*2844/100000,0,0);
        btnSearch.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*12/411);
        btnChart.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*12/411);
        btnEditFood.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*12/411);
        btnRecipe.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*12/411);
        btnHistory.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*12/411);
        searchText.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*14/411);
        showweight.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*40/411);
        unit.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*14/411);
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*15/411);
        daily_calories.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*10/411);
        showname.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*24/411);
        showcarbohydrate.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*14/411);
        showprotein.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*14/411);
        showfat.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*14/411);
        weightcarbo.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*14/411);
        weightprot.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*14/411);
        weightfat.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*14/411);
        wcalories.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*14/411);
        calories.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*14/411);

    }
    public void detectDarkMode(){
        theme = sharedPref.getString(getString(R.string.theme), "light");
        if (theme.equals("light")){
            sc.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            main.setTextColor(getResources().getColor(R.color.colorBlack));
            searchitem.setTextColor(getResources().getColor(R.color.colorBlack));
            searchText.setBackgroundResource(R.drawable.search_field_rounded);
            searchText.setHintTextColor(getResources().getColor(R.color.colorGrey));
            searchText.setTextColor(getResources().getColor(R.color.colorBlack));
            searchText.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_search_blue_24dp), null, null, null);
            btnSearch.setTextColor(getResources().getColor(R.color.colorWhite));
            btnSearch.setBackgroundResource(R.drawable.go_search_rounded);

            weight_background.setBackgroundResource(R.drawable.current_weighting_rounded);
            showweight.setTextColor(getResources().getColor(R.color.colorWhite));
            unit.setTextColor(getResources().getColor(R.color.colorWhite));
            text.setTextColor(getResources().getColor(R.color.colorBlack));
            daily_calories.setTextColor(getResources().getColor(R.color.colorBlack));
            showname.setTextColor(getResources().getColor(R.color.colorBlack));
            display.setBackgroundResource(R.drawable.table_rounded);
            showcarbohydrate.setTextColor(getResources().getColor(R.color.colorWhite));
            showprotein.setTextColor(getResources().getColor(R.color.colorWhite));
            showfat.setTextColor(getResources().getColor(R.color.colorWhite));
            calories.setTextColor(getResources().getColor(R.color.colorWhite));
            weightcarbo.setTextColor(getResources().getColor(R.color.colorWhite));
            weightprot.setTextColor(getResources().getColor(R.color.colorWhite));
            weightfat.setTextColor(getResources().getColor(R.color.colorWhite));
            wcalories.setTextColor(getResources().getColor(R.color.colorWhite));
            btnRecipe.setBackgroundResource(R.drawable.edit_item_rounded);
            btnEditFood.setBackgroundResource(R.drawable.edit_item_rounded);
            btnChart.setBackgroundResource(R.drawable.save_entry_rounded);
            btnHistory.setBackgroundResource(R.drawable.save_entry_rounded);
            btnRecipe.setTextColor(getResources().getColor(R.color.colorBlack));
            btnEditFood.setTextColor(getResources().getColor(R.color.colorBlack));
            btnChart.setTextColor(getResources().getColor(R.color.colorBlack));
            btnHistory.setTextColor(getResources().getColor(R.color.colorBlack));

            b1.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            b2.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            b3.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            b4.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            b1.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_home_green_24dp),null,null);
            b2.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_library_add_black_24dp),null,null);
            b3.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_history_black_24dp),null,null);
            b4.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_settings_black_24dp),null,null);
            b1.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            b2.setTextColor(getResources().getColor(R.color.darkThemeBackground));
            b3.setTextColor(getResources().getColor(R.color.darkThemeBackground));
            b4.setTextColor(getResources().getColor(R.color.darkThemeBackground));
            searchText.setDropDownBackgroundResource(R.color.colorWhite);


        } else if (theme.equals("dark")){
            sc.setBackgroundColor(getResources().getColor(R.color.darkThemeBackground));
            main.setTextColor(getResources().getColor(R.color.colorWhite));
            searchitem.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            searchText.setBackgroundResource(R.drawable.search_field_rounded_dark);
            searchText.setTextColor(getResources().getColor(R.color.colorWhite));
            searchText.setHintTextColor(getResources().getColor(R.color.colorDarkThemeHintGrey));
            searchText.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_search_grey_24dp), null, null, null);
            btnSearch.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            btnSearch.setBackgroundResource(R.drawable.go_search_rounded_dark);

            weight_background.setBackgroundResource(R.drawable.current_weighting_rounded_dark);
            showweight.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            unit.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            text.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            daily_calories.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            showname.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            display.setBackgroundResource(R.drawable.table_rounded_dark);
            showcarbohydrate.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            showprotein.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            showfat.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            calories.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            weightcarbo.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            weightprot.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            weightfat.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            wcalories.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));

            btnRecipe.setBackgroundResource(R.drawable.edit_item_rounded_dark);
            btnEditFood.setBackgroundResource(R.drawable.edit_item_rounded_dark);
            btnChart.setBackgroundResource(R.drawable.save_entry_rounded_dark);
            btnHistory.setBackgroundResource(R.drawable.save_entry_rounded_dark);
            btnRecipe.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            btnEditFood.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            btnChart.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            btnHistory.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));

            b1.setBackgroundColor(getResources().getColor(R.color.darkThemeBackground));
            b2.setBackgroundColor(getResources().getColor(R.color.darkThemeBackground));
            b3.setBackgroundColor(getResources().getColor(R.color.darkThemeBackground));
            b4.setBackgroundColor(getResources().getColor(R.color.darkThemeBackground));
            b1.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_home_blue_24dp),null,null);
            b2.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_library_add_white_24dp),null,null);
            b3.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_history_white_24dp),null,null);
            b4.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_settings_white_24dp),null,null);
            b1.setTextColor(getResources().getColor(R.color.colorBlue));
            b2.setTextColor(getResources().getColor(R.color.colorWhite));
            b3.setTextColor(getResources().getColor(R.color.colorWhite));
            b4.setTextColor(getResources().getColor(R.color.colorWhite));
            searchText.setDropDownBackgroundResource(R.color.darkThemeBackground);

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
