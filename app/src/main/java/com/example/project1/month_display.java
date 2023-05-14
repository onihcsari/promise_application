package com.example.project1;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class month_display extends AppCompatActivity {

    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    public DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    private AlarmManager alarmManager;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> Arrayevents = new ArrayList<>();
    public FirebaseAuth mAuth;
    public EditText editTextUID;
    private List<DBfile> allEvents = new ArrayList<>();
    private MaterialCalendarView calendarView;
    private LocalDate selectedDate;

    List<DBfile> events = new ArrayList<>();
    long notificationTime = System.currentTimeMillis() + (10 * 60 * 1000); // 10분 후의 시간을 밀리초로 계산


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub1);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();
        calendarView = findViewById(R.id.calendarView);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        listView = findViewById(R.id.listView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Arrayevents);
        listView.setAdapter(adapter);

        editTextUID = findViewById(R.id.editTextUID);
        editTextUID.setText("UID: " + uid);

        calendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

//        valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    // 데이터베이스에서 날짜 및 시간 값을 가져옴
//                    String dateString = snapshot.child("date").getValue(String.class);
//                    String timeString = snapshot.child("time").getValue(String.class);
//
//                    // 날짜와 시간 값을 이용하여 알림 예약 작업 설정
//                    scheduleNotification(dateString, timeString);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // 처리 중 오류 발생 시 처리 로직
//            }
//        };

        // fetchEventsFromFirebase();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (!notificationManager.areNotificationsEnabled()) {
            Toast.makeText(month_display.this, "알림 설정을 허용해야 합니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName())
                    .putExtra(Settings.EXTRA_CHANNEL_ID, "Your channel ID");
            startActivity(intent);
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_menu);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if(id==R.id.menu_plus){
                    Intent intent = new Intent(month_display.this, SubActivity.class);
                    startActivity(intent);
                }
                    return true;
                // 아이템 선택에 대한 동작 구현
            }
        });

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                // Get the selected date
                Date date1 = date.getDate();
                LocalDate localDate = date.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String dateString = sdf.format(date1);

                fetchEventsFromFirebase(dateString);
                // Get events on the selected date

                // List<DBfile> eventsOnDate = getEventsOnDate(dateString);
                showEvents(events);

                // Highlight the selected date
                calendarView.setDateSelected(date, true);
            }
        });

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                // 선택된 약속에 대한 상세 정보를 표시하는 액티비티로 이동
//                String selectedEvent = events.get(position);
//                Intent intent = new Intent(month_display.this, EventDetailActivity.class);
//                intent.putExtra("event", selectedEvent);
//                startActivity(intent);
//            }
//        });
    }

//    protected void onDestroy() {
//        super.onDestroy();
//
//        // ValueEventListener 제거
//        databaseReference.removeEventListener(valueEventListener);
//    }

//    private void scheduleNotification(String dateString, String timeString) {
//        try {
//            // 날짜와 시간을 파싱하여 Calendar 객체에 설정
//            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
//            Date date = dateFormat.parse(dateString + " " + timeString);
//
//            if (date != null) {
//                // 푸시 알림을 보낼 시간을 3시간 전으로 설정
//                Calendar notificationTime = Calendar.getInstance();
//                notificationTime.setTime(date);
//                notificationTime.add(Calendar.HOUR_OF_DAY, -3);
//
//                // 알림을 보낼 때 실행될 BroadcastReceiver를 설정
//                Intent intent = new Intent(this, MyBroadcastReceiver.class);
//                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//                // AlarmManager를 사용하여 알림 예약 작업 설정
//                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//                if (alarmManager != null) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, notificationTime.getTimeInMillis(), pendingIntent);
//                    } else {
//                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, notificationTime.getTimeInMillis(), pendingIntent);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public List<DBfile> getEventsOnDate(String localDate) {
        List<DBfile> events = new ArrayList<>();
        String uid = mAuth.getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(uid);
        // Realtime Database에서 선택한 날짜에 해당하는 약속 정보 가져오기
        ref.orderByChild("date").equalTo(localDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                events.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String eventKey = snapshot.getKey();
                    String date = snapshot.child("date").getValue(String.class);
                    String time = snapshot.child("time").getValue(String.class);
                    String title = snapshot.child("title").getValue(String.class);
                    String location = snapshot.child("location").getValue(String.class);
                    String category = snapshot.child("category").getValue(String.class);
                    String number = snapshot.child("number").getValue(String.class);
                    DBfile dbfile = new DBfile(date, time, title, location, category, number);
                    events.add(dbfile);
                }

                // 가져온 약속 정보를 이용해서 달력 아래쪽에 약속 정보 보여주는 메소드 호출
                showEvents(events);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }

        });

        return events;
    }
    private void showEvents(List<DBfile> events) {
        if (events.size() > 0) {
            List<String> eventStrings = new ArrayList<>();
            int count = 0;
            for (DBfile event : events) {
                count++;
                String eventString = "약속 " + count + "\n"
                        + "시간: " + event.getTime() + " "
                        + "장소: " + event.getLocation();
                eventStrings.add(eventString);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, eventStrings);
            listView.setAdapter(adapter);
        } else {
            // 약속이 없을 경우 빈 리스트를 보여줍니다.
            List<String> emptyList = new ArrayList<>();
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, emptyList);
            listView.setAdapter(adapter);
        }
    }

    private void fetchEventsFromFirebase(String dateString) {
        databaseReference.child("DB").child(mAuth.getCurrentUser().getUid()).orderByChild("date").equalTo(dateString).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clear the list of events
                events.clear();

                // Iterate through each child node
                for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                    // Convert the snapshot to a DBfile object
                    DBfile event = eventSnapshot.getValue(DBfile.class);

                    // Add the event to the list
                    events.add(event);
                }

                // Show the events on the screen
                showEvents(events);

                // Schedule notifications for the fetched events
                // scheduleNotification(events);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }

//    private void scheduleNotification(List<DBfile> events) {
//        // 예약된 알림을 모두 취소
//        cancelAllNotifications();
//
//        // 예약된 시간 전에 알림 예약
//        for (DBfile event : events) {
//            // 예약 시간 가져오기
//            String eventDateTime = event.getDate() + " " + event.getTime();
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
//            try {
//                Date eventDate = sdf.parse(eventDateTime);
//                if (eventDate != null) {
//                    // 예약 시간에서 일정 시간 전으로 설정 (예: 10분 전)
//                    Calendar cal = Calendar.getInstance();
//                    cal.setTime(eventDate);
//                    cal.add(Calendar.MINUTE, -10); // 예약하고자 하는 시간의 일정 분 전으로 설정
//
//                    // 알림 예약
//                    scheduleNotification(cal.getTime(), event.getTitle(), event.getLocation());
//                }
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private void scheduleNotification(Date notificationTime, String title, String location) {
//        Intent intent = new Intent(this, MyBroadcastReceiver.class);
//        intent.putExtra("title", title);
//        intent.putExtra("message", location);
//
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTime.getTime(), pendingIntent);
//    }
//
//    private void cancelAllNotifications() {
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(this, MyBroadcastReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
//        if (pendingIntent != null) {
//            alarmManager.cancel(pendingIntent);
//            pendingIntent.cancel();
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private List<DBfile> parseData(DataSnapshot dataSnapshot) {
        List<DBfile> events = new ArrayList<>();

        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            DBfile event = new DBfile();

            String date = snapshot.child("date").getValue(String.class);
            String time = snapshot.child("time").getValue(String.class);
            String title = snapshot.child("title").getValue(String.class);
            String location = snapshot.child("location").getValue(String.class);
            String category = snapshot.child("category").getValue(String.class);
            String number = snapshot.child("number").getValue(String.class);

            event.setDate(date);
            event.setTime(time);
            event.setTitle(title);
            event.setLocation(location);
            event.setCategory(category);
            event.setNumber(number);

            events.add(event);
        }

        return events;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id==R.id.menu_week){
            calendarView.state().edit()
                    .setCalendarDisplayMode(CalendarMode.WEEKS)
                    .commit();
            return true;
        } else if(id==R.id.menu_month){
            calendarView.state().edit()
                    .setCalendarDisplayMode(CalendarMode.MONTHS)
                    .commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
