package com.example.project1;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class SubActivity extends AppCompatActivity {

    public Button btn_selectDate, btn_selectTime, btn_OK, btn_map, btn_share;
    public EditText editTextDate, editTextTime, editTextTitle, editTextLocation, editTextCategory, editTextNumber, editTextUID;
    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    public DatabaseReference databaseReference = database.getReference();
    public TimePickerDialog timePickerDialog;
    public DatePickerDialog datePickerDialog;

    private ActivityResultLauncher<Intent> mapActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub2);

        Intent intent = getIntent();
        ArrayList<Integer> data = (ArrayList<Integer>) intent.getSerializableExtra("input");

        btn_map = findViewById(R.id.btn_map);
        btn_selectDate = findViewById(R.id.btn_selectDate);
        btn_selectTime = findViewById(R.id.btn_selectTime);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextTime = findViewById(R.id.editTextTime);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextCategory = findViewById(R.id.editTextCategory);
        editTextNumber = findViewById(R.id.editTextNumber);
        editTextDate = findViewById(R.id.editTextDate);
        editTextUID = findViewById(R.id.editTextUID);
        btn_OK = findViewById(R.id.button_OK);
        btn_share = findViewById(R.id.btn_share);
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        mapActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        String address = result.getData().getStringExtra("address");
                        EditText editText = findViewById(R.id.editTextLocation);
                        editText.setText(address);
                    }
                });

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
                        String str_month = "", str_day = "";
                        month = month + 1;
                        if (month < 10) {
                            str_month = "0" + month;
                        } else {
                            str_month = String.valueOf(month);
                        }
                        if (day < 10) {
                            str_day = "0" + day;
                        } else {
                            str_day = String.valueOf(day);
                        }
                        String date = year + "-" + str_month + "-" + str_day;
                        editTextDate.setText(date);
                    }
                }, pYear, pMonth, pDay);
                datePickerDialog.show();
            }
        });
        btn_selectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialog = new TimePickerDialog(SubActivity.this, android.R.style.Theme_Holo_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        String time = hour + "시 " + minute + "분";
                        editTextTime.setText(time);
                    }
                }, pHour, pMinute, false);
                timePickerDialog.show();
            }
        });
        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SubActivity.this, MapActivity.class);
                mapActivityResultLauncher.launch(intent);
            }
        });

        btn_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDBfile(editTextDate.getText().toString(), editTextTime.getText().toString(),
                        editTextTitle.getText().toString(), editTextLocation.getText().toString(),
                        editTextCategory.getText().toString(), editTextNumber.getText().toString());
                finish();
            }
        });

        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = editTextUID.getText().toString();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("DB").child(uid);
                DBfile dbfile = new DBfile(editTextDate.getText().toString(), editTextTime.getText().toString(),
                        editTextTitle.getText().toString(), editTextLocation.getText().toString(),
                        editTextCategory.getText().toString(), editTextNumber.getText().toString());
                String eventKey = ref.push().getKey();
                if (eventKey != null) {
                    ref.child(eventKey).child("date").setValue(dbfile.getDate());
                    ref.child(eventKey).child("time").setValue(dbfile.getTime());
                    ref.child(eventKey).child("title").setValue(dbfile.getTitle());
                    ref.child(eventKey).child("location").setValue(dbfile.getLocation());
                    ref.child(eventKey).child("category").setValue(dbfile.getCategory());
                    ref.child(eventKey).child("number").setValue(dbfile.getNumber());
                }
                editTextUID.setText("");
            }
        });
    }
    public void addDBfile(String Date, String Time, String Title, String Location, String Category, String Number) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = databaseRef.child("DB").child(userId);

            DBfile DBfile = new DBfile(Date, Time, Title, Location, Category, Number);
            String eventKey = userRef.push().getKey();

            if (eventKey != null) {
                userRef.child(eventKey).child("date").setValue(DBfile.getDate());
                userRef.child(eventKey).child("time").setValue(DBfile.getTime());
                userRef.child(eventKey).child("title").setValue(DBfile.getTitle());
                userRef.child(eventKey).child("location").setValue(DBfile.getLocation());
                userRef.child(eventKey).child("category").setValue(DBfile.getCategory());
                userRef.child(eventKey).child("number").setValue(DBfile.getNumber());
            } else {
                Log.w(TAG, "Error generating event key");
            }
        } else {
            Log.w(TAG, "No current user");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            String address = data.getStringExtra("address");
            EditText editText = findViewById(R.id.editTextLocation);
            editText.setText(address);
        }
    }


    public void processDatePickerResult(int year, int month, int day) {
        String month_string = Integer.toString(month + 1);
        String day_string = Integer.toString(day);
        String year_string = Integer.toString(year);
        String dateMessage = (month_string + "/" + day_string + "/" + year_string);

        Toast.makeText(this, "Date: " + dateMessage, Toast.LENGTH_SHORT).show();
    }
}