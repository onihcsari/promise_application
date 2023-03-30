package com.example.project1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;


public class SubActivity extends AppCompatActivity{

    public Button save_btn;
    public TextView diaryTextView,textView2,textView3;
    public EditText editText2, editText3, editText4, editText5;
    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    public DatabaseReference databaseReference = database.getReference();
    public int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        Intent intent = getIntent();
        ArrayList<Integer> data = (ArrayList<Integer>) intent.getSerializableExtra("input");
        year = data.get(0);
        month = data.get(1);
        day = data.get(2);

        Calendar cal = Calendar.getInstance();
        TimePicker timePicker = findViewById(R.id.timePicker);
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        save_btn=findViewById(R.id.save_btn);
        editText2 = findViewById(R.id.editText2);
        editText3 = findViewById(R.id.editText3);
        editText4 = findViewById(R.id.editText4);
        editText5 = findViewById(R.id.editText5);
        save_btn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 addDBfile(editText2.getText().toString(), editText3.getText().toString(),
                         editText4.getText().toString(), editText5.getText().toString(),
                         year, month+1, day, hour, minute);
                 finish();
             }
        });
    }

    public void addDBfile(String location, String name, String what, String number, int year, int month, int day, int hour, int minute) {
        DBfile DBfile = new DBfile(location, name, what, number, year, month, day, hour, minute);
        databaseReference.child("DB").child("USER").child("location").setValue(DBfile.getLocation());
        databaseReference.child("DB").child("USER").child("name").setValue(DBfile.getName());
        databaseReference.child("DB").child("USER").child("what").setValue(DBfile.getWhat());
        databaseReference.child("DB").child("USER").child("number").setValue(DBfile.getNumber());
        databaseReference.child("DB").child("USER").child("year").setValue(DBfile.getYear());
        databaseReference.child("DB").child("USER").child("month").setValue(DBfile.getMonth());
        databaseReference.child("DB").child("USER").child("day").setValue(DBfile.getDay());
        databaseReference.child("DB").child("USER").child("hour").setValue(DBfile.getHour());
        databaseReference.child("DB").child("USER").child("minute").setValue(DBfile.getMinute());
    }


}
