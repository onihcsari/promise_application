package com.example.project1;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class SubActivity extends AppCompatActivity{

    public Button btn_selectDate, btn_selectTime, btn_OK;
    public EditText editTextDate, editTextTime, editTextTitle, editTextLocation, editTextCategory, editTextNumber;
    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    public DatabaseReference databaseReference = database.getReference();
    public TimePickerDialog timePickerDialog;
    public DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub2);

        Intent intent = getIntent();
        ArrayList<Integer> data = (ArrayList<Integer>) intent.getSerializableExtra("input");


        btn_selectDate=findViewById(R.id.btn_selectDate);
        btn_selectTime=findViewById(R.id.btn_selectTime);
        editTextTitle=findViewById(R.id.editTextTitle);
        editTextTime=findViewById(R.id.editTextTime);
        editTextLocation=findViewById(R.id.editTextLocation);
        editTextCategory=findViewById(R.id.editTextCategory);
        editTextNumber=findViewById(R.id.editTextNumber);
        editTextDate=findViewById(R.id.editTextDate);

        btn_OK=findViewById(R.id.button_OK);

        Calendar calendar = Calendar.getInstance();
        int pYear = calendar.get(Calendar.YEAR);
        int pMonth = calendar.get(Calendar.MONTH);
        int pDay = calendar.get(Calendar.DAY_OF_MONTH);
        int pHour = calendar.get(Calendar.HOUR_OF_DAY);
        int pMinute = calendar.get(Calendar.MINUTE);
        btn_selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                datePickerDialog = new DatePickerDialog(SubActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month + 1;
                        String date = year + "/" + month + "/" + day;
                        editTextDate.setText(date);
                    }
                }, pYear, pMonth, pDay);
                datePickerDialog.show();
            }
        });
        btn_selectTime.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                timePickerDialog = new TimePickerDialog(SubActivity.this, android.R.style.Theme_Holo_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {

                    }
                }, pHour, pMinute, false);
                timePickerDialog.show();
            }
        });

//        btn_OK.setOnClickListener(new View.OnClickListener() {
//             @Override
//             public void onClick(View view) {
//                 addDBfile(editText2.getText().toString(), editText3.getText().toString(),
//                         editText4.getText().toString(), editText5.getText().toString(),
//                         year, month+1, day, hour, minute);
//                 finish();
//             }
//        });
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

    public void processDatePickerResult(int year, int month, int day) {
        String month_string = Integer.toString(month+1);
        String day_string = Integer.toString(day);
        String year_string = Integer.toString(year);
        String dateMessage = (month_string + "/" + day_string + "/" + year_string);

        Toast.makeText(this, "Date: " + dateMessage, Toast.LENGTH_SHORT).show();
    }
}
