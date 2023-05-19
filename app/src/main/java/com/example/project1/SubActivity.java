package com.example.project1;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class SubActivity extends AppCompatActivity {

    public Button btn_selectDate, btn_selectTime, btn_OK, btn_map, btn_check;
    public EditText editTextDate, editTextTime, editTextTitle, editTextLocation, editTextCategory, editTextNumber, editTextUID;
    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    public DatabaseReference databaseReference = database.getReference();
    public TimePickerDialog timePickerDialog;
    public DatePickerDialog datePickerDialog;
    private AlarmManager alarmManager;
    private LinearLayout layoutUidContainer;
    private List<EditText> uidEditTextList = new ArrayList<>(); // UID 입력 필드 리스트
    public FirebaseAuth mAuth;





    private ActivityResultLauncher<Intent> mapActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub2);

        Intent intent = getIntent();

        btn_map = findViewById(R.id.btn_map);
        btn_selectDate = findViewById(R.id.btn_selectDate);
        btn_selectTime = findViewById(R.id.btn_selectTime);
        btn_check = findViewById(R.id.btn_check);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextTime = findViewById(R.id.editTextTime);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextCategory = findViewById(R.id.editTextCategory);
        editTextNumber = findViewById(R.id.editTextNumber);
        editTextDate = findViewById(R.id.editTextDate);
        // editTextUID = findViewById(R.id.editTextUID);
        btn_OK = findViewById(R.id.button_OK);
        layoutUidContainer = findViewById(R.id.layout_uid_container);

        DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        String myUID = mAuth.getCurrentUser().getUid();

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

        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int uidCount = Integer.parseInt(editTextNumber.getText().toString());
                updateUidFields(uidCount);
                EditText uidEditText = new EditText(SubActivity.this);
                uidEditText.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));
                uidEditText.setText(myUID);
                uidEditText.setEnabled(false);
                layoutUidContainer.addView(uidEditText);
                uidEditTextList.add(uidEditText);
            }
        });

        btn_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> uidList = new ArrayList<>();
                for (int i = 0; i < uidEditTextList.size(); i++) {
                    uidList.add(uidEditTextList.get(i).getText().toString());
                }
                for (int i = 0; i < uidEditTextList.size(); i++) {
                    String uid = uidList.get(i);
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("DB").child(uid);
                    DBfile dbfile = new DBfile(editTextDate.getText().toString(), editTextTime.getText().toString(),
                            editTextTitle.getText().toString(), editTextLocation.getText().toString(),
                            editTextCategory.getText().toString(), editTextNumber.getText().toString(), uidList);
                    String eventKey = ref.push().getKey();
                    if (eventKey != null) {
                        ref.child(eventKey).child("date").setValue(dbfile.getDate());
                        ref.child(eventKey).child("time").setValue(dbfile.getTime());
                        ref.child(eventKey).child("title").setValue(dbfile.getTitle());
                        ref.child(eventKey).child("location").setValue(dbfile.getLocation());
                        ref.child(eventKey).child("category").setValue(dbfile.getCategory());
                        ref.child(eventKey).child("number").setValue(dbfile.getNumber());
                        ref.child(eventKey).child("uid").setValue(dbfile.getUid());
                    }
                }
                Context context = SubActivity.this;
                // 알림 예약을 위한 PendingIntent 생성

                Intent notificationIntent1 = new Intent(context, MyBroadcastReceiver.class);
                notificationIntent1.putExtra("title", "약속 알림");
                notificationIntent1.putExtra("message", "24시간 후에 약속이 있습니다.");
                PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, 0, notificationIntent1, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                Intent notificationIntent2 = new Intent(context, MyBroadcastReceiver.class);
                notificationIntent2.putExtra("title", "약속 알림");
                notificationIntent2.putExtra("message", "3시간 후에 약속이 있습니다.");
                PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, 1, notificationIntent2, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                // editTextDate에서 날짜 값 가져오기
                String appointmentDate = editTextDate.getText().toString();

                // editTextTime에서 시간 값 가져오기
                String appointmentTime = editTextTime.getText().toString();

                appointmentTime = appointmentTime.replaceAll("[^0-9:]", "");
                appointmentTime = appointmentTime.substring(0, 2) + ":" + appointmentTime.substring(2);

                Calendar yourAppointmentTime = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                Date appointmentDateTime = null;
                try {
                    appointmentDateTime = sdf.parse(appointmentDate + " " + appointmentTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (appointmentDateTime != null) {
                    yourAppointmentTime.setTime(appointmentDateTime);

                    // 약속 시간으로부터 1일 전의 시간 계산
                    Calendar notificationTime1 = Calendar.getInstance();
                    notificationTime1.setTime(yourAppointmentTime.getTime()); // 약속 시간 설정
                    notificationTime1.add(Calendar.DAY_OF_MONTH, -1); // 1일 전으로 설정

                    // 약속 시간으로부터 3시간 전의 시간 계산
                    Calendar notificationTime2 = Calendar.getInstance();
                    notificationTime2.setTime(yourAppointmentTime.getTime()); // 약속 시간 설정
                    notificationTime2.add(Calendar.HOUR_OF_DAY, -3); // 3시간 전으로 설정

                    // AlarmManager를 사용하여 알림 예약
                    alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    if (alarmManager != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, notificationTime1.getTimeInMillis(), pendingIntent1);
                            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, notificationTime2.getTimeInMillis(), pendingIntent2);
                        } else {
                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, notificationTime1.getTimeInMillis(), pendingIntent1);
                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, notificationTime2.getTimeInMillis(), pendingIntent2);
                        }
                    }
                    finish();
                }
            }
        });
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

    private void updateUidFields(int uidCount) {
        layoutUidContainer.removeAllViews();

        for (int i = 0; i < uidCount-1; i++) {
            EditText editTextUid = new EditText(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 10, 0, 0);
            editTextUid.setLayoutParams(layoutParams);
            editTextUid.setHint("UID");
            editTextUid.setInputType(InputType.TYPE_CLASS_TEXT);
            layoutUidContainer.addView(editTextUid);
            uidEditTextList.add(editTextUid);
        }
    }

}