package com.example.senseplate;

// Importing the libraries used.
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class EditFoodActivity extends Activity {
    // Declaring the global variables
    JSONParser jsonParser = new JSONParser();
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_FOOD = "food";
    private static final String TAG_NAME = "name";
    private static final String TAG_INGREDIENT = "ingredient";
    private static final String TAG_CARBOHYDRATE = "carbohydrate";
    private static final String TAG_PROTEIN = "protein";
    private static final String TAG_FAT = "fat";
    private static final String TAG_WEIGHT = "weight";
    private static final String TAG = "EditFoodActivity";
    private SharedPreferences sharedPref;
    ArrayList<String> items;

    boolean check = false;
    boolean check2 = false;
    boolean recipe = false;
    boolean check3 = false;
    boolean added = false;
    String name, carbohydrate, protein, fat, user, iname, icarbo, iprot, ifat, iweight, theme;
    EditText Carbohydrates, Proteins, Fats, Weight, name_ingredient_field;
    TextView errors, username, searchView, textWeight, textCarb, textProt, textFat, calories, add_item, edit,total_calories, ingredient_item, total_calories_100, calories_100, textview2,name_ingredient, line;
    Button editButton, srchButton, chart, deleteButton, caloriesButton, add_ingredient, delete_ingredient, save_ingredient, back, addItem, createRecipe;
    AutoCompleteTextView inputName;
    Spinner spinner_ingredients;
    LinearLayout mainli;
    ScrollView sc;
    int width, height,change = 0, TotalWeight;
    float TotalCarbo, TotalProt, TotalFat;
    DecimalFormat f = new DecimalFormat("####.00");
    ArrayList<String> nameList, carboList, protList, fatList, weightList, deleteIng, addIng, nameA, protA, carboA, fatA,weightA;


    // Method called when the Activity is created.
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_food);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        // Initializing the widgets.
        sc = (ScrollView) findViewById(R.id.sc);
        mainli = (LinearLayout) findViewById(R.id.mainli);
        edit = (TextView) findViewById(R.id.main);
        inputName = (AutoCompleteTextView) findViewById(R.id.edit_item);
        textview2 = (TextView) findViewById(R.id.textView2);
        line = (TextView) findViewById(R.id.line);
        add_item = (TextView) findViewById(R.id.add_item);
        ingredient_item = (TextView) findViewById(R.id.ingredient_item);
        name_ingredient = (TextView) findViewById(R.id.name_ingredient);
        name_ingredient_field = (EditText) findViewById(R.id.name_ingredient_field);
        spinner_ingredients = (Spinner) findViewById(R.id.spinner_ingredients);
        Carbohydrates = (EditText) findViewById(R.id.carbohydrate_add_field);
        Weight = (EditText) findViewById(R.id.weight_add_field);
        Proteins = (EditText) findViewById(R.id.protein_add_field);
        Fats = (EditText) findViewById(R.id.fat_add_field);
        errors = (TextView) findViewById(R.id.error);
//        username = (TextView) findViewById(R.id.username);
        searchView = (TextView) findViewById(R.id.add_item_nameT);
        editButton = (Button) findViewById(R.id.save);
        srchButton = (Button) findViewById(R.id.search);
        chart = (Button) findViewById(R.id.chart);
        deleteButton = (Button) findViewById(R.id.clear);
        caloriesButton = (Button) findViewById(R.id.show_calories);
        add_ingredient = (Button) findViewById(R.id.add_ingredient);
        delete_ingredient = (Button) findViewById(R.id.delete_ingredient);
        save_ingredient = (Button) findViewById(R.id.save_ingredient);
        back = (Button) findViewById(R.id.back);
        addItem = (Button) findViewById(R.id.addItem);
        createRecipe = (Button) findViewById(R.id.createRecipe);
        textCarb = (TextView) findViewById(R.id.carbohydrate_add);
        textWeight = (TextView) findViewById(R.id.weight_add);
        textProt = (TextView) findViewById(R.id.protein_add);
        textFat = (TextView) findViewById(R.id.fat_add);
        calories = (TextView) findViewById(R.id.calories_field);
        total_calories = (TextView) findViewById(R.id.total_calories);
        calories_100 = (TextView) findViewById(R.id.calories_field_100);
        total_calories_100 = (TextView) findViewById(R.id.total_calories_100);
        resize();
        Log.d("T",String.valueOf(height/ Resources.getSystem().getDisplayMetrics().density));
        // Gets the username of the user logged in. In case none user is logged, set the value to "".
        sharedPref = getSharedPreferences("com.example.senseplate", Context.MODE_PRIVATE);
        user = sharedPref.getString(getString(R.string.username),"");

        // Sets the event for the search button
        srchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace the EditText with a Textview to avoid that the user changes the name
                // searched once the connection is established
                name = inputName.getText().toString();
                searchView.setText(name);
                inputName.setVisibility(View.GONE);
                searchView.setVisibility(View.VISIBLE);
                errors.setVisibility(View.GONE);
                addItem.setVisibility(View.GONE);
                createRecipe.setVisibility(View.GONE);
                // Calls the class that performs the first connection to check if the food name
                // entered exists in the database and if it is the case, it returns its nutritional
                // values.
                new SearchEditFood().execute();
            }
        });
        spinner_ingredients.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0){
                    ((TextView) view).setTextColor(Color.GRAY);
                } else {
                    if (theme.equals("light")) {
                        ((TextView) view).setTextColor(Color.BLACK);
                    } else if (theme.equals("dark")){
                        ((TextView) view).setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
                    }
                    if (errors.getVisibility()==View.VISIBLE){
                        errors.setVisibility(View.GONE);
                    }
                    if (change != 0){
                        carboList.remove(change-1);
                        carboList.add(change-1, Carbohydrates.getText().toString());
                        protList.remove(change-1);
                        protList.add(change-1, Proteins.getText().toString());
                        fatList.remove(change-1);
                        fatList.add(change-1, Fats.getText().toString());
                        weightList.remove(change-1);
                        weightList.add(change-1, Weight.getText().toString());
                    }
                    Carbohydrates.setText(carboList.get(position - 1));
                    Proteins.setText(protList.get(position - 1));
                    Fats.setText(fatList.get(position - 1));
                    Weight.setText(weightList.get(position - 1));
                    calories.setText("");
                    calories_100.setText("");
                    change = position;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
        // Sets the event for the edit button
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Calls the class that performs another connection to save the changes made on the
                // database.
                if (!recipe){
                    name = inputName.getText().toString();
                    carbohydrate = Carbohydrates.getText().toString();
                    protein = Proteins.getText().toString();
                    fat = Fats.getText().toString();
                    new EditFood().execute();
                } else {
                    if (spinner_ingredients.getSelectedItemPosition() == 0){
                        errors.setText("Please choose an ingredient before saving.");
                        errors.setVisibility(View.VISIBLE);
                    } else {
                        carboList.remove(spinner_ingredients.getSelectedItemPosition() - 1);
                        protList.remove(spinner_ingredients.getSelectedItemPosition() - 1);
                        fatList.remove(spinner_ingredients.getSelectedItemPosition() - 1);
                        weightList.remove(spinner_ingredients.getSelectedItemPosition() - 1);

                        carboList.add(spinner_ingredients.getSelectedItemPosition() - 1, Carbohydrates.getText().toString());
                        protList.add(spinner_ingredients.getSelectedItemPosition() - 1, Proteins.getText().toString());
                        fatList.add(spinner_ingredients.getSelectedItemPosition() - 1, Fats.getText().toString());
                        weightList.add(spinner_ingredients.getSelectedItemPosition() - 1, Weight.getText().toString());

                        TotalFat = 0;
                        TotalProt = 0;
                        TotalCarbo = 0;
                        TotalWeight = 0;
                        for (int i = 0; i < carboList.size(); i++) {
                            TotalWeight += Integer.parseInt(weightList.get(i));
                            Log.d("Res",carboList.get(i));
                            TotalCarbo += Float.parseFloat(carboList.get(i)) * Integer.parseInt(weightList.get(i));
                            TotalProt += Float.parseFloat(protList.get(i)) * Integer.parseInt(weightList.get(i));
                            TotalFat += Float.parseFloat(fatList.get(i)) * Integer.parseInt(weightList.get(i));
                        }
                        name = inputName.getText().toString();
                        carbohydrate = String.valueOf(TotalCarbo / TotalWeight);
                        protein = String.valueOf(TotalProt / TotalWeight);
                        fat = String.valueOf(TotalFat / TotalWeight);
                        new EditFood().execute();
                        new DeleteIngredient().execute();
                        new AddRecipe().execute();
                    }
                }
            }
        });
        // Sets the event for the add button in case the food name entered does not exist on the
        // database.
        chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recipe) {
                    boolean nameInUse = false;
                    if (inputName.getText().toString().length() > 0 && Carbohydrates.getText().toString().length() > 0 && Proteins.getText().toString().length() > 0 && Fats.getText().toString().length() > 0 && Weight.getText().toString().length()>0) {
                        if (added){
                            // Saves the ingredient
                            if (name_ingredient_field.length()>0 && Carbohydrates.length()>0 && Proteins.length()>0 && Fats.length()>0 && Weight.length()>0) {
                                try{
                                    Integer.parseInt(Carbohydrates.getText().toString());
                                    Carbohydrates.setText(Carbohydrates.getText() + ".00");
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                try{
                                    Integer.parseInt(Proteins.getText().toString());
                                    Proteins.setText(Proteins.getText() + ".00");
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                try{
                                    Integer.parseInt(Fats.getText().toString());
                                    Fats.setText(Fats.getText() + ".00");
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                                for (int i = 0; i < nameList.size(); i++){
                                    if (nameList.get(i).equals(name_ingredient_field.getText().toString()+" (Ingredient)")){
                                        nameInUse = true;
                                    }
                                }
                                if (nameInUse){
                                    errors.setText("Ingredient name entered already in use.");
                                    errors.setVisibility(View.VISIBLE);
                                } else {
                                    if (errors.getVisibility() == View.VISIBLE) {
                                        errors.setVisibility(View.GONE);
                                    }
                                    nameList.add(nameList.size(), name_ingredient_field.getText().toString() + " (Ingredient)");
                                    carboList.add(carboList.size(), Carbohydrates.getText().toString());
                                    protList.add(protList.size(), Proteins.getText().toString());
                                    fatList.add(fatList.size(), Fats.getText().toString());
                                    weightList.add(weightList.size(), Weight.getText().toString());
                                    addIng.add(name_ingredient_field.getText().toString());
                                    addIng.add(Weight.getText().toString());
                                    addIng.add(Carbohydrates.getText().toString());
                                    addIng.add(Proteins.getText().toString());
                                    addIng.add(Fats.getText().toString());

                                    name_ingredient_field.setVisibility(View.GONE);
                                    name_ingredient.setVisibility(View.GONE);
                                    editButton.setVisibility(View.VISIBLE);
                                    deleteButton.setVisibility(View.VISIBLE);
                                    save_ingredient.setVisibility(View.GONE);
                                    back.setVisibility(View.GONE);
                                    spinner_ingredients.setSelection(nameList.size() - 1);
                                    change = nameList.size() - 1;
                                }
                            }
                        } else {
                            carboList.remove(spinner_ingredients.getSelectedItemPosition() - 1);
                            protList.remove(spinner_ingredients.getSelectedItemPosition() - 1);
                            fatList.remove(spinner_ingredients.getSelectedItemPosition() - 1);
                            weightList.remove(spinner_ingredients.getSelectedItemPosition() - 1);

                            carboList.add(spinner_ingredients.getSelectedItemPosition() - 1, Carbohydrates.getText().toString());
                            protList.add(spinner_ingredients.getSelectedItemPosition() - 1, Proteins.getText().toString());
                            fatList.add(spinner_ingredients.getSelectedItemPosition() - 1, Fats.getText().toString());
                            weightList.add(spinner_ingredients.getSelectedItemPosition() - 1, Weight.getText().toString());
                        }
                        if (!nameInUse) {
                            nameA = new ArrayList<String>();
                            weightA = new ArrayList<String>();
                            carboA = new ArrayList<String>();
                            protA = new ArrayList<String>();
                            fatA = new ArrayList<String>();
                            TotalFat = 0;
                            TotalProt = 0;
                            TotalCarbo = 0;
                            TotalWeight = 0;
                            for (int i = 0; i < carboList.size(); i++) {
                                nameA.add(nameList.get(i + 1));
                                carboA.add(String.valueOf(Float.parseFloat(carboList.get(i)) * Float.parseFloat(weightList.get(i)) / 100));
                                protA.add(String.valueOf(Float.parseFloat(protList.get(i)) * Float.parseFloat(weightList.get(i)) / 100));
                                fatA.add(String.valueOf(Float.parseFloat(fatList.get(i)) * Float.parseFloat(weightList.get(i)) / 100));
                                weightA.add(weightList.get(i));
                                TotalWeight += Integer.parseInt(weightList.get(i));
                                TotalCarbo += Float.parseFloat(carboList.get(i)) * Integer.parseInt(weightList.get(i));
                                TotalProt += Float.parseFloat(protList.get(i)) * Integer.parseInt(weightList.get(i));
                                TotalFat += Float.parseFloat(fatList.get(i)) * Integer.parseInt(weightList.get(i));
                            }
                            carboA.add(0, String.valueOf(TotalCarbo / 100));
                            protA.add(0, String.valueOf(TotalProt / 100));
                            fatA.add(0, String.valueOf(TotalFat / 100));
                            weightA.add(0, String.valueOf(TotalWeight));
                            nameA.add(0, inputName.getText().toString());
                            // Starts the chart activity.
                            Intent i = new Intent(getApplicationContext(), PieChart.class);
                            // Passes the arrays to the chart activity.
                            i.putStringArrayListExtra("name", nameA);
                            i.putStringArrayListExtra("weight", weightA);
                            i.putStringArrayListExtra("carbo", carboA);
                            i.putStringArrayListExtra("prot", protA);
                            i.putStringArrayListExtra("fat", fatA);
                            // Passes the Strings to the chart activity.
                            i.putExtra("username", user);
                            i.putExtra("recipe", "Yes");
                            i.putExtra("sender", "editrecipe");
                            startActivity(i);
                        }

                    }
                } else {
                    if (inputName.getText().toString().length() > 0 && Carbohydrates.getText().toString().length() > 0 && Proteins.getText().toString().length() > 0 && Fats.getText().toString().length() > 0) {
                        // Starts the PieChart activity and passing several values to it.
                        Intent i = new Intent(getApplicationContext(), PieChart.class);
                        i.putExtra("name", inputName.getText().toString());
                        i.putExtra("weight", "100");
                        i.putExtra("carbo", Carbohydrates.getText().toString());
                        i.putExtra("prot", Proteins.getText().toString());
                        i.putExtra("fat", Fats.getText().toString());
                        i.putExtra("username", user);
                        i.putExtra("sender", "edit");
                        i.putExtra("recipe", "No");
                        startActivity(i);
                    }
                }
            }
        });
        // Sets the event for the delete button.
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Calls the class that deletes the food item from the database
                AlertDialog.Builder alert = new AlertDialog.Builder(EditFoodActivity.this);
                alert.setTitle("Delete Recipe");
                if (recipe) {
                    alert.setMessage("Are you sure you want to delete the Recipe?");
                } else {
                    alert.setMessage("Are you sure you want to delete "+inputName.getText().toString()+ " item?");
                }
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DeleteFood().execute();
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }

                });
                alert.show();
            }
        });
        // Sets the event for the show calories button.
        caloriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Calls the method that calculates and displays the calories.
                if (Carbohydrates.getText().toString().length() > 0 && Proteins.getText().toString().length() > 0 && Fats.getText().toString().length() > 0){
                    calculateCalories();
                }
            }
        });

        delete_ingredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (change != 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditFoodActivity.this);
                    builder.setTitle("Delete Ingredient");
                    builder.setMessage("Are you sure that you want to delete "+nameList.get(spinner_ingredients.getSelectedItemPosition())+"?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (carboList.size() == 1){
                                AlertDialog.Builder builder2 = new AlertDialog.Builder(EditFoodActivity.this);
                                builder2.setTitle("Delete last Ingredient");
                                builder2.setMessage("The Recipe only has 1 ingredient, if you delete the last ingredient, the recipe will also be deleted. Are you sure you want to delete the recipe?");
                                builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new DeleteFood().execute();
                                    }
                                });
                                builder2.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }

                                });
                                builder2.show();
                            } else {
                                deleteIng.add(nameList.get(change));
                                nameList.remove(change);
                                carboList.remove(change - 1);
                                protList.remove(change - 1);
                                fatList.remove(change - 1);
                                weightList.remove(change - 1);
                                spinner_ingredients.setSelection(0);
                                change = 0;
                                Carbohydrates.setText("");
                                Proteins.setText("");
                                Fats.setText("");
                                Weight.setText("");
                                calories.setText("");
                                calories_100.setText("");
                                Toast.makeText(EditFoodActivity.this, "Ingredient deleted...", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }

                    });
                    builder.show();
                } else {
                    errors.setText("Please choose the ingredient to be deleted.");
                    errors.setVisibility(View.VISIBLE);
                }
            }
        });
        add_ingredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name_ingredient.setVisibility(View.VISIBLE);
                name_ingredient_field.setVisibility(View.VISIBLE);
                editButton.setVisibility(View.GONE);
                deleteButton.setVisibility(View.GONE);
                save_ingredient.setVisibility(View.VISIBLE);
                back.setVisibility(View.VISIBLE);
                Carbohydrates.setText("");
                Proteins.setText("");
                Fats.setText("");
                Weight.setText("");
                calories.setText("");
                calories_100.setText("");
                added = true;
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name_ingredient.setVisibility(View.GONE);
                name_ingredient_field.setVisibility(View.GONE);
                editButton.setVisibility(View.VISIBLE);
                deleteButton.setVisibility(View.VISIBLE);
                save_ingredient.setVisibility(View.GONE);
                back.setVisibility(View.GONE);
            }
        });
        save_ingredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name_ingredient_field.length()>0 && Carbohydrates.length()>0 && Proteins.length()>0 && Fats.length()>0 && Weight.length()>0) {
                    try{
                        Integer.parseInt(Carbohydrates.getText().toString());
                        Carbohydrates.setText(Carbohydrates.getText() + ".00");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    try{
                        Integer.parseInt(Proteins.getText().toString());
                        Proteins.setText(Proteins.getText() + ".00");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    try{
                        Integer.parseInt(Fats.getText().toString());
                        Fats.setText(Fats.getText() + ".00");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    boolean nameInUse = false;
                    for (int i = 0; i < nameList.size(); i++){
                        if (nameList.get(i).equals(name_ingredient_field.getText().toString()+" (Ingredient)")){
                            nameInUse = true;
                        }
                    }
                    if (nameInUse){
                        errors.setText("Ingredient name entered already in use.");
                        errors.setVisibility(View.VISIBLE);
                    } else {
                        if (errors.getVisibility() == View.VISIBLE){
                            errors.setVisibility(View.GONE);
                        }
                        nameList.add(nameList.size(), name_ingredient_field.getText().toString() + " (Ingredient)");
                        carboList.add(carboList.size(), Carbohydrates.getText().toString());
                        protList.add(protList.size(), Proteins.getText().toString());
                        fatList.add(fatList.size(), Fats.getText().toString());
                        weightList.add(weightList.size(), Weight.getText().toString());
                        addIng.add(name_ingredient_field.getText().toString());
                        addIng.add(Weight.getText().toString());
                        addIng.add(Carbohydrates.getText().toString());
                        addIng.add(Proteins.getText().toString());
                        addIng.add(Fats.getText().toString());

                        name_ingredient_field.setVisibility(View.GONE);
                        name_ingredient.setVisibility(View.GONE);
                        editButton.setVisibility(View.VISIBLE);
                        deleteButton.setVisibility(View.VISIBLE);
                        save_ingredient.setVisibility(View.GONE);
                        back.setVisibility(View.GONE);
                        spinner_ingredients.setSelection(nameList.size() - 1);
                        change = nameList.size() - 1;
                        added = false;
                    }
                }

            }
        });
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (getApplicationContext(), AddFoodActivity.class);
                startActivity(i);
                finish();
            }
        });
        createRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (getApplicationContext(), RecipeActivity.class);
                startActivity(i);
                finish();
            }
        });
        // Calls the class that connects to the database retrieving the names of all the food items.
        detectDarkMode();
        new CompleteText().execute();
        deleteIng = new ArrayList<String>();
        addIng = new ArrayList<String>();
    }
    // Connects to the database to check if the food name entered by the user exists.
    // The class AsyncTask is the superclass that enables to perform tasks in the background.
    class SearchEditFood extends AsyncTask<String, String, String>{
        @Override
        // Method to set some of the variables that will be used in the connection.
        protected void onPreExecute() {
            super.onPreExecute();
            name = inputName.getText().toString();
        }

        @Override
        protected String doInBackground(String... strings) {
            String url_search_product =
                    "http://nam33.student.eda.kent.ac.uk/search_edit_food.php";
            // Adds the variables that are going to be sent in an ArrayList.
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("idUser", user));
            // Calls the method that makes the connection passing the URL variables, the Method and
            // the ArrayList as parameters.
            JSONObject json = jsonParser.makeHttpRequest(url_search_product, "GET", params);

            Log.d("Create Response", json.toString());

            try {
                // Checks if the operation was performed successfully.
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // Retrieve the nutritional values from the connection.
                    JSONArray food = json.getJSONArray(TAG_FOOD);
                    JSONObject c = food.getJSONObject(0);
                    name = c.getString(TAG_NAME);
                    carbohydrate = c.getString(TAG_CARBOHYDRATE);
                    protein = c.getString(TAG_PROTEIN);
                    fat = c.getString(TAG_FAT);
                } else if(success == 2){
                    recipe = true;
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
            // Displays the nutritional values.
            Carbohydrates.setText(carbohydrate);
            Proteins.setText(protein);
            Fats.setText(fat);
            // In case something wrong happened, an error message will be displayed.
            if (check){
                error();
            } else{
                display();
            }
        }
    }
    // Connects to the database to send the changes made by the user.
    // The class AsyncTask is the superclass that enables to perform tasks in the background.
    class EditFood extends AsyncTask<String,String,String>{
        @Override
        // Method to set the variables that will be used in the connection.
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            String url_update_product =
                    "http://nam33.student.eda.kent.ac.uk/update_food.php";
            // Adds the variables that are going to be sent in an ArrayList.
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("carbohydrate", carbohydrate));
            params.add(new BasicNameValuePair("protein", protein));
            params.add(new BasicNameValuePair("fat", fat));
            params.add(new BasicNameValuePair("idUser", user));
            // Calls the method that makes the connection passing the URL variables, the Method and
            // the ArrayList as parameters.
            JSONObject json = jsonParser.makeHttpRequest(url_update_product, "POST",
                    params);

            try{
                // Checks if the operation was performed successfully.
                int success = json.getInt(TAG_SUCCESS);
                if (success ==1){
                    // Runs from the beginning this activity in case the user want to modify
                    // something else and finishes the previous one.
                    check3 = true;
                } else {
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // Displays a message telling the user that the nutritional values were updated
            // correctly.
            if (check3) {
                if (recipe) {
                    new SaveIngredients().execute();
                }
                Toast.makeText(getApplicationContext(), "Values updated correctly...", Toast.
                        LENGTH_LONG).show();
                Intent i = new Intent (getApplicationContext(), EditFoodActivity.class);
                startActivity(i);
                finish();
            }
        }
    }

    // Connects to the database to send the changes made by the user.
    // The class AsyncTask is the superclass that enables to perform tasks in the background.
    class DeleteFood extends AsyncTask<String, String, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG,user);
        }

        @Override
        protected String doInBackground(String... strings) {
            String url_delete_product =
                    "http://nam33.student.eda.kent.ac.uk/delete_food.php";
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // Adds the variables that are going to be sent in an ArrayList.
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("idUser", user));
            // Calls the method that makes the connection passing the URL variables, the Method and
            // the ArrayList as parameters.
            JSONObject json = jsonParser.makeHttpRequest(url_delete_product, "POST", params);

            try{
                // Checks if the operation was performed successfully.
                int success = json.getInt(TAG_SUCCESS);
                if (success ==1){
                    // Runs the Main Activity and finishes this activity clearing the previous log.
                    Intent i = new Intent (getApplicationContext(), MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                } else {
                    check2 = true;
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // In case something wrong happened, an error message will be displayed.
            // If not, a message saying "Food deleted" will be displayed.
            if (check2){
                error();
            } else{
                Toast.makeText(getApplicationContext(),"Food deleted",
                        Toast.LENGTH_LONG).show();
                if (recipe) {
                    new DeleteIngredients().execute();
                }
            }
        }
    }
    // Method responsible of displaying the error message
    public void error(){
        errors.setText("ERROR - Name not found.... Please try again or add it");
        errors.setVisibility(View.VISIBLE);
        inputName.setVisibility(View.VISIBLE);
        searchView.setVisibility(View.GONE);
        addItem.setVisibility(View.VISIBLE);
        createRecipe.setVisibility(View.VISIBLE);
//        addButton.setVisibility(View.VISIBLE);
        check = false;
    }
    // Method that makes visible all the widgets to modify the nutritional values once the food
    // name is checked.
    public void display(){
        Carbohydrates.setVisibility(View.VISIBLE);
        Proteins.setVisibility(View.VISIBLE);
        Fats.setVisibility(View.VISIBLE);
        editButton.setVisibility(View.VISIBLE);
        textFat.setVisibility(View.VISIBLE);
        textCarb.setVisibility(View.VISIBLE);
        textProt.setVisibility(View.VISIBLE);
        deleteButton.setVisibility(View.VISIBLE);
        calories.setVisibility(View.VISIBLE);
        total_calories.setVisibility(View.VISIBLE);
        caloriesButton.setVisibility(View.VISIBLE);
        chart.setVisibility(View.VISIBLE);
        if (recipe){
            new getIngredients().execute();
            textWeight.setVisibility(View.VISIBLE);
            Weight.setVisibility(View.VISIBLE);
            ingredient_item.setVisibility(View.VISIBLE);
            spinner_ingredients.setVisibility(View.VISIBLE);
            add_ingredient.setVisibility(View.VISIBLE);
            delete_ingredient.setVisibility(View.VISIBLE);
            textview2.setVisibility(View.VISIBLE);
            calories_100.setVisibility(View.VISIBLE);
            total_calories_100.setVisibility(View.VISIBLE);
        }
    }
    // Method that calculates and displays the calories.
    public void calculateCalories(){

        if (recipe) {
            float caloriess_100 = (Float.parseFloat(Carbohydrates.getText().toString())*4 + Float.parseFloat
                    (Proteins.getText().toString())*4 + Float.parseFloat(Fats.getText().toString())*9);
            calories_100.setText(f.format(caloriess_100) + " kcal");
            float caloriess = (Float.parseFloat(Carbohydrates.getText().toString()) * 4 + Float.parseFloat
                    (Proteins.getText().toString()) * 4 + Float.parseFloat(Fats.getText().toString()) * 9)
                    * Float.parseFloat(Weight.getText().toString()) / 100;
            calories.setText(f.format(caloriess) + " kcal");
        } else {
            float caloriess = (Float.parseFloat(Carbohydrates.getText().toString())*4 + Float.parseFloat
                    (Proteins.getText().toString())*4 + Float.parseFloat(Fats.getText().toString())*9);
            calories.setText(f.format(caloriess) + " kcal");
        }

    }

    // Connects to the database to take all the food names from the user and 'admin'.
    // The class AsyncTask is the superclass that enables to perform tasks in the background.
    class CompleteText extends AsyncTask<String, String, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String url_search_all_food =
                    "http://nam33.student.eda.kent.ac.uk/search_all_food_update.php";
            // Adds the variables that are going to be sent in an ArrayList.
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("idUser", user));
            // Calls the method that makes the connection passing the URL variables, the Method and
            // the ArrayList as parameters.
            JSONObject json = jsonParser.makeHttpRequest(url_search_all_food, "GET", params);

            try {
                // Checks if the operation was performed successfully.
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // Creates an ArrayList to store all the names.
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
            // When the user types one character on the search bar, a dropdown list will appear
            // showing the food item names that can be modified.
            if (theme.equals("dark")) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.
                        layout.spinner_ingredient, items);
                inputName.setThreshold(1);
                inputName.setAdapter(adapter);
            } else if (theme.equals("light")){
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.
                        layout.simple_list_item_1, items);
                inputName.setThreshold(1);
                inputName.setAdapter(adapter);
            }
        }
    }

    private void resize(){

        mainli.setPadding(width*4866/100000,0,width*4866/100000,0);
        Proteins.setWidth(width*3576/10000);
        Fats.setWidth(width*3576/10000);
        Carbohydrates.setWidth(width*3576/10000);
        Weight.setWidth(width*3576/10000);
        calories.setWidth(width*3576/10000);
        calories_100.setWidth(width*3576/10000);
        inputName.setDropDownWidth(width);


        RelativeLayout.LayoutParams f1 = (RelativeLayout.LayoutParams) Fats.getLayoutParams();
        f1.setMargins(width*486/10000,0,0,0);
        edit.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/ Resources.getSystem().getDisplayMetrics().density)*33/411);
        add_item.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*19/411);
        name_ingredient.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*19/411);
        textFat.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*19/411);
        textProt.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*19/411);
        textCarb.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*19/411);
        textWeight.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*19/411);
        ingredient_item.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*19/411);
        total_calories.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*19/411);
        total_calories_100.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*19/411);
        srchButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*12/411);
        caloriesButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*12/411);
        chart.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*12/411);
        deleteButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*12/411);
        editButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*12/411);
        add_ingredient.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*12/411);
        delete_ingredient.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*12/411);
        back.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*12/411);
        addItem.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*12/411);
        createRecipe.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*12/411);
        save_ingredient.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*12/411);
        inputName.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*14/411);
        searchView.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/Resources.getSystem().getDisplayMetrics().density)*14/411);


        caloriesButton.setPadding(width*73/1000,width*121/10000,width*73/1000,width*121/10000);
        chart.setPadding(width*73/1000,width*121/10000,width*73/1000,width*121/10000);
        srchButton.setPadding(width*73/1000,width*121/10000,width*73/1000,width*121/10000);
        deleteButton.setPadding(width*73/1000,width*121/10000,width*73/1000,width*121/10000);
        editButton.setPadding(width*73/1000,width*121/10000,width*73/1000,width*121/10000);
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
        LinearLayout.LayoutParams i1 = (LinearLayout.LayoutParams) add_item.getLayoutParams();
        i1.setMargins(0,height*2844/100000,0,0);
        RelativeLayout.LayoutParams c4 = (RelativeLayout.LayoutParams) textCarb.getLayoutParams();
        c4.setMargins(0,height*2133/100000,0,0);
        RelativeLayout.LayoutParams c5 = (RelativeLayout.LayoutParams) textWeight.getLayoutParams();
        c5.setMargins(0,height*2133/100000,width*2116/10000,0);

    }





    // Class that performs the connection to the database to get the ingredients from recipes.
    // The class AsyncTask is the superclass that enables to perform tasks in the background.
    class getIngredients extends AsyncTask<String, String, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // Method responsible of the connection to the database.
        @Override
        protected String doInBackground(String... strings) {
            // Local variable that contains the path of the PHP file used for connection
            String url_get_Ingredients = "http://nam33.student.eda.kent.ac.uk/get_Ingredients.php";
            // Creating an ArrayList and adding the variables that will be sent.
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("recipeName", name.substring(0,name.length()-13) + user +")"));
            params.add(new BasicNameValuePair("idUser", user));
            // Calls the method that makes the connection passing the URL variables, the Method and
            // the ArrayList as parameters.
            JSONObject json = jsonParser.makeHttpRequest(url_get_Ingredients,
                    "GET", params);

            try {
                // Checks if the operation was performed successfully.
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1){
                    // Gets the name, nutritional values, weight of each ingredient from the
                    // recipe and stores each value in an array.
                    nameList = new ArrayList<String>();
                    carboList = new ArrayList<String>();
                    protList = new ArrayList<String>();
                    fatList = new ArrayList<String>();
                    weightList = new ArrayList<String>();


                    nameList.add("Select the Ingredient");
                    JSONArray food = json.getJSONArray(TAG_FOOD);
                    for (int i = 0; i < food.length(); i++){
                        JSONObject c = food.getJSONObject(i);
                        iname = c.getString(TAG_INGREDIENT) + " (Ingredient)";
                        icarbo = c.getString(TAG_CARBOHYDRATE);
                        iprot = c.getString(TAG_PROTEIN);
                        ifat = c.getString(TAG_FAT);
                        iweight = c.getString(TAG_WEIGHT);
                        nameList.add(iname);
                        carboList.add(icarbo);
                        protList.add(iprot);
                        fatList.add(ifat);
                        weightList.add(iweight);
                        Log.d("TESf","ESR");
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
            if (theme.equals("light")) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
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
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
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
    }

    class DeleteIngredients extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            name = name.substring(0,name.length()-13) +user+")";
        }

        @Override
        protected String doInBackground(String... strings) {
            String url_delete_ingredients =
                    "http://nam33.student.eda.kent.ac.uk/delete_ingredients.php";
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // Adds the variables that are going to be sent in an ArrayList.
            params.add(new BasicNameValuePair("recipeName", name));
            params.add(new BasicNameValuePair("idUser", user));
            // Calls the method that makes the connection passing the URL variables, the Method and
            // the ArrayList as parameters.
            JSONObject json = jsonParser.makeHttpRequest(url_delete_ingredients, "POST", params);

            try{
                // Checks if the operation was performed successfully.
                int success = json.getInt(TAG_SUCCESS);
                if (success ==1){
                    // Runs the Main Activity and finishes this activity clearing the previous log.
                } else {

                }
            } catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    class SaveIngredients extends AsyncTask<String, String, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String url_update_ingredients =
                    "http://nam33.student.eda.kent.ac.uk/update_ingredient.php";
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // Adds the variables that are going to be sent in an ArrayList.
            for (int i = 0; i < carboList.size(); i++) {
                Log.d("ds", nameList.get(i + 1).substring(0,nameList.get(i + 1).length() - 13) + " "+ weightList.get(i) +" "+carboList.get(i) +" "+protList.get(i)+" "+fatList.get(i)+" "+name.substring(0,name.length()-13) + user + ")"+" "+user);
                params.add(new BasicNameValuePair("ingredient", nameList.get(i + 1).substring(0,nameList.get(i + 1).length() - 13)));
                params.add(new BasicNameValuePair("weight", weightList.get(i)));
                params.add(new BasicNameValuePair("carbohydrate", carboList.get(i)));
                params.add(new BasicNameValuePair("protein", protList.get(i)));
                params.add(new BasicNameValuePair("fat", fatList.get(i)));
                params.add(new BasicNameValuePair("recipeName", name.substring(0,name.length()-13) + user + ")"));
                params.add(new BasicNameValuePair("idUser", user));
                // Calls the method that makes the connection passing the URL variables, the Method and
                // the ArrayList as parameters.
                JSONObject json = jsonParser.makeHttpRequest(url_update_ingredients, "POST", params);

                try {
                    // Checks if the operation was performed successfully.
                    int success = json.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        // Runs the Main Activity and finishes this activity clearing the previous log.

                    } else {

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
        }
    }
    class DeleteIngredient extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String url_delete_ingredient =
                    "http://nam33.student.eda.kent.ac.uk/delete_ingredient.php";
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // Adds the variables that are going to be sent in an ArrayList.
            for (int i = 0; i < deleteIng.size(); i++) {
                params.add(new BasicNameValuePair("ingredient", deleteIng.get(i).substring(0,deleteIng.get(i).length() - 13)));
                params.add(new BasicNameValuePair("recipeName", name.substring(0,name.length()-13) +user+")"));
                params.add(new BasicNameValuePair("idUser", user));
                // Calls the method that makes the connection passing the URL variables, the Method and
                // the ArrayList as parameters.
                JSONObject json = jsonParser.makeHttpRequest(url_delete_ingredient, "POST", params);

                try {
                    // Checks if the operation was performed successfully.
                    int success = json.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        // Runs the Main Activity and finishes this activity clearing the previous log.
                    } else {

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
        }
    }

    // Class that performs the connection to the database to add the ingredients to the recipes
    // database.
    // The class AsyncTask is the superclass that enables to perform tasks in the background.
    class AddRecipe extends AsyncTask<String, String, String>{
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
            for (int i = 0; i < addIng.size(); i = i + 6) {
                params.add(new BasicNameValuePair("ingredient", addIng.get(i)));
                params.add(new BasicNameValuePair("weight", addIng.get(i+1)));
                params.add(new BasicNameValuePair("carbohydrate", addIng.get(i+2)));
                params.add(new BasicNameValuePair("protein", addIng.get(i+3)));
                params.add(new BasicNameValuePair("fat", addIng.get(i+4)));
                params.add(new BasicNameValuePair("recipeName", name.substring(0,name.length()-13) +user+")"));
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
                        // Once one ingredient is added, remove all its data from the ArrayList to
                        // continue with the enxt ingredient.
                        for (int v = 0; v < 7; v++) {
                            params.remove(0);
                        }
                    } else {
                        // Displays an error message.
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

        }
    }

    public void detectDarkMode() {
        theme = sharedPref.getString(getString(R.string.theme), "light");
        if (theme.equals("light")) {
            sc.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            edit.setTextColor(getResources().getColor(R.color.colorBlack));
            add_item.setTextColor(getResources().getColor(R.color.colorBlack));
            inputName.setHintTextColor(getResources().getColor(R.color.colorGrey));
            inputName.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_search_blue_24dp), null, null, null);
            inputName.setTextColor(getResources().getColor(R.color.colorBlack));
            inputName.setBackgroundResource(R.drawable.search_field_rounded);
            searchView.setHintTextColor(getResources().getColor(R.color.colorGrey));
            searchView.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_search_blue_24dp), null, null, null);
            searchView.setTextColor(getResources().getColor(R.color.colorBlack));
            searchView.setBackgroundResource(R.drawable.search_field_rounded);
            srchButton.setTextColor(getResources().getColor(R.color.colorWhite));
            srchButton.setBackgroundResource(R.drawable.go_search_rounded);
            line.setBackgroundColor(getResources().getColor(R.color.colorBlack));


            textProt.setTextColor(getResources().getColor(R.color.colorBlack));
            textCarb.setTextColor(getResources().getColor(R.color.colorBlack));
            textFat.setTextColor(getResources().getColor(R.color.colorBlack));
            textWeight.setTextColor(getResources().getColor(R.color.colorBlack));
            Proteins.setTextColor(getResources().getColor(R.color.colorBlack));
            Proteins.setBackgroundResource(R.drawable.search_field_rounded);
            Fats.setTextColor(getResources().getColor(R.color.colorBlack));
            Fats.setBackgroundResource(R.drawable.search_field_rounded);
            Carbohydrates.setTextColor(getResources().getColor(R.color.colorBlack));
            Carbohydrates.setBackgroundResource(R.drawable.search_field_rounded);
            Weight.setTextColor(getResources().getColor(R.color.colorBlack));
            Weight.setBackgroundResource(R.drawable.search_field_rounded);
            total_calories.setTextColor(getResources().getColor(R.color.colorBlack));
            total_calories_100.setTextColor(getResources().getColor(R.color.colorBlack));
            calories.setTextColor(getResources().getColor(R.color.colorBlack));
            calories.setBackgroundResource(R.drawable.search_field_rounded);
            calories_100.setTextColor(getResources().getColor(R.color.colorBlack));
            calories_100.setBackgroundResource(R.drawable.search_field_rounded);
            chart.setBackgroundResource(R.drawable.show_chart_rounded);
            chart.setTextColor(getResources().getColor(R.color.colorWhite));
            caloriesButton.setBackgroundResource(R.drawable.edit_item_rounded);
            caloriesButton.setTextColor(getResources().getColor(R.color.colorWhite));
            deleteButton.setBackgroundResource(R.drawable.clear_button_rounded);
            deleteButton.setTextColor(getResources().getColor(R.color.colorWhite));
            editButton.setBackgroundResource(R.drawable.save_entry_rounded);
            editButton.setTextColor(getResources().getColor(R.color.colorWhite));
            back.setBackgroundResource(R.drawable.clear_button_rounded);
            back.setTextColor(getResources().getColor(R.color.colorWhite));
            save_ingredient.setBackgroundResource(R.drawable.save_entry_rounded);
            save_ingredient.setTextColor(getResources().getColor(R.color.colorWhite));
            add_ingredient.setBackgroundResource(R.drawable.show_chart_rounded);
            add_ingredient.setTextColor(getResources().getColor(R.color.colorWhite));
            delete_ingredient.setBackgroundResource(R.drawable.clear_button_rounded);
            delete_ingredient.setTextColor(getResources().getColor(R.color.colorWhite));
            textview2.setBackgroundColor(getResources().getColor(R.color.colorBlack));
            name_ingredient.setTextColor(getResources().getColor(R.color.colorBlack));
            ingredient_item.setTextColor(getResources().getColor(R.color.colorBlack));
            name_ingredient_field.setTextColor(getResources().getColor(R.color.colorBlack));
            name_ingredient_field.setHintTextColor(getResources().getColor(R.color.colorGrey));
            name_ingredient_field.setBackgroundResource(R.drawable.search_field_rounded);
            inputName.setDropDownBackgroundResource(R.color.colorWhite);
            spinner_ingredients.setPopupBackgroundResource(R.color.colorWhite);
        } else if (theme.equals("dark")) {
            sc.setBackgroundColor(getResources().getColor(R.color.darkThemeBackground));
            edit.setTextColor(getResources().getColor(R.color.colorWhite));
            add_item.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            inputName.setHintTextColor(getResources().getColor(R.color.colorDarkThemeHintGrey));
            inputName.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_search_grey_24dp), null, null, null);
            inputName.setTextColor(getResources().getColor(R.color.colorWhite));
            inputName.setBackgroundResource(R.drawable.search_field_rounded_dark);
            searchView.setHintTextColor(getResources().getColor(R.color.colorDarkThemeHintGrey));
            searchView.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_search_grey_24dp), null, null, null);
            searchView.setTextColor(getResources().getColor(R.color.colorWhite));
            searchView.setBackgroundResource(R.drawable.search_field_rounded_dark);
            srchButton.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            srchButton.setBackgroundResource(R.drawable.go_search_rounded_dark);
            line.setBackgroundColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));


            textProt.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            textCarb.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            textFat.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            textWeight.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            Proteins.setTextColor(getResources().getColor(R.color.colorWhite));
            Proteins.setBackgroundResource(R.drawable.search_field_rounded_dark);
            Fats.setTextColor(getResources().getColor(R.color.colorWhite));
            Fats.setBackgroundResource(R.drawable.search_field_rounded_dark);
            Carbohydrates.setTextColor(getResources().getColor(R.color.colorWhite));
            Carbohydrates.setBackgroundResource(R.drawable.search_field_rounded_dark);
            Weight.setTextColor(getResources().getColor(R.color.colorWhite));
            Weight.setBackgroundResource(R.drawable.search_field_rounded_dark);
            total_calories.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            total_calories_100.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            calories.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            calories.setBackgroundResource(R.drawable.search_field_rounded_dark);
            calories_100.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            calories_100.setBackgroundResource(R.drawable.search_field_rounded_dark);
            chart.setBackgroundResource(R.drawable.show_chart_rounded_dark);
            chart.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            caloriesButton.setBackgroundResource(R.drawable.edit_item_rounded_dark);
            caloriesButton.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            deleteButton.setBackgroundResource(R.drawable.clear_button_rounded_dark);
            deleteButton.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            editButton.setBackgroundResource(R.drawable.save_entry_rounded_dark);
            editButton.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            back.setBackgroundResource(R.drawable.clear_button_rounded_dark);
            back.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            save_ingredient.setBackgroundResource(R.drawable.save_entry_rounded_dark);
            save_ingredient.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            add_ingredient.setBackgroundResource(R.drawable.show_chart_rounded_dark);
            add_ingredient.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            delete_ingredient.setBackgroundResource(R.drawable.clear_button_rounded_dark);
            delete_ingredient.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            textview2.setBackgroundColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            name_ingredient.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            ingredient_item.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            name_ingredient_field.setTextColor(getResources().getColor(R.color.colorWhite));
            name_ingredient_field.setHintTextColor(getResources().getColor(R.color.colorDarkThemeHintGrey));
            name_ingredient_field.setBackgroundResource(R.drawable.search_field_rounded_dark);
            inputName.setDropDownBackgroundResource(R.color.darkThemeBackground);
            spinner_ingredients.setPopupBackgroundResource(R.color.darkThemeBackground);
        }
    }
}