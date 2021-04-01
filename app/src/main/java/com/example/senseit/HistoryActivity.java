package com.example.senseit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    ListView listView;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listView = findViewById(R.id.l_view);
        databaseHelper = new DatabaseHelper(this);
        loadData();
    }

    private void loadData() {
        ArrayList<String> data = new ArrayList<>();
        Cursor results = databaseHelper.getAllData();

        if(results.getCount() == 0){
            Toast.makeText(this, "No Data Available for this sensor!", Toast.LENGTH_SHORT).show();
        }else{
            while (results.moveToNext()){
                data.add(results.getString(0)+"\t"+results.getString(1));
            }
        }
    }
}