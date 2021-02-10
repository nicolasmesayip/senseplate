package com.example.senseplate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Test extends AppCompatActivity {
    private ArrayList<FoodItem> foodItemArrayList = new ArrayList<>();

    private static final int VERTICAL_ITEM_SPACE = 30;
    private static final String NOTIFY_DELETE = "Item deleted";
    private static final String NOTIFY_DELETE_ALL = "All items deleted";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String username, theme;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private JSONParser jsonParser = new JSONParser();
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_HISTORY = "history";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_CARBOHYDRATE = "carbohydrate";
    private static final String TAG_PROTEIN = "protein";
    private static final String TAG_FAT = "fat";
    private static final String TAG_WEIGHT = "weight";
    private static final String TAG_DATE = "date";
    private ArrayList<String> nameList, carboList, weightList, protList, fatList, dateList, idList;
    private DecimalFormat f = new DecimalFormat("####.00");
    private Button b1, b2, b3, b4, btnClearAll;
    private RelativeLayout backgroundbtn;
    private int height, width;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_test);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        sharedPreferences = getSharedPreferences("com.example.senseplate", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        username = sharedPreferences.getString(getString(R.string.username), "");
        theme = sharedPreferences.getString(getString(R.string.theme), "light");


        buildRecyclerView();

        backgroundbtn = (RelativeLayout) findViewById(R.id.backgroundbtns);
        btnClearAll = (Button) findViewById(R.id.button_clear_all);
        b1 = (Button) findViewById(R.id.button1);
        b2 = (Button) findViewById(R.id.button2);
        b3 = (Button) findViewById(R.id.button3);
        b4 = (Button) findViewById(R.id.button4);

        btnClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(Test.this, R.style.AlertDialogStyle)
                        .setMessage("Are you sure you want to delete all items?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                foodItemArrayList.clear();
                                mAdapter.notifyDataSetChanged();
                                Toast.makeText(getApplicationContext(), NOTIFY_DELETE_ALL, Toast.LENGTH_LONG)
                                        .show();
                                id = "";
                                new DeleteHistory().execute();
                            }
                        })

                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .create()
                        .show();
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
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
                mRecyclerView.scrollToPosition(0);
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
        resize();
        detectDarkMode();
        new getIngredients().execute();
    }

    public void insertItem(int number) {
        for (int i = 0; i < number; i++) {
            String carbs = carboList.get(i);
            String fats = fatList.get(i);
            String protein = protList.get(i);
            String food = nameList.get(i);
            String calories = f.format(Float.parseFloat(carbs) * 4 + Float.parseFloat(fats) * 9 + Float.parseFloat(protein) * 4);
            String weight = weightList.get(i);
            String date = dateList.get(i);
            date = date.substring(8,10) +date.substring(4,8) + date.substring(0,4) + date.substring(10,16);

            foodItemArrayList.add(0,
                    new FoodItem(carbs, fats, protein, food, calories, weight, date));
        }
        mAdapter.notifyItemInserted(0);
        mRecyclerView.scrollToPosition(0);
    }

    public void buildRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ExampleAdapter();
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
    }


    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onSwiped(final RecyclerView.ViewHolder viewHolder, int i) {

            new AlertDialog.Builder(viewHolder.itemView.getContext(), R.style.AlertDialogStyle)
                    .setMessage("Are you sure you want to delete this item?")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final int position = viewHolder.getAdapterPosition();
                            id = idList.get(position);
                            new DeleteHistory().execute();
                            foodItemArrayList.remove(position);
                            mAdapter.notifyItemRemoved(position);
                            Toast.makeText(getApplicationContext(), NOTIFY_DELETE, Toast.LENGTH_LONG)
                                    .show();
                        }
                    })

                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                        }
                    })
                    .create()
                    .show();
        }
    };


    public static class ExampleViewHolder extends RecyclerView.ViewHolder {

        public TableLayout tableLayout;
        public TableRow tableRow2;

        public TextView carbs;
        public TextView fats;
        public TextView protein;
        public TextView foodName;
        public TextView calories;
        public TextView weight;
        public TextView timestamp;

        public ExampleViewHolder(View itemView) {
            super(itemView);

            tableLayout = itemView.findViewById(R.id.table_layout);
            tableRow2 = (TableRow) tableLayout.getChildAt(1);

            calories = (TextView) tableRow2.getChildAt(0);
            carbs = (TextView) tableRow2.getChildAt(1);
            fats = (TextView) tableRow2.getChildAt(2);
            protein = (TextView) tableRow2.getChildAt(3);

            foodName = itemView.findViewById(R.id.food_name_field);
            weight = itemView.findViewById(R.id.food_weight_field);

            timestamp = itemView.findViewById(R.id.time_added);
        }
    }

    class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.ExampleViewHolder> {

        public class ExampleViewHolder extends RecyclerView.ViewHolder {

            protected CardView cardView;
            protected TableLayout tableLayout;
            protected TableRow tableRow1;
            protected TableRow tableRow2;

            protected TextView carbs;
            protected TextView fats;
            protected TextView protein;
            protected TextView foodName;
            protected TextView calories;
            protected TextView carbs_field;
            protected TextView fats_field;
            protected TextView protein_field;
            protected TextView calories_field;
            protected TextView weight;
            protected TextView timestamp;

            public ExampleViewHolder(View itemView) {
                super(itemView);

                cardView = (CardView) itemView.findViewById(R.id.cardview);
                tableLayout = itemView.findViewById(R.id.table_layout);
                tableRow1 = (TableRow) tableLayout.getChildAt(0);
                tableRow2 = (TableRow) tableLayout.getChildAt(1);

                calories_field = (TextView) tableRow1.getChildAt(0);
                carbs_field = (TextView) tableRow1.getChildAt(1);
                fats_field = (TextView) tableRow1.getChildAt(2);
                protein_field = (TextView) tableRow1.getChildAt(3);

                calories = (TextView) tableRow2.getChildAt(0);
                carbs = (TextView) tableRow2.getChildAt(1);
                fats = (TextView) tableRow2.getChildAt(2);
                protein = (TextView) tableRow2.getChildAt(3);

                foodName = itemView.findViewById(R.id.food_name_field);
                weight = itemView.findViewById(R.id.food_weight_field);

                timestamp = itemView.findViewById(R.id.time_added);
                resizeCardview();
                detectDarkModeCardview();
            }
            private void resizeCardview(){
                foodName.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/ Resources.getSystem().getDisplayMetrics().density)*26/411);
                timestamp.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/ Resources.getSystem().getDisplayMetrics().density)*16/411);
                weight.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/ Resources.getSystem().getDisplayMetrics().density)*16/411);
                calories_field.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/ Resources.getSystem().getDisplayMetrics().density)*23/411);
                carbs_field.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/ Resources.getSystem().getDisplayMetrics().density)*23/411);
                fats_field.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/ Resources.getSystem().getDisplayMetrics().density)*23/411);
                protein_field.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/ Resources.getSystem().getDisplayMetrics().density)*23/411);
                calories.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/ Resources.getSystem().getDisplayMetrics().density)*15/411);
                carbs.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/ Resources.getSystem().getDisplayMetrics().density)*15/411);
                fats.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/ Resources.getSystem().getDisplayMetrics().density)*15/411);
                protein.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/ Resources.getSystem().getDisplayMetrics().density)*15/411);

            }
            private void detectDarkModeCardview(){
                if (theme.equals("light")){
                    cardView.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                    tableLayout.setBackgroundResource(R.drawable.table_rounded);
                    foodName.setTextColor(getResources().getColor(R.color.colorBlack));
                    timestamp.setTextColor(getResources().getColor(R.color.colorWhite));
                    timestamp.setBackgroundResource(R.drawable.rounded_timestamp);
                    weight.setTextColor(getResources().getColor(R.color.colorWhite));
                    weight.setBackgroundResource(R.drawable.current_weighting_rounded);
                    calories.setTextColor(getResources().getColor(R.color.colorWhite));
                    calories_field.setTextColor(getResources().getColor(R.color.colorWhite));
                    carbs.setTextColor(getResources().getColor(R.color.colorWhite));
                    carbs_field.setTextColor(getResources().getColor(R.color.colorWhite));
                    protein.setTextColor(getResources().getColor(R.color.colorWhite));
                    protein_field.setTextColor(getResources().getColor(R.color.colorWhite));
                    fats.setTextColor(getResources().getColor(R.color.colorWhite));
                    fats_field.setTextColor(getResources().getColor(R.color.colorWhite));
                } else if (theme.equals("dark")){
                    cardView.setBackgroundColor(getResources().getColor(R.color.darkThemeBackground));
                    tableLayout.setBackgroundResource(R.drawable.table_rounded_dark);
                    foodName.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
                    timestamp.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
                    timestamp.setBackgroundResource(R.drawable.rounded_timestamp_dark);
                    weight.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
                    weight.setBackgroundResource(R.drawable.current_weighting_rounded_dark);
                    calories.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
                    calories_field.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
                    carbs.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
                    carbs_field.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
                    protein.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
                    protein_field.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
                    fats.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
                    fats_field.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
                }
            }
        }

        @NonNull
        @Override
        public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_item, parent, false);
            ExampleViewHolder evh = new ExampleViewHolder(v);
            return evh;
        }

        @Override
        public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
            FoodItem currentItem = foodItemArrayList.get(position);

            holder.carbs.setText(currentItem.getCarbs()+"g");
            holder.fats.setText(currentItem.getFats()+"g");
            holder.protein.setText(currentItem.getProtein()+"g");
            holder.foodName.setText(currentItem.getFoodName());
            holder.calories.setText(currentItem.calories()+"kcal");
            holder.weight.setText(currentItem.getWeight()+"g");
            holder.timestamp.setText(currentItem.getCurrentDate());
        }

        @Override
        public int getItemCount() {
            return foodItemArrayList.size();
        }

    }

    class FoodItem {

        private final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd/MM/YYYY 'at' hh: mm", Locale.UK);

        private String carbs;
        private String currentDate;
        private String fats;
        private String protein;
        private String food_name;
        private String calories;
        private String weight;


        public FoodItem(String carbs, String fats, String protein, String food_name, String calories, String weight, String currentDate) {
            this.carbs = carbs;
            this.fats = fats;
            this.protein = protein;
            this.food_name = food_name;
            this.calories = calories;
            this.weight = weight;
            this.currentDate = currentDate;

        }

        public String getCarbs() {
            return carbs;
        }

        public String getFats() {
            return fats;
        }

        public String getProtein() {
            return protein;
        }

        public String getFoodName() {
            return food_name;
        }

        public String calories() {
            return calories;
        }

        public String getWeight(){
            return weight;
        }

        public String getCurrentDate() {
            return currentDate;
        }
    }

    class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int verticalSpaceHeight;

        public VerticalSpaceItemDecoration(int verticalSpaceHeight) {
            this.verticalSpaceHeight  = verticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = verticalSpaceHeight;
        }
    }
    class getIngredients extends AsyncTask<String, String, String> {

        private String iname, icarbo, iprot, ifat, iweight, idate, id;
        int number;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // Method responsible of the connection to the database.
        @Override
        protected String doInBackground(String... strings) {
            // Local variable that contains the path of the PHP file used for connection
            String url_getHistory = "http://nam33.student.eda.kent.ac.uk/getHistory.php";
            // Creating an ArrayList and adding the variables that will be sent.
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", username));
            // Calls the method that makes the connection passing the URL variables, the Method and
            // the ArrayList as parameters.
            JSONObject json = jsonParser.makeHttpRequest(url_getHistory,
                    "GET", params);

            try {
                // Checks if the operation was performed successfully.
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // Gets the name, nutritional values, weight of each ingredient from the
                    // recipe and stores each value in an array.
                    nameList = new ArrayList<String>();
                    carboList = new ArrayList<String>();
                    protList = new ArrayList<String>();
                    fatList = new ArrayList<String>();
                    weightList = new ArrayList<String>();
                    dateList = new ArrayList<String>();
                    idList = new ArrayList<String>();


                    JSONArray history = json.getJSONArray(TAG_HISTORY);
                    for (int i = 0; i < history.length(); i++) {
                        JSONObject c = history.getJSONObject(i);
                        id = c.getString(TAG_ID);
                        iname = c.getString(TAG_NAME);
                        icarbo = c.getString(TAG_CARBOHYDRATE);
                        iprot = c.getString(TAG_PROTEIN);
                        ifat = c.getString(TAG_FAT);
                        iweight = c.getString(TAG_WEIGHT);
                        idate = c.getString(TAG_DATE);
                        idList.add(0, id);
                        nameList.add(iname);
                        carboList.add(icarbo);
                        protList.add(iprot);
                        fatList.add(ifat);
                        weightList.add(iweight);
                        dateList.add(idate);
                        number++;
                    }

                } else {
                }
            } catch (
                    JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            insertItem(number);
        }
    }

    class DeleteHistory extends AsyncTask<String, String, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            // Local variable that contains the path of the PHP file used for connection
            String url_history_product = "http://nam33.student.eda.kent.ac.uk/deleteHistory.php";
            // Creating an ArrayList and adding the ingredient variables that will be sent.
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", id));
            params.add(new BasicNameValuePair("username", username));

            // Calls the method that makes the connection passing the URL variables,
            // the Method and the ArrayList as parameters.
            JSONObject jsons = jsonParser.makeHttpRequest(url_history_product,
                    "POST", params);
            try {
                // Checks if the operation was performed successfully.
                int success = jsons.getInt(TAG_SUCCESS);
                if (success == 1) {
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
    private void resize(){
        b1.setWidth(width*25/100);
        b2.setWidth(width*25/100);
        b3.setWidth(width*25/100);
        b4.setWidth(width*25/100);
        btnClearAll.setTextSize(TypedValue.COMPLEX_UNIT_SP, (width/ Resources.getSystem().getDisplayMetrics().density)*12/411);
    }
    private void detectDarkMode(){
        if (theme.equals("light")){
            mRecyclerView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            backgroundbtn.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            btnClearAll.setTextColor(getResources().getColor(R.color.colorBlack));
            btnClearAll.setBackgroundResource(R.drawable.clear_button_rounded);
            b1.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            b2.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            b3.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            b4.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            b1.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_home_black_24dp),null,null);
            b2.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_library_add_black_24dp),null,null);
            b3.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_history_green_24dp),null,null);
            b4.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_settings_black_24dp),null,null);
            b1.setTextColor(getResources().getColor(R.color.darkThemeBackground));
            b2.setTextColor(getResources().getColor(R.color.darkThemeBackground));
            b3.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            b4.setTextColor(getResources().getColor(R.color.darkThemeBackground));
        } else if (theme.equals("dark")){
            mRecyclerView.setBackgroundColor(getResources().getColor(R.color.colorDarkGrey));
            backgroundbtn.setBackgroundColor(getResources().getColor(R.color.colorDarkGrey));
            btnClearAll.setTextColor(getResources().getColor(R.color.colorDarkThemeTitleGrey));
            btnClearAll.setBackgroundResource(R.drawable.clear_button_rounded_dark);
            b1.setBackgroundColor(getResources().getColor(R.color.darkThemeBackground));
            b2.setBackgroundColor(getResources().getColor(R.color.darkThemeBackground));
            b3.setBackgroundColor(getResources().getColor(R.color.darkThemeBackground));
            b4.setBackgroundColor(getResources().getColor(R.color.darkThemeBackground));
            b1.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_home_white_24dp),null,null);
            b2.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_library_add_white_24dp),null,null);
            b3.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_history_blue_24dp),null,null);
            b4.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_settings_white_24dp),null,null);
            b1.setTextColor(getResources().getColor(R.color.colorWhite));
            b2.setTextColor(getResources().getColor(R.color.colorWhite));
            b3.setTextColor(getResources().getColor(R.color.colorBlue));
            b4.setTextColor(getResources().getColor(R.color.colorWhite));
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
