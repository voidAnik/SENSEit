package com.example.senseit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

public class HistoryActivity extends AppCompatActivity {

    public ListView listView;
    public DatabaseHelper databaseHelper;
    public Integer sensor_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_history);
        Objects.requireNonNull(getSupportActionBar()).setTitle("HISTORY");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = findViewById(R.id.l_view);
        databaseHelper = new DatabaseHelper(this);
        sensor_id = getIntent().getIntExtra("sensor_id", -1);
        loadData(sensor_id);
    }

    private void loadData(int id) {
        ArrayList<String> column1 = new ArrayList<>();
        ArrayList<String> column2 = new ArrayList<>();
        Cursor results;
        switch (id){
            case 1:
                results = databaseHelper.getLightData();
                break;
            case 2:
                results = databaseHelper.getProxyData();
                break;
            case 3:
                results = databaseHelper.getAccelerometerData();
                break;
            case 4:
                results = databaseHelper.getGyroData();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + id);
        }


        if(results.getCount() == 0){
            Toast.makeText(this, "No Data Available for this sensor!", Toast.LENGTH_SHORT).show();
        }else{
            if(id == 1 || id == 2) {
                while (results.moveToNext()) {
                    column1.add(results.getString(2));
                    column2.add(results.getString(3));
                }
            }else{
                while (results.moveToNext()) {
                    column1.add(results.getString(2));
                    column2.add("X: "+results.getString(3)+"  Y: "+results.getString(4)+"  Z: "+results.getString(5));
                }
            }
        }

        CustomAdapter adapter = new CustomAdapter(this, column1, column2);
        listView.setAdapter(adapter);
    }

   /* @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent home = new Intent(this, MainActivity.class);
    }*/
}