package com.example.senseplate;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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


public class Testing extends Activity {

    JSONParser jsonParser = new JSONParser();
    String theme, user;
    AutoCompleteTextView name_ingredient_field;
    EditText Carbohydrates, Proteins, Fats, Weight, number_ingredients_field, recipename;
    TextView recipe_header, errors, username,  textWeight, textCarb, textProt, textFat, calories, recipe_item,number_ingredients , total_calories, ingredient_item, total_calories_100, calories_100,name_ingredient, line, line2, line3;
    int width, height, n_ingredients = 0, change = 0;
    ScrollView background;
    LinearLayout mainli;
    Button number_ingredientButton, add_ingredient, delete_ingredient, chart, caloriesButton, save;
    Spinner spinner_ingredients;
    ArrayList<String> ingredients, ingredientList, carbo, carboList, prot, protList, fat, fatList, weights, weightList, items;
    ArrayAdapter<String> adapter, adapter2;
    private SharedPreferences sharedPref;
    boolean name_watch = false;
    DecimalFormat f = new DecimalFormat("####.00");
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_FOOD = "food";
    private static final String TAG_NAME = "name";
    private static final String TAG_INGREDIENT = "ingredient";
    private static final String TAG_CARBOHYDRATE = "carbohydrate";
    private static final String TAG_PROTEIN = "protein";
    private static final String TAG_FAT = "fat";
    private static final String TAG_WEIGHT = "weight";


    int totalWeight = 0;
    float totalCarbo = 0, totalProt = 0, totalFat = 0;
    float per100Carbo = 0, per100Prot = 0, per100Fat = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test2);



        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        // Initializing the widgets.
        background = (ScrollView) findViewById(R.id.background);
        mainli = (LinearLayout) findViewById(R.id.mainli);
        recipe_header = (TextView) findViewById(R.id.recipe);
        line3 = (TextView) findViewById(R.id.line3);
        line = (TextView) findViewById(R.id.line);
        spinner_ingredients = (Spinner) findViewById(R.id.spinner_ingredients);
        line2 = (TextView) findViewById(R.id.line2);
        recipe_item = (TextView) findViewById(R.id.recipe_item);
        number_ingredients = (TextView) findViewById(R.id.number_ingredients);
        number_ingredients_field = (EditText) findViewById(R.id.number);
        recipename = (EditText) findViewById(R.id.recipename);
        ingredient_item = (TextView) findViewById(R.id.ingredient_item);
        name_ingredient = (TextView) findViewById(R.id.name_ingredient);
        name_ingredient_field = (AutoCompleteTextView) findViewById(R.id.name_ingredient_field);
        Carbohydrates = (EditText) findViewById(R.id.carbohydrate_add_field);
        Weight = (EditText) findViewById(R.id.weight_add_field);
        Proteins = (EditText) findViewById(R.id.protein_add_field);
        Fats = (EditText) findViewById(R.id.fat_add_field);
        errors = (TextView) findViewById(R.id.error);
        textCarb = (TextView) findViewById(R.id.carbohydrate_add);
        textWeight = (TextView) findViewById(R.id.weight_add);
        textProt = (TextView) findViewById(R.id.protein_add);
        textFat = (TextView) findViewById(R.id.fat_add);
        calories = (TextView) findViewById(R.id.calories_field);
        total_calories = (TextView) findViewById(R.id.total_calories);
        calories_100 = (TextView) findViewById(R.id.calories_field_100);
        total_calories_100 = (TextView) findViewById(R.id.total_calories_100);


        add_ingredient = (Button) findViewById(R.id.add_ingredient);
        delete_ingredient = (Button) findViewById(R.id.delete_ingredient);
        number_ingredientButton = (Button) findViewById(R.id.number_ingredientButton);
        chart = (Button) findViewById(R.id.chart);
        caloriesButton = (Button) findViewById(R.id.show_calories);
        save = (Button) findViewById(R.id.save);

        sharedPref = getSharedPreferences("com.example.senseplate", Context.MODE_PRIVATE);
        user = sharedPref.getString(getString(R.string.username),"");
        theme = sharedPref.getString(getString(R.string.theme), "light");

        ingredientList = new ArrayList<>();
        carboList = new ArrayList<>();
        protList = new ArrayList<>();
        fatList = new ArrayList<>();
        weightList = new ArrayList<>();
        items = new ArrayList<>();
        if (theme.equals("dark")) {
            adapter2 = new ArrayAdapter<>(getApplicationContext(), R.
                    layout.spinner_ingredient, items);
        } else if (theme.equals("light")){
            adapter2 = new ArrayAdapter<>(getApplicationContext(), android.R.
                    layout.simple_list_item_1, items);
        }

        ingredientList.add(0,"Choose the number of ingredients");
        setSpinner();

        number_ingredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (number_ingredients_field.getText().toString().length() > 0){
                    n_ingredients = Integer.parseInt(number_ingredients_field.getText().toString());
                    if (n_ingredients > 0) {
                        if (n_ingredients < 21) {
                            removeErrors();
                            ingredientList.remove(0);
                            ingredientList.add(0,"Select the Ingredient:");

                            if (carboList.size() == 0) {
                                for (int i = 0; i <= n_ingredients; i++) {
                                    ingredientList.add("Ingredient #" + (i) + ": ");
                                    carboList.add("0");
                                    protList.add("0");
                                    fatList.add("0");
                                    weightList.add("0");
                                }
                                ingredientList.remove(1);
                            } else {
                                for (int i = carboList.size(); i <= n_ingredients; i++) {
                                    ingredientList.add("Ingredient #" + (i) + ": ");
                                    carboList.add("0");
                                    protList.add("0");
                                    fatList.add("0");
                                    weightList.add("0");
                                }
                            }
                            setSpinner();
                            fieldsNotClickable();
                        } else {
                            setErrors("The number of ingredients must be less than 20");
                        }
                    } else {
                        setErrors("The number of ingredients must be greater than 0");
                    }
                } else {
                    setErrors("The number of ingredients must be greater than 0");
                }
            }
        });

        add_ingredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (number_ingredients_field.getText().toString().length() > 0){
                    n_ingredients = Integer.parseInt(number_ingredients_field.getText().toString());
                    if (n_ingredients < 20) {
                        int position = spinner_ingredients.getSelectedItemPosition();
                        if (carboList.get(position).equals("0") && protList.get(position).equals("0") && fatList.get(position).equals("0") && weightList.get(position).equals("0") && position != 0) {
                            ingredientList.remove(position);
                            carboList.remove(position);
                            protList.remove(position);
                            fatList.remove(position);
                            weightList.remove(position);
                            ingredientList.add(position, "Ingredient #"+n_ingredients+": "+name_ingredient_field.getText().toString());
                            carboList.add(position, Carbohydrates.getText().toString());
                            protList.add(position, Proteins.getText().toString());
                            fatList.add(position, Fats.getText().toString());
                            weightList.add(position, Weight.getText().toString());
                        }
                        n_ingredients++;
                        number_ingredients_field.setText(String.valueOf(n_ingredients));
                        ingredientList.add("Ingredient #" + (n_ingredients)+": ");
                        carboList.add("0");
                        protList.add("0");
                        fatList.add("0");
                        weightList.add("0");
                        spinner_ingredients.setSelection(n_ingredients);
                        name_ingredient_field.setText("");
                        Carbohydrates.setText("");
                        Proteins.setText("");
                        Fats.setText("");
                        Weight.setText("");
                        calories_100.setText("");
                        calories.setText("");
                        change = 0;
                        removeErrors();
                        fieldsNotClickable();

                    } else {
                        setErrors("The number of ingredients must be less than 20");
                    }
                } else {
                    removeErrors();
                    n_ingredients++;
                    number_ingredients_field.setText(String.valueOf(n_ingredients));
                    ingredientList.remove(0);
                    ingredientList.add(0,"Select the Ingredient:");
                    ingredientList.add("Ingredient #" + (n_ingredients) + ": ");

                    setSpinner();
                    for (int i = 0; i <= n_ingredients; i++) {
                        carboList.add("0");
                        protList.add("0");
                        fatList.add("0");
                        weightList.add("0");
                    }

                }
            }
        });
        delete_ingredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (number_ingredients_field.getText().toString().length() > 0) {
                    n_ingredients = Integer.parseInt(number_ingredients_field.getText().toString());
                    if (change != 0) {
                        if (n_ingredients > 0) {
                            removeErrors();
                            if (ingredientList.get(change).length() > 15) {
                                AlertDialog.Builder alert = new AlertDialog.Builder(Testing.this);
                                alert.setTitle("Delete Ingredient");
                                alert.setMessage("Are you sure you want to delete " + ingredientList.get(change) + "?");
                                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        name_ingredient_field.setText("");
                                        Carbohydrates.setText("");
                                        Proteins.setText("");
                                        Fats.setText("");
                                        Weight.setText("");
                                        calories.setText("");
                                        calories_100.setText("");
                                        n_ingredients--;
                                        ingredientList.remove(change);
                                        carboList.remove(change);
                                        protList.remove(change);
                                        fatList.remove(change);
                                        weightList.remove(change);
                                        number_ingredients_field.setText(String.valueOf(n_ingredients));
                                        spinner_ingredients.setSelection(0);
                                        change = 0;
                                        for (int i = 1; i < ingredientList.size(); i++){
                                            String ingredient_value;
                                            if (i < 10) {
                                                ingredient_value = ingredientList.get(i).substring(15);
                                            } else {
                                                ingredient_value = ingredientList.get(i).substring(16);
                                            }
                                                ingredientList.remove(i);
                                                ingredientList.add(i,"Ingredient #" + i + ": " + ingredient_value);
                                        }
                                    }
                                });
                                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }

                                });
                                alert.show();
                            } else {

                                name_ingredient_field.setText("");
                                Carbohydrates.setText("");
                                Proteins.setText("");
                                Fats.setText("");
                                Weight.setText("");
                                calories.setText("");
                                calories_100.setText("");
                                n_ingredients--;
                                number_ingredients_field.setText(String.valueOf(n_ingredients));
                                ingredientList.remove(change);
                                carboList.remove(change);
                                protList.remove(change);
                                fatList.remove(change);
                                weightList.remove(change);
                                spinner_ingredients.setSelection(0);
                                change = 0;
                                for (int i = 1; i < ingredientList.size(); i++){
                                    String ingredient_value;
                                    if (i < 10) {
                                        ingredient_value = ingredientList.get(i).substring(15);
                                    } else {
                                        ingredient_value = ingredientList.get(i).substring(16);
                                    }
                                    ingredientList.remove(i);
                                    ingredientList.add(i,"Ingredient #" + i + ": " + ingredient_value);
                                }
                            }
                            fieldsNotClickable();


                        } else {
                            setErrors("The number of ingredients must be greater than 0");
                        }
                    } else {
                        setErrors("You must select an ingredient to delete.");
                    }
                } else {
                    setErrors("The number of ingredients must be greater than 0");
                }
            }
        });
        spinner_ingredients.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    ((TextView) view).setTextColor(Color.GRAY);
                } else {
                    Proteins.setClickable(true);
                    Carbohydrates.setClickable(true);
                    Fats.setClickable(true);
                    Weight.setClickable(true);
                    name_ingredient_field.setClickable(true);
                    Proteins.setEnabled(true);
                    Carbohydrates.setEnabled(true);
                    Fats.setEnabled(true);
                    Weight.setEnabled(true);
                    name_ingredient_field.setEnabled(true);
                    name_watch = false;
                    if (theme.equals("light")) {
                        ((TextView) view).setTextColor(Color.BLACK);
                    } else if (theme.equals("dark")){
                        ((TextView) view).setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
                    }
                    if (Carbohydrates.getText().toString().length() > 0) {
                        carboList.remove(change);
                        carboList.add(change, Carbohydrates.getText().toString());
                    } else {
                        carboList.remove(change);
                        carboList.add(change, String.valueOf(0));
                    }
                    if (Proteins.getText().toString().length() > 0) {
                        protList.remove(change);
                        protList.add(change, Proteins.getText().toString());
                    } else {
                        protList.remove(change);
                        protList.add(change, String.valueOf(0));
                    }
                    if (Fats.getText().toString().length() > 0) {
                        fatList.remove(change);
                        fatList.add(change, Fats.getText().toString());
                    } else {
                        fatList.remove(change);
                        fatList.add(change, String.valueOf(0));
                    }
                    if (Weight.getText().toString().length() > 0) {
                        weightList.remove(change);
                        weightList.add(change, Weight.getText().toString());
                    } else {
                        weightList.remove(change);
                        weightList.add(change, String.valueOf(0));
                    }
                    if (ingredientList.get(position).length() > 14){
                        if (position < 10){
                            name_ingredient_field.setText(ingredientList.get(position).substring(15));
                        } else {
                            name_ingredient_field.setText(ingredientList.get(position).substring(16));
                        }
                        Carbohydrates.setText(carboList.get(position));
                        Proteins.setText(protList.get(position));
                        Fats.setText(fatList.get(position));
                        Weight.setText(weightList.get(position));
                    } else {
                        name_ingredient_field.setText("");
                        Carbohydrates.setText("");
                        Proteins.setText("");
                        Fats.setText("");
                        Weight.setText("");
                    }
                    calories.setText("");
                    calories_100.setText("");
                    name_watch = true;
                    change = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        caloriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < carboList.size(); i++){
                    Log.d("reee",carboList.get(i));
                }
                if (Carbohydrates.getText().toString().length() > 0 && Proteins.getText().toString().length() > 0 && Fats.getText().toString().length() > 0 && Weight.getText().toString().length() > 0){
                    float carbs = Float.parseFloat(Carbohydrates.getText().toString());
                    float prots = Float.parseFloat(Proteins.getText().toString());
                    float fats = Float.parseFloat(Fats.getText().toString());
                    float weight = Float.parseFloat(Weight.getText().toString());
                    calories.setText(f.format((carbs+prots)*4*weight/100 + fats*9*weight/100) + "kcal");
                    calories_100.setText(f.format((carbs+prots)*4 + fats*9) + "kcal");
                }
            }
        });
        chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean ingredientsdefined = true;
                int position = spinner_ingredients.getSelectedItemPosition();
                for (int i = 1; i <= n_ingredients; i++) {
                    Log.d("f", ingredientList.get(i));
                    if (ingredientList.get(i).length() < 16) {
                        ingredientsdefined = false;
                    }
                }
                if (ingredientsdefined && recipename.getText().toString().length() > 0 && name_ingredient_field.getText().toString().length() > 0 && Carbohydrates.getText().toString().length() > 0 && Proteins.getText().toString().length() > 0 && Fats.getText().toString().length() > 0 && Weight.getText().toString().length() > 0) {

                    if (carboList.get(position).equals("0") && protList.get(position).equals("0") && fatList.get(position).equals("0") && weightList.get(position).equals("0")) {
                        carboList.add(position, Carbohydrates.getText().toString());
                        protList.add(position, Proteins.getText().toString());
                        fatList.add(position, Fats.getText().toString());
                        weightList.add(position, Weight.getText().toString());
                    }

                    calculatePer100Grams();

                    ingredients = new ArrayList<>();
                    carbo = new ArrayList<>();
                    prot = new ArrayList<>();
                    fat = new ArrayList<>();
                    weights = new ArrayList<>();

                    ingredients.add(0, recipename.getText().toString().substring(0, 1).toUpperCase() +
                            recipename.getText().toString().substring(1) + " (Recipe)");
                    weights.add(0, String.valueOf(totalWeight));
                    carbo.add(0, String.valueOf(per100Carbo * totalWeight / 100));
                    prot.add(0, String.valueOf(per100Prot * totalWeight / 100));
                    fat.add(0, String.valueOf(per100Fat * totalWeight / 100));

                    for (int i = 1; i <= n_ingredients; i++) {
                        if (i < 10) {
                            ingredients.add(ingredientList.get(i).substring(15) + " (Ingredient)");
                        } else {
                            ingredients.add(ingredientList.get(i).substring(16) + " (Ingredient)");
                        }
                        weights.add(weightList.get(i));
                        carbo.add(String.valueOf(Float.parseFloat(carboList.get(i)) * Float.parseFloat(weightList.get(i)) / 100));
                        prot.add(String.valueOf(Float.parseFloat(protList.get(i)) * Float.parseFloat(weightList.get(i)) / 100));
                        fat.add(String.valueOf(Float.parseFloat(fatList.get(i)) * Float.parseFloat(weightList.get(i)) / 100));
                    }

                    // Starts the chart activity.
                    Intent i = new Intent(getApplicationContext(), PieChart.class);
                    // Passes the arrays to the chart activity.
                    i.putStringArrayListExtra("name", ingredients);
                    i.putStringArrayListExtra("weight", weights);
                    i.putStringArrayListExtra("carbo", carbo);
                    i.putStringArrayListExtra("prot", prot);
                    i.putStringArrayListExtra("fat", fat);
                    // Passes the Strings to the chart activity.
                    i.putExtra("username", user);
                    i.putExtra("recipe", "Yes");
                    i.putExtra("sender", "addrecipe");
                    startActivity(i);



                } else {
                    setErrors("Required fields are missing");
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean ingredientsdefined = true;
                int position = spinner_ingredients.getSelectedItemPosition();
                for (int i = 1; i <= n_ingredients; i++) {
                    if (ingredientList.get(i).length() < 16) {
                        ingredientsdefined = false;
                    } else {
                        ingredientsdefined = true;
                    }
                }
                if (ingredientsdefined && recipename.getText().toString().length() > 0 && name_ingredient_field.getText().toString().length() > 0 && Carbohydrates.getText().toString().length() > 0 && Proteins.getText().toString().length() > 0 && Fats.getText().toString().length() > 0 && Weight.getText().toString().length() > 0) {
                    if (carboList.get(position).equals("0") && protList.get(position).equals("0") && fatList.get(position).equals("0") && weightList.get(position).equals("0")) {
                        carboList.add(spinner_ingredients.getSelectedItemPosition(), Carbohydrates.getText().toString());
                        protList.add(spinner_ingredients.getSelectedItemPosition(), Proteins.getText().toString());
                        fatList.add(spinner_ingredients.getSelectedItemPosition(), Fats.getText().toString());
                        weightList.add(spinner_ingredients.getSelectedItemPosition(), Weight.getText().toString());
                    }

                    for (int i = 1; i <= n_ingredients; i++) {
                        if (carboList.get(i).equals("0") && protList.get(i).equals("0") && fatList.get(i).equals("0") && weightList.get(i).equals("0")) {
                            ingredientsdefined = false;
                        } else if (weightList.get(i).equals("0")) {
                            ingredientsdefined = false;
                        } else {
                            ingredientsdefined = true;
                        }
                    }
                    if (checkRepeatedName() && ingredientsdefined) {
                        calculatePer100Grams();

                        ingredients = new ArrayList<>();
                        carbo = new ArrayList<>();
                        prot = new ArrayList<>();
                        fat = new ArrayList<>();
                        weights = new ArrayList<>();

                        ingredients.add(0, recipename.getText().toString().substring(0, 1).toUpperCase() +
                                recipename.getText().toString().substring(1));
                        weights.add(0, String.valueOf(totalWeight));
                        carbo.add(0, String.valueOf(per100Carbo));
                        prot.add(0, String.valueOf(per100Prot));
                        fat.add(0, String.valueOf(per100Fat));

                        for (int i = 1; i <= n_ingredients; i++) {
                            if (i < 10) {
                                ingredients.add(ingredientList.get(i).substring(15));
                            } else {
                                ingredients.add(ingredientList.get(i).substring(16));
                            }
                            weights.add(weightList.get(i));
                            carbo.add(carboList.get(i));
                            prot.add(protList.get(i));
                            fat.add(fatList.get(i));
                            Log.d("ing", ingredients.get(0));
                            Log.d("carb", carbo.get(0));
                            Log.d("prot", prot.get(0));
                            Log.d("fat", fat.get(0));
                            Log.d("weight", weights.get(0));
                        }
                        new AddRecipeToSearch().execute();
                    } else if (!ingredientsdefined){
                        setErrors("Required fields are missing");
                    }
                } else {
                    setErrors("Required field(s) are missing");
                }
            }
        });
        resize();
        detectDarkMode();
        name_ingredient_field.addTextChangedListener(new IngredientsTextWatcher());
        new SearchIngredient().execute();
    }

    private void fieldsNotClickable(){
            Proteins.setClickable(false);
            Carbohydrates.setClickable(false);
            Fats.setClickable(false);
            Weight.setClickable(false);
            name_ingredient_field.setClickable(false);
            Proteins.setEnabled(false);
            Carbohydrates.setEnabled(false);
            Fats.setEnabled(false);
            Weight.setEnabled(false);
            name_ingredient_field.setEnabled(false);
    }

    private void setSpinner(){
        if (theme.equals("light")) {
            adapter = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_spinner_dropdown_item, ingredientList) {
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


                @Override
                public boolean isEnabled(int position) {
                    if (position == 0) {
                        return false;
                    } else {
                        return true;
                    }
                }
            };
            // Sets the adapter in the spinner.
            spinner_ingredients.setAdapter(adapter);
        } else if (theme.equals("dark")){
            adapter = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_spinner_dropdown_item, ingredientList) {
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


                @Override
                public boolean isEnabled(int position) {
                    if (position == 0) {
                        return false;
                    } else {
                        return true;
                    }
                }
            };
            // Sets the adapter in the spinner.
            spinner_ingredients.setAdapter(adapter);
        }
    }

    private boolean checkRepeatedName(){
        ArrayList<String> nameswithoutdefaulttext = new ArrayList<>();
        boolean repeated = false;
        nameswithoutdefaulttext.add(0,"null");
        for (int i = 1; i <= n_ingredients; i++){
            if (i < 10) {
                nameswithoutdefaulttext.add(ingredientList.get(i).substring(15));
            } else {
                nameswithoutdefaulttext.add(ingredientList.get(i).substring(16));
            }
        }
        for (int i = 1; i <= n_ingredients; i++){
            for (int x = i+1; x <= n_ingredients; x++){
                if (nameswithoutdefaulttext.get(i).equals(nameswithoutdefaulttext.get(x))){
                    setErrors("Ingredients must have different names");
                    return false;
                }  else {setErrors("Correct names");}
            }

        }
        return true;
    }
    // Class that performs the connection to the database to add the recipe to the food database.
    // The class AsyncTask is the superclass that enables to perform tasks in the background.
    class AddRecipeToSearch extends AsyncTask<String, String, String> {
        boolean check = false;
        //        String recipesnameunit;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // Method responsible of the connection to the database.
        @Override
        protected String doInBackground(String... strings) {
            // Local variable that contains the path of the PHP file used for connection
            String url_insert_food_recipe =
                    "http://nam33.student.eda.kent.ac.uk/insert_food_recipe.php";
            // Creating an ArrayList and adding the variables that will be sent.
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("name", ingredients.get(0) + " ( Recipe/100g)"));
            params.add(new BasicNameValuePair("carbohydrate", carbo.get(0)));
            params.add(new BasicNameValuePair("protein", prot.get(0)));
            params.add(new BasicNameValuePair("fat", fat.get(0)));
            params.add(new BasicNameValuePair("idUser", user));

            // Calls the method that makes the connection passing the URL variables, the Method and
            // the ArrayList as parameters.
            JSONObject json = jsonParser.makeHttpRequest(url_insert_food_recipe,
                    "POST", params);

            // Prints in the log the result of the operation.
            Log.d("Create Response", json.toString());

            try {
                // Checks if the operation was performed successfully.
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    check = true;
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
                // Calls the class that adds the ingredients to the recipe database.
                new AddRecipe().execute();
            } else {
                setErrors("Recipe name already in use.");
            }
        }
    }

    // Class that performs the connection to the database to add the ingredients to the recipes
    // database.
    // The class AsyncTask is the superclass that enables to perform tasks in the background.
    class AddRecipe extends AsyncTask<String, String, String>{
        boolean check = false;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // Method responsible of the connection to the database.
        @Override
        protected String doInBackground(String... strings) {
            // Local variable that contains the path of the PHP file used for connection
            String url_insert_recipe = "http://nam33.student.eda.kent.ac.uk/recipes.php";
            // Creating an ArrayList and adding the ingredient variables that will be sent.
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            for (int i = 1; i <= n_ingredients; i++) {
                params.add(new BasicNameValuePair("ingredient", ingredients.get(i)));
                params.add(new BasicNameValuePair("weight", weights.get(i)));
                params.add(new BasicNameValuePair("carbohydrate", carbo.get(i)));
                params.add(new BasicNameValuePair("protein", prot.get(i)));
                params.add(new BasicNameValuePair("fat", fat.get(i)));
                params.add(new BasicNameValuePair("recipeName", ingredients.get(0)+" ("+user+")"));
                params.add(new BasicNameValuePair("idUser", user));

                // Calls the method that makes the connection passing the URL variables,
                // the Method and the ArrayList as parameters.
                JSONObject jsons = jsonParser.makeHttpRequest(url_insert_recipe,
                        "POST", params);
                try {
                    // Checks if the operation was performed successfully.
                    int success = jsons.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        Log.d("Create Response", jsons.toString());
                        check = true;
                        // Once one ingredient is added, remove all its data from the ArrayList to
                        // continue with the enxt ingredient.
                        for (int v = 0; v < 7; v++) {
                            params.remove(0);
                        }
                    } else {
                        // Displays an error message.
                        setErrors("Required field(s) are missing.");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // Starts the Main Activity and finishes the actual one.
            if (check){
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        }
    }

    // Class that performs the connection to the database to get the food item names from the food
    // database.
    // The class AsyncTask is the superclass that enables to perform tasks in the background.
    class SearchIngredient extends AsyncTask<String, String, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // Method responsible of the connection to the database.
        @Override
        protected String doInBackground(String... strings) {
            // Local variable that contains the path of the PHP file used for connection
            String url_search_all_ingredients =
                    "http://nam33.student.eda.kent.ac.uk/search_all_ingredients.php";
            // Creating an ArrayList and adding the variables that will be sent.
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("idUser", user));

            // Calls the method that makes the connection passing the URL variables, the Method and
            // the ArrayList as parameters.
            JSONObject json = jsonParser.makeHttpRequest(url_search_all_ingredients,
                    "GET", params);

            try {
                // Checks if the operation was performed successfully.
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // Gets all the food item names and stores them in an array.
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
            // Sets the adapter in each ingredient field name.
                name_ingredient_field.setThreshold(1);
                name_ingredient_field.setAdapter(adapter2);
        }
    }



    // Class that performs the connection to the database to retrieve the values of the food.
    // The class AsyncTask is the superclass that enables to perform tasks in the background.
    class RetrieveDataIngredients extends AsyncTask<String, String, String>{
        boolean check = false;
        String ingredientName, ingredientCarbo, ingredientProt, ingredientFat;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ingredientName = name_ingredient_field.getText().toString();

        }

        @Override
        // Method responsible of the connection to the database.
        protected String doInBackground(String... strings) {
            // Local variable that contains the path of the PHP file used for connection
            String url_search_ingredient =
                    "http://nam33.student.eda.kent.ac.uk/search_ingredient.php";
            // Creating an ArrayList and adding the variables that will be sent.
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name", ingredientName));
            params.add(new BasicNameValuePair("idUser", user));
            // Calls the method that makes the connection passing the URL variables, the Method and
            // the ArrayList as parameters.
            JSONObject json = jsonParser.makeHttpRequest(url_search_ingredient,
                    "GET", params);

            try {
                // Checks if the operation was performed successfully.
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // Gets the nutritional values from the database.
                    JSONArray food = json.getJSONArray(TAG_FOOD);
                    JSONObject c = food.getJSONObject(0);
                    ingredientCarbo = c.getString(TAG_CARBOHYDRATE);
                    ingredientProt = c.getString(TAG_PROTEIN);
                    ingredientFat = c.getString(TAG_FAT);
                } else {check = true;}

            } catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // Checks if the process was performed successfully.
            if (!check) {
                // Displays the values.
                Carbohydrates.setText(ingredientCarbo);
                Proteins.setText(ingredientProt);
                Fats.setText(ingredientFat);
                Weight.setText("100");
            }
        }
    }


    private class IngredientsTextWatcher implements TextWatcher {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (name_watch) {
                new RetrieveDataIngredients().execute();
                int position = spinner_ingredients.getSelectedItemPosition();
                ingredientList.remove(position);
                ingredientList.add(position, "Ingredient #" + (spinner_ingredients.getSelectedItemPosition()) + ": " + s.toString());

            }
        }
    }
    private void calculatePer100Grams(){
        totalFat = 0;
        totalProt = 0;
        totalCarbo = 0;
        totalWeight = 0;
        per100Carbo = 0;
        per100Fat = 0;
        per100Prot = 0;

        for (int x = 1; x <= n_ingredients; x++) {
            totalWeight += Integer.parseInt(weightList.get(x));
            totalCarbo += Float.parseFloat(carboList.get(x)) * Integer.parseInt(weightList.get(x));
            totalProt += Float.parseFloat(protList.get(x)) * Integer.parseInt(weightList.get(x));
            totalFat += Float.parseFloat(fatList.get(x)) * Integer.parseInt(weightList.get(x));
        }
            per100Carbo = totalCarbo / totalWeight;
            per100Prot = totalProt / totalWeight;
            per100Fat = totalFat / totalWeight;
    }

    private void setErrors(String message){
        errors.setText(message);
        errors.setVisibility(View.VISIBLE);
    }
    private void removeErrors(){
        if (errors.getVisibility() == View.VISIBLE){
            errors.setVisibility(View.GONE);
        }
    }


    private void resize(){
        Proteins.setWidth(width*3576/10000);
        Fats.setWidth(width*3576/10000);
        Carbohydrates.setWidth(width*3576/10000);
        Weight.setWidth(width*3576/10000);
        calories.setWidth(width*3576/10000);
        calories_100.setWidth(width*3576/10000);


        recipe_header.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density*33/411));
        recipe_item.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density*19/411));
        number_ingredients.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density*19/411));

        recipename.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density*16/411));
        number_ingredients_field.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density*16/411));
        name_ingredient.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/ Resources.getSystem().getDisplayMetrics().density)*19/411);
        number_ingredients.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*19/411);
        textFat.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*19/411);
        textProt.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*19/411);
        textCarb.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*19/411);
        textWeight.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*19/411);
        ingredient_item.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*19/411);
        total_calories.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*19/411);
        total_calories_100.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*19/411);
        caloriesButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*12/411);
        chart.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*12/411);
        add_ingredient.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*12/411);
        delete_ingredient.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*12/411);
        number_ingredientButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*12/411);
        save.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*12/411);

        save.setPadding(width*73/1000,width*121/10000,width*73/1000,width*121/10000);
        caloriesButton.setPadding(width*73/1000,width*121/10000,width*73/1000,width*121/10000);
        chart.setPadding(width*73/1000,width*121/10000,width*73/1000,width*121/10000);
        number_ingredientButton.setPadding(width*73/1000,width*121/10000,width*73/1000,width*121/10000);
        add_ingredient.setPadding(width*73/1000,width*121/10000,width*73/1000,width*121/10000);
        delete_ingredient.setPadding(width*73/1000,width*121/10000,width*73/1000,width*121/10000);


        RelativeLayout.LayoutParams p1 = (RelativeLayout.LayoutParams) caloriesButton.getLayoutParams();
        p1.setMargins(width*486/10000,height*5689/100000,width*486/10000,0);
        RelativeLayout.LayoutParams c3 = (RelativeLayout.LayoutParams) chart.getLayoutParams();
        c3.setMargins(width*486/10000,height*5689/100000,width*486/10000,0);



        RelativeLayout.LayoutParams p2 = (RelativeLayout.LayoutParams) textProt.getLayoutParams();
        p2.setMargins(0,height*4392/100000,0,0);
        RelativeLayout.LayoutParams f2 = (RelativeLayout.LayoutParams) textFat.getLayoutParams();
        f2.setMargins(0,height*4392/100000,width*1459/10000,0);


        RelativeLayout.LayoutParams c1 = (RelativeLayout.LayoutParams) total_calories.getLayoutParams();
        c1.setMargins( 0,height*4267/100000,0,0);
        RelativeLayout.LayoutParams c2 = (RelativeLayout.LayoutParams) total_calories_100.getLayoutParams();
        c2.setMargins( 0,height*4267/100000,width*7299/100000,0);
        LinearLayout.LayoutParams i1 = (LinearLayout.LayoutParams) recipe_item.getLayoutParams();
        i1.setMargins(0,height*2844/100000,0,0);
        RelativeLayout.LayoutParams c4 = (RelativeLayout.LayoutParams) textCarb.getLayoutParams();
        c4.setMargins(0,height*2133/100000,0,0);
        RelativeLayout.LayoutParams c5 = (RelativeLayout.LayoutParams) textWeight.getLayoutParams();
        c5.setMargins(0,height*2133/100000,width*2116/10000,0);

    }
    private void detectDarkMode(){
        if (theme.equals("light")){
            background.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            recipe_header.setTextColor(getResources().getColor(R.color.colorBlack));
            recipe_item.setTextColor(getResources().getColor(R.color.colorBlack));
            recipename.setTextColor(getResources().getColor(R.color.colorBlack));
            recipename.setHintTextColor(getResources().getColor(R.color.colorGrey));
            recipename.setBackgroundResource(R.drawable.search_field_rounded);
            number_ingredients.setTextColor(getResources().getColor(R.color.colorBlack));
            number_ingredients_field.setBackgroundResource(R.drawable.search_field_rounded);
            number_ingredients_field.setTextColor(getResources().getColor(R.color.colorBlack));
            number_ingredients_field.setHintTextColor(getResources().getColor(R.color.colorGrey));
            number_ingredientButton.setBackgroundResource(R.drawable.go_search_rounded);
            number_ingredientButton.setTextColor(getResources().getColor(R.color.colorWhite));
            line.setBackgroundColor(getResources().getColor(R.color.colorBlack));
            line2.setBackgroundColor(getResources().getColor(R.color.colorBlack));
            line3.setBackgroundColor(getResources().getColor(R.color.colorBlack));
            add_ingredient.setTextColor(getResources().getColor(R.color.colorWhite));
            add_ingredient.setBackgroundResource(R.drawable.show_chart_rounded);
            delete_ingredient.setTextColor(getResources().getColor(R.color.colorWhite));
            delete_ingredient.setBackgroundResource(R.drawable.clear_button_rounded);
            ingredient_item.setTextColor(getResources().getColor(R.color.colorBlack));
            spinner_ingredients.setPopupBackgroundResource(R.color.colorWhite);
            name_ingredient.setTextColor(getResources().getColor(R.color.colorBlack));
            name_ingredient_field.setBackgroundResource(R.drawable.search_field_rounded);
            name_ingredient_field.setTextColor(getResources().getColor(R.color.colorBlack));
            name_ingredient_field.setHintTextColor(getResources().getColor(R.color.colorGrey));
            textProt.setTextColor(getResources().getColor(R.color.colorBlack));
            Proteins.setTextColor(getResources().getColor(R.color.colorBlack));
            Proteins.setBackgroundResource(R.drawable.search_field_rounded);
            textFat.setTextColor(getResources().getColor(R.color.colorBlack));
            Fats.setTextColor(getResources().getColor(R.color.colorBlack));
            Fats.setBackgroundResource(R.drawable.search_field_rounded);
            textCarb.setTextColor(getResources().getColor(R.color.colorBlack));
            Carbohydrates.setTextColor(getResources().getColor(R.color.colorBlack));
            Carbohydrates.setBackgroundResource(R.drawable.search_field_rounded);
            textWeight.setTextColor(getResources().getColor(R.color.colorBlack));
            Weight.setTextColor(getResources().getColor(R.color.colorBlack));
            Weight.setBackgroundResource(R.drawable.search_field_rounded);
            total_calories.setTextColor(getResources().getColor(R.color.colorBlack));
            total_calories_100.setTextColor(getResources().getColor(R.color.colorBlack));
            calories.setTextColor(getResources().getColor(R.color.colorBlack));
            calories.setBackgroundResource(R.drawable.search_field_rounded);
            calories_100.setTextColor(getResources().getColor(R.color.colorBlack));
            calories_100.setBackgroundResource(R.drawable.search_field_rounded);
            caloriesButton.setTextColor(getResources().getColor(R.color.colorWhite));
            caloriesButton.setBackgroundResource(R.drawable.edit_item_rounded);
            chart.setTextColor(getResources().getColor(R.color.colorWhite));
            chart.setBackgroundResource(R.drawable.show_chart_rounded);
            save.setBackgroundResource(R.drawable.save_entry_rounded);
            save.setTextColor(getResources().getColor(R.color.colorWhite));
            name_ingredient_field.setDropDownBackgroundResource(R.color.colorWhite);
        } else if (theme.equals("dark")){
            background.setBackgroundColor(getResources().getColor(R.color.darkThemeBackground));
            recipe_header.setTextColor(getResources().getColor(R.color.colorWhite));
            recipe_item.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            recipename.setTextColor(getResources().getColor(R.color.colorWhite));
            recipename.setHintTextColor(getResources().getColor(R.color.colorDarkThemeHintGrey));
            recipename.setBackgroundResource(R.drawable.search_field_rounded_dark);
            number_ingredients.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            number_ingredients_field.setBackgroundResource(R.drawable.search_field_rounded_dark);
            number_ingredients_field.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            number_ingredients_field.setHintTextColor(getResources().getColor(R.color.colorDarkThemeHintGrey));
            number_ingredientButton.setBackgroundResource(R.drawable.go_search_rounded_dark);
            number_ingredientButton.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            line.setBackgroundColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            line2.setBackgroundColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            line3.setBackgroundColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            add_ingredient.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            add_ingredient.setBackgroundResource(R.drawable.show_chart_rounded_dark);
            delete_ingredient.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            delete_ingredient.setBackgroundResource(R.drawable.clear_button_rounded_dark);
            ingredient_item.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            spinner_ingredients.setPopupBackgroundResource(R.color.darkThemeBackground);
            name_ingredient.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            name_ingredient_field.setBackgroundResource(R.drawable.search_field_rounded_dark);
            name_ingredient_field.setTextColor(getResources().getColor(R.color.colorWhite));
            name_ingredient_field.setHintTextColor(getResources().getColor(R.color.colorDarkThemeHintGrey));
            textProt.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            Proteins.setTextColor(getResources().getColor(R.color.colorWhite));
            Proteins.setBackgroundResource(R.drawable.search_field_rounded_dark);
            textFat.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            Fats.setTextColor(getResources().getColor(R.color.colorWhite));
            Fats.setBackgroundResource(R.drawable.search_field_rounded_dark);
            textCarb.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            Carbohydrates.setTextColor(getResources().getColor(R.color.colorWhite));
            Carbohydrates.setBackgroundResource(R.drawable.search_field_rounded_dark);
            textWeight.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            Weight.setTextColor(getResources().getColor(R.color.colorWhite));
            Weight.setBackgroundResource(R.drawable.search_field_rounded_dark);
            total_calories.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            total_calories_100.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            calories.setTextColor(getResources().getColor(R.color.colorWhite));
            calories.setBackgroundResource(R.drawable.search_field_rounded_dark);
            calories_100.setTextColor(getResources().getColor(R.color.colorWhite));
            calories_100.setBackgroundResource(R.drawable.search_field_rounded_dark);
            caloriesButton.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            caloriesButton.setBackgroundResource(R.drawable.edit_item_rounded_dark);
            chart.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            chart.setBackgroundResource(R.drawable.show_chart_rounded_dark);
            save.setBackgroundResource(R.drawable.save_entry_rounded_dark);
            save.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            name_ingredient_field.setDropDownBackgroundResource(R.color.darkThemeBackground);

        }
    }
}