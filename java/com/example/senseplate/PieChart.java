package com.example.senseplate;

// Importing the libraries used.
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class PieChart extends Activity {

    // Declaring the global variables.
    JSONParser jsonParser = new JSONParser();
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_FOOD = "food";
    private static final String TAG_NAME = "ingredient";
    private static final String TAG_CARBOHYDRATE = "carbohydrate";
    private static final String TAG_PROTEIN = "protein";
    private static final String TAG_FAT = "fat";
    private static final String TAG_WEIGHT = "weight";


    Button button1, button2, button3;
    TextView chart_heading, name_heading, name, carbs_heading, carbs_field, protein_heading, protein_field, fats_heading, fats_field, weight_heading, weight_field, calories_heading, calories_field;
    String names, weights, usernames, calories,sender,recipe, theme;
    String iname, icarbo, iprot, ifat, iweight;
    Float carbo, prot, fat;
    Spinner spinner;
    List<SliceValue> pieData = new ArrayList<>();
    ArrayList<String> carboList, protList, fatList, nameList, weightList;
    ArrayAdapter<String> adapter;
    PieChartView pieChartView;
    PieChartData pieChartData;
    RelativeLayout rl;
    int numberData = 3, width, height;
    // Specifies the format used (2 decimal places).
    DecimalFormat f = new DecimalFormat("####.00");
    SharedPreferences sharedPref;
    // Method called when the Activity is created.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.piechart);

        // Initializes the Arrays.
        carboList = new ArrayList<String>();
        protList = new ArrayList<String>();
        fatList = new ArrayList<String>();
        nameList = new ArrayList<String>();
        weightList = new ArrayList<String>();

        // Gets the dimensions of the device where the app runs.
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        sharedPref = getSharedPreferences("com.example.senseplate", Context.MODE_PRIVATE);
        usernames = sharedPref.getString(getString(R.string.username),"");
        theme = sharedPref.getString(getString(R.string.theme), "light");
        // Initializes the widgets.
        rl = (RelativeLayout) findViewById(R.id.rl);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        chart_heading = (TextView) findViewById(R.id.chart_header);
        name = (TextView) findViewById(R.id.name);
        name_heading = (TextView) findViewById(R.id.name_header);
        carbs_field = (TextView) findViewById(R.id.carbs_field);
        protein_field = (TextView) findViewById(R.id.protein_field);
        fats_field = (TextView) findViewById(R.id.fats_field);
        weight_field = (TextView) findViewById(R.id.weight_field);
        calories_field = (TextView) findViewById(R.id.calories_field);
        carbs_heading = (TextView) findViewById(R.id.carbs_heading);
        protein_heading = (TextView) findViewById(R.id.protein_heading);
        fats_heading = (TextView) findViewById(R.id.fats_heading);
        weight_heading = (TextView) findViewById(R.id.weight_heading);
        calories_heading = (TextView) findViewById(R.id.calories_heading);
        spinner = (Spinner) findViewById(R.id.spinner);
        pieChartView = findViewById(R.id.chart);
        pieChartData = new PieChartData(pieData);


        // J7: 1280/720
        // Emulator: 2392/1440

        // Checks if the previous activity was the addrecipe.
        sender = getIntent().getStringExtra("sender");
        usernames = getIntent().getStringExtra("username");
        if (sender.equals("addrecipe") || sender.equals("editrecipe")) {
            // Stores the arrays received from the previous activity and adds the names into
            // the spinner.
            spinner.setVisibility(View.VISIBLE);
            nameList = getIntent().getStringArrayListExtra("name");
            weightList = getIntent().getStringArrayListExtra("weight");
            carboList = getIntent().getStringArrayListExtra("carbo");
            protList = getIntent().getStringArrayListExtra("prot");
            fatList = getIntent().getStringArrayListExtra("fat");
            names = nameList.get(0);
            weights = weightList.get(0);
            carbo = Float.parseFloat(carboList.get(0));
            prot = Float.parseFloat(protList.get(0));
            fat = Float.parseFloat(fatList.get(0));
            button1.setText("Calories per Unit");
            setSpinner();
        } else {
            // Stores the String values from the previous activity.
            names = getIntent().getStringExtra("name");
            weights = getIntent().getStringExtra("weight");
            carbo = Float.parseFloat(getIntent().getStringExtra("carbo"));
            prot = Float.parseFloat(getIntent().getStringExtra("prot"));
            fat = Float.parseFloat(getIntent().getStringExtra("fat"));
            recipe = getIntent().getStringExtra("recipe");

            // Checks if it is a recipe.
            if (recipe.equals("Yes")) {
                // Makes visible the spinner and adds the Recipe values to the array.
                spinner.setVisibility(View.VISIBLE);
                nameList.add(names.substring(0, names.length() - 13) + "Recipe)");
                carboList.add(String.valueOf(carbo));
                protList.add(String.valueOf(prot));
                fatList.add(String.valueOf(fat));
                weightList.add(weights);
                // Executes the class that gets all the ingredients.
                new getIngredients().execute();
            }
        }

        // Calculates the calories.
        calories = String.valueOf(carbo * 4 + prot * 4 + fat * 9);
        // Displays the nutritional values along with the name, the weight_field and the username.
        name.setText(names);
        carbs_field.setText(f.format(carbo * 4)+"kcal");
        protein_field.setText(f.format(prot * 4)+"kcal");
        fats_field.setText(f.format(fat * 9)+"kcal");
        weight_field.setText(f.format(Float.parseFloat(weights)) + "g");
        calories_field.setText(f.format(Float.parseFloat(calories))+"kcal");

        // Adds the data to the pie chart.
        if (theme.equals("light")){
            pieData.add(new SliceValue(carbo * 4, Color.BLUE).
                    setLabel("Carbs (" + f.format(carbo *
                            4 *100/Float.parseFloat(calories)) + "%)"));
            pieData.add(new SliceValue(prot * 4, Color.GREEN).
                    setLabel("Proteins (" + f.format(prot *
                            4 *100/Float.parseFloat(calories)) + "%)"));
            pieData.add(new SliceValue(fat * 9, Color.RED).
                    setLabel("Fats (" + f.format(fat *
                            9 *100/Float.parseFloat(calories)) + "%)"));
        } else {
            pieData.add(new SliceValue(carbo * 4, getResources().getColor(R.color.darkThemeBlue)).
                    setLabel("Carbs (" + f.format(carbo *
                            4 *100/Float.parseFloat(calories)) + "%)"));
            pieData.add(new SliceValue(prot * 4, getResources().getColor(R.color.darkThemeGreen)).
                    setLabel("Proteins (" + f.format(prot *
                            4 *100/Float.parseFloat(calories)) + "%)"));
            pieData.add(new SliceValue(fat * 9, getResources().getColor(R.color.darkThemeRed)).
                    setLabel("Fats (" + f.format(fat *
                            9 *100/Float.parseFloat(calories)) + "%)"));
        }
        pieChartData.setHasLabels(true);
        // Sets the text of the center of the pie.
        pieChartData.setHasCenterCircle(true).setCenterText1("Calories");
        if (theme.equals("light")){
            pieChartData.setCenterText1Color(getResources().getColor(R.color.colorBlack));
        } else if (theme.equals("dark")){
            pieChartData.setCenterText1Color(getResources().getColor(R.color.colorDarkThemeTitleGrey));
        }
        pieChartView.setPieChartData(pieChartData);

        // Sets the event for the first button (Total Calories).
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeButton();
            }
        });
//        // Sets the event for the second button (Calories/100 grams).
//        button2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Removes the data from the array.
//                for (int i = 0; i < 3; i++) {
//                    pieData.remove(0);
//                }
//                // Calculates the calories per nutritional value and divide it by the weight_field
//                // to get the result per 100 grams.
//                float Carb = Float.parseFloat(carbo) * 400 / Float.parseFloat(weights);
//                float Prot = Float.parseFloat(prot) * 400 / Float.parseFloat(weights);
//                float Fat = Float.parseFloat(fat) * 900 / Float.parseFloat(weights);
//                // Calculates the total calories.
//                calories = String.valueOf(Carb + Prot + Fat);
//                // Displays the nutritional values along with the name, the weight_field and the username.
//                name.setText(names);
//                carbs_heading.setText("Carbs calories");
//                protein_heading.setText("Protein calories");
//                fats_heading.setText("Fats calories");
//                carbs_field.setText(f.format(Carb));
//                protein_field.setText(f.format(Fat));
//                weight_field.setText(f.format("100g"));
//                calories_field.setText(f.format(Float.parseFloat(calories))+ "kcal");
//
//                // Adds the data to the pie chart.
//                pieData.add(new SliceValue(Carb, Color.BLUE).setLabel("Carbohydrates ("+
//                        f.format(Carb * 100/Float.parseFloat(calories))+"%)"));
//                pieData.add(new SliceValue(Prot, Color.GREEN).setLabel("Proteins ("+
//                        f.format(Prot * 100/Float.parseFloat(calories))+"%)"));
//                pieData.add(new SliceValue(Fat, Color.RED).setLabel("Fats ("+
//                        f.format(Fat * 100/Float.parseFloat(calories))+"%)"));
//                PieChartData pieChartData = new PieChartData(pieData);
//                pieChartData.setHasLabels(true);
//                // Sets the text of the center of the pie.
//                pieChartData.setHasCenterCircle(true).setCenterText1("Calories");
//                pieChartView.setPieChartData(pieChartData);
//            }
//        });
        // Sets the event for the third button (Grams).
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Removes the data from the array.
                Log.d("Es", String.valueOf(pieData.size()));
                for (int i = 0; i < numberData; i++) {
                        pieData.remove(0);

                }
                numberData = 4;
                // Calculates the total calories.
                calories = String.valueOf(carbo * 4 + prot
                        * 4 + fat * 9);
                // Displays the nutritional values along with the name, the weight_field and the username.
                name.setText(names);
                    carbs_field.setText(f.format(carbo)+"g");
                    protein_field.setText(f.format(prot)+"g");
                    fats_field.setText(f.format(fat)+"g");
                    weight_field.setText(f.format(Float.parseFloat(weights)) + "g");
                    calories_field.setText(f.format(Float.parseFloat(calories))+"kcal");

                    // Adds the data to the pie chart.
                if (theme.equals("light")){
                    pieData.add(new SliceValue(carbo, Color.BLUE).
                            setLabel("Carbs (" + f.format(carbo*
                                    100/Float.parseFloat(weights)) + "%)"));
                    pieData.add(new SliceValue(prot, Color.GREEN).
                            setLabel("Proteins (" + f.format(prot*
                                    100/Float.parseFloat(weights)) + "%)"));
                    pieData.add(new SliceValue(fat, Color.RED).
                            setLabel("Fats (" + f.format(fat*
                                    100/Float.parseFloat(weights)) + "%)"));
                    pieData.add(new SliceValue(Float.parseFloat(weights)-carbo-prot-fat, getResources().getColor(R.color.colorPrimary)).
                            setLabel("None (" + f.format((Float.parseFloat(weights)-carbo-prot-fat)* 100/Float.parseFloat(weights)) + "%)"));
                } else if (theme.equals("dark")){
                    pieData.add(new SliceValue(carbo, getResources().getColor(R.color.darkThemeBlue)).
                            setLabel("Carbs (" + f.format(carbo*
                                    100/Float.parseFloat(weights)) + "%)"));
                    pieData.add(new SliceValue(prot, getResources().getColor(R.color.darkThemeGreen)).
                            setLabel("Proteins (" + f.format(prot*
                                    100/Float.parseFloat(weights)) + "%)"));
                    pieData.add(new SliceValue(fat, getResources().getColor(R.color.darkThemeRed)).
                            setLabel("Fats (" + f.format(fat*
                                    100/Float.parseFloat(weights)) + "%)"));
                    pieData.add(new SliceValue(Float.parseFloat(weights)-carbo-prot-fat, getResources().getColor(R.color.colorPrimaryDark)).
                            setLabel("None (" + f.format((Float.parseFloat(weights)-carbo-prot-fat)* 100/Float.parseFloat(weights)) + "%)"));
                }

                    pieChartData.setHasLabels(true);
                // Sets the text of the center of the pie.
                pieChartData.setHasCenterCircle(true).setCenterText1("Grams");
                    pieChartView.setPieChartData(pieChartData);
            }
        });
        // Sets the event for the spinner..
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // Uses the position of the item selected to get the nutritional values, weight_field
                // and name from the arrays.
                if (theme.equals("light")) {
                    ((TextView) view).setTextColor(Color.BLACK);
                } else if (theme.equals("dark")){
                    if (position == 0) {
                        ((TextView) view).setTextColor(Color.GRAY);
                    } else {
                        ((TextView) view).setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
                    }
                }
                names = nameList.get(position);
                carbo = Float.parseFloat(carboList.get(position));
                prot = Float.parseFloat(protList.get(position));
                fat = Float.parseFloat(fatList.get(position));
                weights = weightList.get(position);
                // Calls the function that calculates and displays the Total Calories.
                initializeButton();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
        resize();
        detectDarkMode();
    }
    public void initializeButton(){
        // Removes the data from the array.

        for (int i = 0; i < numberData; i++) {
            pieData.remove(0);
        }
        numberData = 3;
        // Calculates the total calories.
        calories = String.valueOf(carbo * 4 + prot * 4 +
                fat * 9);
        // Displays the nutritional values along with the name, the weight_field and the username.
        name.setText(names);
        carbs_field.setText(f.format(
                carbo * 4)+"kcal");
        protein_field.setText(f.format(prot * 4)+"kcal");
        fats_field.setText(f.format(fat * 9)+"kcal");
        weight_field.setText(f.format(Float.parseFloat(weights))+"g");
        calories_field.setText(f.format(Float.parseFloat(calories))+"kcal");
        // Adds the data to the pie chart.
        if (theme.equals("light")){
            pieData.add(new SliceValue(carbo * 4, Color.BLUE).
                    setLabel("Carbs (" + f.format(carbo *
                            4 *100/Float.parseFloat(calories)) + "%)"));
            pieData.add(new SliceValue(prot * 4, Color.GREEN).
                    setLabel("Proteins (" + f.format(prot *
                            4 *100/Float.parseFloat(calories)) + "%)"));
            pieData.add(new SliceValue(fat * 9, Color.RED).
                    setLabel("Fats (" + f.format(fat *
                            9 *100/Float.parseFloat(calories)) + "%)"));
        } else {
            pieData.add(new SliceValue(carbo * 4, getResources().getColor(R.color.darkThemeBlue)).
                    setLabel("Carbs (" + f.format(carbo *
                            4 *100/Float.parseFloat(calories)) + "%)"));
            pieData.add(new SliceValue(prot * 4, getResources().getColor(R.color.darkThemeGreen)).
                    setLabel("Proteins (" + f.format(prot *
                            4 *100/Float.parseFloat(calories)) + "%)"));
            pieData.add(new SliceValue(fat * 9, getResources().getColor(R.color.darkThemeRed)).
                    setLabel("Fats (" + f.format(fat *
                            9 *100/Float.parseFloat(calories)) + "%)"));
        }

        pieChartData.setHasLabels(true);
        // Sets the text of the center of the pie.
        pieChartData.setHasCenterCircle(true).setCenterText1("Calories");
        pieChartView.setPieChartData(pieChartData);

    }
    // Class that performs the connection to the database to get the ingredients from recipes.
    // The class AsyncTask is the superclass that enables to perform tasks in the background.
    class getIngredients extends AsyncTask<String, String, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("res", usernames);
        }

        // Method responsible of the connection to the database.
        @Override
        protected String doInBackground(String... strings) {
            // Local variable that contains the path of the PHP file used for connection
            String url_get_Ingredients = "http://nam33.student.eda.kent.ac.uk/get_Ingredients.php";
            // Creating an ArrayList and adding the variables that will be sent.
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("recipeName",
                    names.substring(0,names.length()-13) +usernames +")"));
            params.add(new BasicNameValuePair("idUser", usernames));
            // Calls the method that makes the connection passing the URL variables, the Method and
            // the ArrayList as parameters.
            JSONObject json = jsonParser.makeHttpRequest(url_get_Ingredients,
                    "GET", params);

            try {
                // Checks if the operation was performed successfully.
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1){
                    int total_weight = 0;
                    // Gets the name, nutritional values, weight_field of each ingredient from the
                    // recipe and stores each value in an array.
                    JSONArray food = json.getJSONArray(TAG_FOOD);
                    for (int i = 0; i < food.length(); i++){
                        JSONObject c = food.getJSONObject(i);
                        total_weight += Integer.parseInt(c.getString(TAG_WEIGHT));
                    }
                    for (int i = 0; i < food.length(); i++){
                        JSONObject c = food.getJSONObject(i);
                        iname = c.getString(TAG_NAME) + " (Ingredient)";
                        icarbo = c.getString(TAG_CARBOHYDRATE);
                        iprot = c.getString(TAG_PROTEIN);
                        ifat = c.getString(TAG_FAT);
                        iweight = c.getString(TAG_WEIGHT);
                        nameList.add(iname);
                        carboList.add(String.valueOf(Float.parseFloat(icarbo)*Float.parseFloat(iweight)/total_weight));
                        protList.add(String.valueOf(Float.parseFloat(iprot)*Float.parseFloat(iweight)/total_weight));
                        fatList.add(String.valueOf(Float.parseFloat(ifat)*Float.parseFloat(iweight)/total_weight));
                        weightList.add(String.valueOf(Float.parseFloat(iweight)*Float.parseFloat(weights)/total_weight));
                    }

            } else {
            }
        } catch (
        JSONException e){
            e.printStackTrace();
        }
            return null;
    }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // Initializes the adapter and stores the names of the ingredients.
            setSpinner();
        }
    }

    private void setSpinner(){
        if (theme.equals("light")) {
            adapter = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_spinner_dropdown_item, nameList) {
                @Override
                public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view;
                    if (position == 0) {
                        tv.setTextColor(Color.GRAY);
                    } else {
                        tv.setTextColor(Color.BLACK);
                    }
                    return view;
                }
            };
            // Sets the adapter in the spinner.
            spinner.setAdapter(adapter);
        } else if (theme.equals("dark")){
            adapter = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_spinner_dropdown_item, nameList) {
                @Override
                public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view;

                    if (position == 0) {
                        tv.setTextColor(Color.GRAY);
                    } else {
                        tv.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
                    }
                    return view;
                }

            };
            // Sets the adapter in the spinner.
            spinner.setAdapter(adapter);
        }
    }

    public void resize() {
        button1.getLayoutParams().width = width * 45 / 100;
        button3.getLayoutParams().width = width * 45 / 100;
        chart_heading.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width / Resources.getSystem().getDisplayMetrics().density) * 33 / 411);
        name_heading.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width / Resources.getSystem().getDisplayMetrics().density) * 21 / 411);
        carbs_heading.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width / Resources.getSystem().getDisplayMetrics().density) * 21 / 411);
        protein_heading.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width / Resources.getSystem().getDisplayMetrics().density) * 21 / 411);
        fats_heading.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width / Resources.getSystem().getDisplayMetrics().density) * 21 / 411);
        weight_heading.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width / Resources.getSystem().getDisplayMetrics().density) * 21 / 411);
        calories_heading.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width / Resources.getSystem().getDisplayMetrics().density) * 21 / 411);
        name.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width / Resources.getSystem().getDisplayMetrics().density) * 14 / 411);
        carbs_field.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width / Resources.getSystem().getDisplayMetrics().density) * 14 / 411);
        protein_field.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width / Resources.getSystem().getDisplayMetrics().density) * 14 / 411);
        fats_field.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width / Resources.getSystem().getDisplayMetrics().density) * 14 / 411);
        weight_field.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width / Resources.getSystem().getDisplayMetrics().density) * 14 / 411);
        calories_field.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width / Resources.getSystem().getDisplayMetrics().density) * 14 / 411);
        button1.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*12/411);
        button3.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*12/411);

        RelativeLayout.LayoutParams f1 = (RelativeLayout.LayoutParams) carbs_heading.getLayoutParams();
        f1.setMargins(width * 170 / 10000, width * 4866 / 100000, 0, 0);
        RelativeLayout.LayoutParams f3 = (RelativeLayout.LayoutParams) protein_heading.getLayoutParams();
        f3.setMargins(0, width * 4866 / 100000, 0, 0);
        RelativeLayout.LayoutParams f2 = (RelativeLayout.LayoutParams) fats_heading.getLayoutParams();
        f2.setMargins(0, width * 4866 / 100000, width * 170 / 10000, 0);
        RelativeLayout.LayoutParams f4 = (RelativeLayout.LayoutParams) weight_heading.getLayoutParams();
        f4.setMargins(width * 1946 / 10000, width * 2433 / 100000, 0, 0);
        RelativeLayout.LayoutParams f5 = (RelativeLayout.LayoutParams) calories_heading.getLayoutParams();
        f5.setMargins(0, width * 2433 / 100000, width * 9732 / 100000, 0);
        RelativeLayout.LayoutParams f6 = (RelativeLayout.LayoutParams) carbs_field.getLayoutParams();
        f6.setMargins(0, width * 1216 / 10000, 0, 0);
        RelativeLayout.LayoutParams f7 = (RelativeLayout.LayoutParams) protein_field.getLayoutParams();
        f7.setMargins(0, width * 1216 / 10000, 0, 0);
        RelativeLayout.LayoutParams f8 = (RelativeLayout.LayoutParams) fats_field.getLayoutParams();
        f8.setMargins(0, width * 1216 / 10000, 0, 0);
        RelativeLayout.LayoutParams f9 = (RelativeLayout.LayoutParams) weight_field.getLayoutParams();
        f9.setMargins(0, width * 9732 / 100000, 0, 0);
        RelativeLayout.LayoutParams f10 = (RelativeLayout.LayoutParams) calories_field.getLayoutParams();
        f10.setMargins(0, width * 9732 / 100000, 0, 0);
    }

    public void detectDarkMode(){
        if (theme.equals("light")){
            rl.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            chart_heading.setTextColor(getResources().getColor(R.color.colorBlack));
            name_heading.setTextColor(getResources().getColor(R.color.colorBlack));
            name.setTextColor(getResources().getColor(R.color.colorBlack));
            name.setBackgroundResource(R.drawable.search_field_rounded);
            carbs_heading.setTextColor(getResources().getColor(R.color.colorBlack));
            carbs_field.setTextColor(getResources().getColor(R.color.colorBlack));
            carbs_field.setBackgroundResource(R.drawable.search_field_rounded);
            protein_heading.setTextColor(getResources().getColor(R.color.colorBlack));
            protein_field.setTextColor(getResources().getColor(R.color.colorBlack));
            protein_field.setBackgroundResource(R.drawable.search_field_rounded);
            fats_heading.setTextColor(getResources().getColor(R.color.colorBlack));
            fats_field.setTextColor(getResources().getColor(R.color.colorBlack));
            fats_field.setBackgroundResource(R.drawable.search_field_rounded);
            weight_heading.setTextColor(getResources().getColor(R.color.colorBlack));
            weight_field.setTextColor(getResources().getColor(R.color.colorBlack));
            weight_field.setBackgroundResource(R.drawable.search_field_rounded);
            calories_heading.setTextColor(getResources().getColor(R.color.colorBlack));
            calories_field.setTextColor(getResources().getColor(R.color.colorBlack));
            calories_field.setBackgroundResource(R.drawable.search_field_rounded);
            button1.setTextColor(getResources().getColor(R.color.colorBlack));
            button1.setBackgroundResource(R.drawable.edit_item_rounded);
            button3.setTextColor(getResources().getColor(R.color.colorBlack));
            button3.setBackgroundResource(R.drawable.edit_item_rounded);
            pieChartData.setCenterText1Color(getResources().getColor(R.color.colorBlack));
            spinner.setPopupBackgroundResource(R.color.colorWhite);
        } else if (theme.equals("dark")){
            rl.setBackgroundColor(getResources().getColor(R.color.darkThemeBackground));
            chart_heading.setTextColor(getResources().getColor(R.color.colorWhite));
            name_heading.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            name.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            name.setBackgroundResource(R.drawable.search_field_rounded_dark);
            carbs_heading.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            carbs_field.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            carbs_field.setBackgroundResource(R.drawable.search_field_rounded_dark);
            protein_heading.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            protein_field.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            protein_field.setBackgroundResource(R.drawable.search_field_rounded_dark);
            fats_heading.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            fats_field.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            fats_field.setBackgroundResource(R.drawable.search_field_rounded_dark);
            weight_heading.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            weight_field.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            weight_field.setBackgroundResource(R.drawable.search_field_rounded_dark);
            calories_heading.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            calories_field.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            calories_field.setBackgroundResource(R.drawable.search_field_rounded_dark);
            button1.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            button1.setBackgroundResource(R.drawable.edit_item_rounded_dark);
            button3.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            button3.setBackgroundResource(R.drawable.edit_item_rounded_dark);
            pieChartData.setCenterText1Color(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            spinner.setPopupBackgroundResource(R.color.darkThemeBackground);
        }
    }
}
